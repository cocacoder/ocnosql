/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ailk.oci.ocnosql.tools.load.csvbulkload;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.phoenix.jdbc.PhoenixConnection;
import org.apache.phoenix.jdbc.PhoenixDriver;
import org.apache.phoenix.mapreduce.ImportPreUpsertKeyValueProcessor;
import org.apache.phoenix.util.CSVCommonsLoader;
import org.apache.phoenix.util.ColumnInfo;
import org.apache.phoenix.util.PhoenixRuntime;
import org.apache.phoenix.util.csv.CsvUpsertExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ailk.oci.ocnosql.common.rowkeygenerator.MD5RowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * MapReduce mapper that converts CSV input lines into KeyValues that can be
 * written to HFiles.
 * <p/>
 * KeyValues are produced by executing UPSERT statements on a Phoenix connection
 * and then extracting the created KeyValues and rolling back the statement
 * execution before it is committed to HBase.
 */
public class PhoenixCsvToKeyValueMapper extends
		Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue> {

	private static final Logger LOG = LoggerFactory
			.getLogger(PhoenixCsvToKeyValueMapper.class);

	private static final String COUNTER_GROUP_NAME = "Phoenix MapReduce Import";

	/**
	 * Configuration key for the class name of an
	 * ImportPreUpsertKeyValueProcessor
	 */
	public static final String UPSERT_HOOK_CLASS_CONFKEY = "phoenix.mapreduce.import.kvprocessor";

	/** Configuration key for the field delimiter for input csv records */
	public static final String FIELD_DELIMITER_CONFKEY = "phoenix.mapreduce.import.fielddelimiter";

	/** Configuration key for the array element delimiter for input arrays */
	public static final String ARRAY_DELIMITER_CONFKEY = "phoenix.mapreduce.import.arraydelimiter";

	/** Configuration key for the name of the output table */
	public static final String TABLE_NAME_CONFKEY = "phoenix.mapreduce.import.tablename";

	/** Configuration key for the columns to be imported */
	public static final String COLUMN_INFO_CONFKEY = "phoenix.mapreduce.import.columninfos";

	/** Configuration key for the flag to ignore invalid rows */
	public static final String IGNORE_INVALID_ROW_CONFKEY = "phoenix.mapreduce.import.ignoreinvalidrow";

	// 增加的参数
	/**
	 * rowkey前缀用那些列的值做散列
	 */
	public static final String ROW_PREFIX_COLUMNS = "phoenix.mapreduce.import.rowprefixcolumns";
	/**
	 * rowkey前缀的散列算法,默认md5
	 */
	public static final String ROW_PREFIX_ALG = "phoenix.mapreduce.import.rowprefixalg";
	/**
	 * rowkey主体部分包含哪些列(中间部分)
	 */
	public static final String ROW_COLUMNS = "phoenix.mapreduce.import.rowcolumns";
	/**
	 * 当前表的唯一索引列(可以是多列),主要用来生成rowkey后缀,默认所有的列
	 */
	public static final String UNIQUE_INDEX_COLUMNS = "phoenix.mapreduce.import.uniqueindexcolumns";

	private PhoenixConnection conn;
	private CsvUpsertExecutor csvUpsertExecutor;
	private MapperUpsertListener upsertListener;
	private CsvLineParser csvLineParser;
	private ImportPreUpsertKeyValueProcessor preUpdateProcessor;
	private RowKeyGenerator rowKeyGenerator;
	/**
	 * csv文件分隔符
	 */
	private String separator;
	/**
	 * rowkey前缀hash所涉及列的索引
	 */
	private List<Integer> rowPrefixColIdxs;
	/**
	 * rowkey主体部分所涉及列的索引
	 */
	private List<Integer> rowColIdxs;
	/**
	 * 唯一索引字段的索引
	 */
	private List<Integer> unqIdxColIdxs;

	// 生成rowkey所使用的临时变量
	private StringBuilder rowGentemp;

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {

		Configuration conf = context.getConfiguration();
		String jdbcUrl = getJdbcUrl(conf);

		// This statement also ensures that the driver class is loaded
		LOG.info("Connection with driver {} with url {}",
				PhoenixDriver.class.getName(), jdbcUrl);

		try {
			conn = (PhoenixConnection) DriverManager.getConnection(jdbcUrl);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		upsertListener = new MapperUpsertListener(context, conf.getBoolean(
				IGNORE_INVALID_ROW_CONFKEY, true));
		csvUpsertExecutor = buildUpsertExecutor(conf);
		csvLineParser = new CsvLineParser(conf.get(FIELD_DELIMITER_CONFKEY)
				.charAt(0));

		preUpdateProcessor = loadPreUpsertProcessor(conf);

		// 改造的代码
		// 要导入的列
		List<String> importColumnList = new ArrayList<String>();
		for (ColumnInfo colInfo : buildColumnInfoList(conf)) {
			importColumnList.add(colInfo.getColumnName());
		}
		// 前缀hash所涉及的列
		List<String> rowPrefixColumns = Lists.newArrayList(Splitter.on(",")
				.trimResults().split(conf.get(ROW_PREFIX_COLUMNS)));
		// rowkey前缀hash所涉及列的索引
		rowPrefixColIdxs = new ArrayList<Integer>();
		for (String rpCol : rowPrefixColumns) {
			// 这里必须减1，因为csv文件会比要导入的列少一列，因为主键列是自动生成的
			rowPrefixColIdxs.add(importColumnList.indexOf(rpCol) - 1);
		}

		// rowkey主体所需的列
		List<String> rowColumns = Lists.newArrayList(Splitter.on(",")
				.trimResults().split(conf.get(ROW_COLUMNS)));
		rowColIdxs = new ArrayList<Integer>();
		for (String rCol : rowColumns) {
			// 这里必须减1，因为csv文件会比要导入的列少一列，因为主键列是自动生成的
			rowColIdxs.add(importColumnList.indexOf(rCol) - 1);
		}

		// 唯一索引列
		List<String> uniqueIndexColumns = Lists.newArrayList(Splitter.on(",")
				.trimResults()
				.split(conf.get(UNIQUE_INDEX_COLUMNS, "_allColumns")));
		if (uniqueIndexColumns.size() == 1
				&& uniqueIndexColumns.get(0).equals("_allColumns")) {
			unqIdxColIdxs = null;
		} else {
			unqIdxColIdxs = new ArrayList<Integer>();
			for (String rCol : uniqueIndexColumns) {
				// 这里必须减1，因为csv文件会比要导入的列少一列，因为主键列是自动生成的
				unqIdxColIdxs.add(importColumnList.indexOf(rCol) - 1);
			}
		}

		// 根据命令行指定的rowkey前缀生成策略创建对应的生成器(默认md5)
		rowKeyGenerator = buildRowKeyGenerator(conf.get(ROW_PREFIX_ALG, "md5"));
		separator = conf.get(FIELD_DELIMITER_CONFKEY);

		// 临时变量
		rowGentemp = new StringBuilder();
	}

	/**
	 * 根据指定的rowkey前缀生成策略创建对应的生成器(默认md5)
	 * 
	 * @param string
	 * @return
	 */
	private RowKeyGenerator buildRowKeyGenerator(String alg) {
		if (null == alg || alg.equals("") || alg.equals("md5")) {
			return new MD5RowKeyGenerator();
		} else {
			throw new RuntimeException(
					"Temporarily does not support the other algorithms");
		}
	}

	/**
	 * 解析一行csv记录，根据配置生成对应的rowkey(hash前缀+主体+唯一索引md5后缀)
	 */
	private String generateRowKey(String lineStr) {
		String rowkey = "";
		// 1.生成前缀(根据前缀字段的索引)
		String[] lineArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(
				lineStr, separator);
		rowGentemp.delete(0, rowGentemp.length());
		for (int idx : rowPrefixColIdxs) {
			rowGentemp.append(lineArr[idx]);
		}
		rowkey += rowKeyGenerator.generatePrefix(rowGentemp.toString());
		// 2.生成主体(根据主体字段的索引)
		rowGentemp.delete(0, rowGentemp.length());
		for (int idx : rowColIdxs) {
			rowGentemp.append(lineArr[idx]);
		}
		rowkey += rowGentemp.toString();
		// 3.生成后缀(分两种情况,a.指定了唯一索引列，b.没指定唯一索引列)

		if (null != unqIdxColIdxs) {// a.指定了唯一索引列
			rowGentemp.delete(0, rowGentemp.length());
			for (int idx : unqIdxColIdxs) {
				rowGentemp.append(lineArr[idx]);
			}
			rowkey += getUniquePostfix(rowGentemp.toString());
		} else {// b.没指定唯一索引列,使用所有列的值作为唯一索引
			rowkey += getUniquePostfix(lineStr);
		}
		// 4.返回
		return rowkey;
	}

	/**
	 * 获取16位唯一索引后缀
	 */
	private String getUniquePostfix(String plainText) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String lineStr = value.toString();
		// 如果不是空行
		if (null != lineStr && lineStr.length() > 1) {
			// 生成rowkey(hash前缀+主体+唯一索引md5后缀)，并拼接在当前这一行数据的最前面，用分隔符隔开
			lineStr = generateRowKey(lineStr) + separator + lineStr;
		}
		ImmutableBytesWritable outputKey = new ImmutableBytesWritable();
		try {
			CSVRecord csvRecord = null;
			try {
				csvRecord = csvLineParser.parse(lineStr);
			} catch (IOException e) {
				context.getCounter(COUNTER_GROUP_NAME, "CSV Parser errors")
						.increment(1L);
			}

			if (csvRecord == null) {
				context.getCounter(COUNTER_GROUP_NAME, "Empty records")
						.increment(1L);
				return;
			}
			csvUpsertExecutor.execute(ImmutableList.of(csvRecord));

			Iterator<Pair<byte[], List<KeyValue>>> uncommittedDataIterator = PhoenixRuntime
					.getUncommittedDataIterator(conn);
			while (uncommittedDataIterator.hasNext()) {
				Pair<byte[], List<KeyValue>> kvPair = uncommittedDataIterator
						.next();
				List<KeyValue> keyValueList = kvPair.getSecond();
				keyValueList = preUpdateProcessor.preUpsert(kvPair.getFirst(),
						keyValueList);
				for (KeyValue kv : keyValueList) {
					outputKey.set(kv.getBuffer(), kv.getRowOffset(),
							kv.getRowLength());
					context.write(outputKey, kv);
				}
			}
			conn.rollback();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load the configured ImportPreUpsertKeyValueProcessor, or supply a dummy
	 * processor.
	 */
	@VisibleForTesting
	static ImportPreUpsertKeyValueProcessor loadPreUpsertProcessor(
			Configuration conf) {
		Class<? extends ImportPreUpsertKeyValueProcessor> processorClass = null;
		try {
			processorClass = conf.getClass(UPSERT_HOOK_CLASS_CONFKEY,
					DefaultImportPreUpsertKeyValueProcessor.class,
					ImportPreUpsertKeyValueProcessor.class);
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't load upsert hook class",
					e);
		}

		return ReflectionUtils.newInstance(processorClass, conf);
	}

	/**
	 * Build up the JDBC URL for connecting to Phoenix.
	 * 
	 * @return the full JDBC URL for a Phoenix connection
	 */
	@VisibleForTesting
	static String getJdbcUrl(Configuration conf) {
		String zkQuorum = conf.get("hbase.zookeeper.quorum") + ":"
				+ conf.get("hbase.zookeeper.property.clientPort");
		if (zkQuorum == null) {
			throw new IllegalStateException(HConstants.ZOOKEEPER_QUORUM
					+ " is not configured");
		}
		return PhoenixRuntime.JDBC_PROTOCOL
				+ PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + zkQuorum;
	}

	@VisibleForTesting
	CsvUpsertExecutor buildUpsertExecutor(Configuration conf) {
		String tableName = conf.get(TABLE_NAME_CONFKEY);
		String arraySeparator = conf.get(ARRAY_DELIMITER_CONFKEY,
				CSVCommonsLoader.DEFAULT_ARRAY_ELEMENT_SEPARATOR);
		Preconditions.checkNotNull(tableName, "table name is not configured");

		List<ColumnInfo> columnInfoList = buildColumnInfoList(conf);

		return CsvUpsertExecutor.create(conn, tableName, columnInfoList,
				upsertListener, arraySeparator);
	}

	/**
	 * Write the list of to-import columns to a job configuration.
	 * 
	 * @param conf
	 *            configuration to be written to
	 * @param columnInfoList
	 *            list of ColumnInfo objects to be configured for import
	 */
	@VisibleForTesting
	static void configureColumnInfoList(Configuration conf,
			List<ColumnInfo> columnInfoList) {
		conf.set(COLUMN_INFO_CONFKEY,
				Joiner.on("|").useForNull("").join(columnInfoList));
	}

	/**
	 * Build the list of ColumnInfos for the import based on information in the
	 * configuration.
	 */
	@VisibleForTesting
	static List<ColumnInfo> buildColumnInfoList(Configuration conf) {
		return Lists.newArrayList(Iterables.transform(
				Splitter.on("|").split(conf.get(COLUMN_INFO_CONFKEY)),
				new Function<String, ColumnInfo>() {
					@Nullable
					@Override
					public ColumnInfo apply(@Nullable String input) {
						if (input.isEmpty()) {
							// An empty string represents a null that was passed
							// in to
							// the configuration, which corresponds to an input
							// column
							// which is to be skipped
							return null;
						}
						return ColumnInfo.fromString(input);
					}
				}));
	}

	/**
	 * Listener that logs successful upserts and errors to job counters.
	 */
	@VisibleForTesting
	static class MapperUpsertListener implements
			CsvUpsertExecutor.UpsertListener {

		private final Context context;
		private final boolean ignoreRecordErrors;

		private MapperUpsertListener(Context context, boolean ignoreRecordErrors) {
			this.context = context;
			this.ignoreRecordErrors = ignoreRecordErrors;
		}

		@Override
		public void upsertDone(long upsertCount) {
			context.getCounter(COUNTER_GROUP_NAME, "Upserts Done")
					.increment(1L);
		}

		@Override
		public void errorOnRecord(CSVRecord csvRecord, String errorMessage) {
			LOG.error("Error on record {}: {}", csvRecord, errorMessage);
			context.getCounter(COUNTER_GROUP_NAME, "Errors on records")
					.increment(1L);
			if (!ignoreRecordErrors) {
				throw new RuntimeException("Error on record, " + errorMessage
						+ ", " + "record =" + csvRecord);
			}
		}
	}

	/**
	 * Parses a single CSV input line, returning a {@code CSVRecord}.
	 */
	@VisibleForTesting
	static class CsvLineParser {

		private final CSVFormat csvFormat;

		CsvLineParser(char fieldDelimiter) {
			this.csvFormat = CSVFormat.newFormat(fieldDelimiter);
		}

		public CSVRecord parse(String input) throws IOException {
			// TODO Creating a new parser for each line seems terribly
			// inefficient but
			// there's no public way to parse single lines via commons-csv. We
			// should update
			// it to create a LineParser class like this one.
			CSVParser csvParser = new CSVParser(new StringReader(input),
					csvFormat);
			return Iterables.getFirst(csvParser, null);
		}
	}

	/**
	 * A default implementation of {@code ImportPreUpsertKeyValueProcessor} that
	 * is used if no specific class is configured. This implementation simply
	 * passes through the KeyValue list that is passed in.
	 */
	public static class DefaultImportPreUpsertKeyValueProcessor implements
			ImportPreUpsertKeyValueProcessor {

		@Override
		public List<KeyValue> preUpsert(byte[] rowKey, List<KeyValue> keyValues) {
			return keyValues;
		}
	}
}
