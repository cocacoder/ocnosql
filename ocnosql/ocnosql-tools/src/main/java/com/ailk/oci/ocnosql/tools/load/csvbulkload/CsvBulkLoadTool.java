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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.phoenix.jdbc.PhoenixDatabaseMetaData;
import org.apache.phoenix.query.QueryConstants;
import org.apache.phoenix.util.CSVCommonsLoader;
import org.apache.phoenix.util.ColumnInfo;
import org.apache.phoenix.util.PhoenixRuntime;
import org.apache.phoenix.util.SchemaUtil;
import org.apache.phoenix.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
/**
 * Base tool for running MapReduce-based ingests of data.
 */
@SuppressWarnings("deprecation")
public class CsvBulkLoadTool extends Configured implements Tool {
	public final static String NAME = "csvBulkLoad";

    private static final Logger LOG = LoggerFactory.getLogger(CsvBulkLoadTool.class);

    //static final Option ZK_QUORUM_OPT = new Option("z", "zookeeper", true, "Zookeeper quorum to connect to (optional)");
    static final Option INPUT_PATH_OPT = new Option("i", "input", true, "Input CSV path (mandatory)");
    static final Option OUTPUT_PATH_OPT = new Option("o", "output", true, "Output path for temporary HFiles (optional)");
    static final Option SCHEMA_NAME_OPT = new Option("s", "schema", true, "Phoenix schema name (optional)");
    static final Option TABLE_NAME_OPT = new Option("t", "table", true, "Phoenix table name (mandatory)");
    static final Option DELIMITER_OPT = new Option("d", "delimiter", true, "Input delimiter, defaults to comma");
    static final Option ARRAY_DELIMITER_OPT = new Option("a", "array-delimiter", true, "Array element delimiter (optional)");
    static final Option IMPORT_COLUMNS_OPT = new Option("c", "import-columns", true, "Comma-separated list of columns to be imported");
    static final Option IGNORE_ERRORS_OPT = new Option("g", "ignore-errors", false, "Ignore input errors");
    static final Option HELP_OPT = new Option("h", "help", false, "Show this help and quit");
    
    //自己加的参数
    /**
     * rowkey前缀用那些列的值做散列
     */
    static final Option ROW_PREFIX_COLUMNS_OPT = new Option("rpc", "row-prefix-columns", true, "Comma-separated list of columns to be generate rowkey prefix  (mandatory)");
    /**
     * rowkey前缀的散列算法,默认md5
     */
    static final Option ROW_PREFIX_ALG_OPT = new Option("rpa", "row-prefix-alg", true, "rowkey prefix generating algorithm,default md5 (optional)");
    /**
     * rowkey主体部分包含哪些列(中间部分)
     */
    static final Option ROW_COLUMNS_OPT = new Option("rc", "row-columns", true, "Comma-separated list of columns to be assemble the main part of rowkey (mandatory)");
    /**
     * 当前表的唯一索引列(可以是多列),主要用来生成rowkey后缀,默认所有的列
     */
    static final Option UNIQUE_INDEX_COLUMNS_OPT = new Option("u", "unique-index-columns", true, "Comma-separated list of unique columns to be generate rowkey postfix(optional)");

    /**
     * 特别注意：
     *  1.-c 列明要注意大小写，不要列族名，主键也得有，必须在最前面;
     *  2. 如果已经用phoenix建表了-c也可不不写，默认就是以建表语句字段的顺序来导入的
     *  3.-i 可以指定文件或者文件夹
     *  4.-g可以忽略csv文件里的错误记录，不会抛异常,错误记录也不导入，比如缺少列
     *  5.给定csv文件的空值列在hbase里默认不会存空值，干脆那一列都没建，这样能节省空间
     *  6.改造之前，csv文件的列数和-c指定的列数必须相等，否则为错误记录；改造之后，csv文件比-c指定的列少一列，因为主键列的数据是自动生成的
     *  7.所谓的主键就是hbase里的rowkey
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ToolRunner.run(new CsvBulkLoadTool(), args);
    }

    /**
     * Parses the commandline arguments, throws IllegalStateException if mandatory arguments are
     * missing.
     *
     * @param args supplied command line arguments
     * @return the parsed command line
     */
    CommandLine parseOptions(String[] args) {

        Options options = getOptions();

        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException e) {
            printHelpAndExit("Error parsing command line options: " + e.getMessage(), options);
        }

        if (cmdLine.hasOption(HELP_OPT.getOpt())) {
            printHelpAndExit(options, 0);
        }

        if (!cmdLine.hasOption(TABLE_NAME_OPT.getOpt())) {
            throw new IllegalStateException(TABLE_NAME_OPT.getLongOpt() + " is a mandatory " +
                    "parameter");
        }

        /**
        if (!cmdLine.getArgList().isEmpty()) {
            throw new IllegalStateException("Got unexpected extra parameters: "
                    + cmdLine.getArgList());
        }
		*/
        if (!cmdLine.hasOption(INPUT_PATH_OPT.getOpt())) {
            throw new IllegalStateException(INPUT_PATH_OPT.getLongOpt() + " is a mandatory " +
                    "parameter");
        }
        //检查rowkey前缀包含的列是否指定
        if (!cmdLine.hasOption(ROW_PREFIX_COLUMNS_OPT.getOpt())) {
            throw new IllegalStateException(ROW_PREFIX_COLUMNS_OPT.getLongOpt() + " is a mandatory " +
                    "parameter");
        }
        //检查rowkey主体部分所包含的的列是否给出
        if (!cmdLine.hasOption(ROW_COLUMNS_OPT.getOpt())) {
            throw new IllegalStateException(ROW_COLUMNS_OPT.getLongOpt() + " is a mandatory " +
                    "parameter");
        }

        return cmdLine;
    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption(INPUT_PATH_OPT);
        options.addOption(TABLE_NAME_OPT);
        //options.addOption(ZK_QUORUM_OPT);
        options.addOption(OUTPUT_PATH_OPT);
        options.addOption(SCHEMA_NAME_OPT);
        options.addOption(DELIMITER_OPT);
        options.addOption(ARRAY_DELIMITER_OPT);
        options.addOption(IMPORT_COLUMNS_OPT);
        options.addOption(IGNORE_ERRORS_OPT);
        options.addOption(HELP_OPT);
        //增加的选项
        options.addOption(ROW_PREFIX_COLUMNS_OPT);
        options.addOption(ROW_PREFIX_ALG_OPT);
        options.addOption(ROW_COLUMNS_OPT);
        options.addOption(UNIQUE_INDEX_COLUMNS_OPT);
        return options;
    }


    private void printHelpAndExit(String errorMessage, Options options) {
        System.err.println(errorMessage);
        printHelpAndExit(options, 1);
    }

    private void printHelpAndExit(Options options, int exitCode) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("help", options);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {

        HBaseConfiguration.addHbaseResources(getConf());
        Configuration conf=getConf();
        String quorum=conf.get("hbase.zookeeper.quorum");
        String clientPort=conf.get("hbase.zookeeper.property.clientPort");
        LOG.info("hbase.zookeeper.quorum="+quorum);
        LOG.info("hbase.zookeeper.property.clientPort="+clientPort);
        LOG.info("phoenix.query.dateFormat="+conf.get("phoenix.query.dateFormat"));

        CommandLine cmdLine = null;
        try {
            cmdLine = parseOptions(args);
            LOG.info("JdbcUrl="+getJdbcUrl(quorum+":"+clientPort));
        } catch (IllegalStateException e) {
            printHelpAndExit(e.getMessage(), getOptions());
        }
        Class.forName(DriverManager.class.getName());
        Connection conn = DriverManager.getConnection(
                getJdbcUrl(quorum+":"+clientPort));
        String tableName = cmdLine.getOptionValue(TABLE_NAME_OPT.getOpt());
        String schemaName = cmdLine.getOptionValue(SCHEMA_NAME_OPT.getOpt());
        String qualifiedTableName = getQualifiedTableName(schemaName, tableName);
        List<ColumnInfo> importColumns = buildImportColumns(conn, cmdLine, qualifiedTableName);
        
        LOG.info("tableName="+tableName);
        LOG.info("schemaName="+schemaName);
        LOG.info("qualifiedTableName="+qualifiedTableName);
        
        configureOptions(cmdLine, importColumns, getConf());

        try {
            validateTable(conn, schemaName, tableName);
        } finally {
            conn.close();
        }

        Path inputPath = new Path(cmdLine.getOptionValue(INPUT_PATH_OPT.getOpt()));
        Path outputPath = null;
        if (cmdLine.hasOption(OUTPUT_PATH_OPT.getOpt())) {
            outputPath = new Path(cmdLine.getOptionValue(OUTPUT_PATH_OPT.getOpt()));
        } else {
            outputPath = new Path("/tmp/" + UUID.randomUUID());
        }
        LOG.info("Configuring HFile output path to {}", outputPath);

        Job job = new Job(getConf(),
                "Phoenix MapReduce import for "
                        + getConf().get(PhoenixCsvToKeyValueMapper.TABLE_NAME_CONFKEY));

        // Allow overriding the job jar setting by using a -D system property at startup
        if (job.getJar() == null) {
            job.setJarByClass(PhoenixCsvToKeyValueMapper.class);
        }
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, inputPath);

        FileSystem.get(getConf());
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setMapperClass(PhoenixCsvToKeyValueMapper.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(KeyValue.class);

        HTable htable = new HTable(getConf(), qualifiedTableName);

        // Auto configure partitioner and reducer according to the Main Data table
        HFileOutputFormat.configureIncrementalLoad(job, htable);

        LOG.info("Running MapReduce import job from {} to {}", inputPath, outputPath);
        boolean success = job.waitForCompletion(true);
        if (!success) {
            LOG.error("Import job failed, check JobTracker for details");
            return 1;
        }

        LOG.info("Loading HFiles from {}", outputPath);
        LoadIncrementalHFiles loader = new LoadIncrementalHFiles(getConf());
        loader.doBulkLoad(outputPath, htable);
        htable.close();

        LOG.info("Incremental load complete");

        LOG.info("Removing output directory {}", outputPath);
        if (!FileSystem.get(getConf()).delete(outputPath, true)) {
            LOG.error("Removing output directory {} failed", outputPath);
        }

        return 0;
    }

    String getJdbcUrl(String zkQuorum) {
        if (zkQuorum == null) {
            LOG.warn("Defaulting to localhost for ZooKeeper quorum");
            zkQuorum = "localhost:2181";
        }
        return PhoenixRuntime.JDBC_PROTOCOL + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + zkQuorum;
    }

    /**
     * Build up the list of columns to be imported. The list is taken from the command line if
     * present, otherwise it is taken from the table description.
     *
     * @param conn connection to Phoenix
     * @param cmdLine supplied command line options
     * @param qualifiedTableName table name (possibly with schema) of the table to be imported
     * @return the list of columns to be imported
     */
    List<ColumnInfo> buildImportColumns(Connection conn, CommandLine cmdLine,
            String qualifiedTableName) throws SQLException {
        List<String> userSuppliedColumnNames = null;
        if (cmdLine.hasOption(IMPORT_COLUMNS_OPT.getOpt())) {
            userSuppliedColumnNames = Lists.newArrayList(
                    Splitter.on(",").trimResults().split
                            (cmdLine.getOptionValue(IMPORT_COLUMNS_OPT.getOpt())));
        }
        return CSVCommonsLoader.generateColumnInfo(
                conn, qualifiedTableName, userSuppliedColumnNames, true);
    }

    /**
     * Calculate the HBase HTable name for which the import is to be done.
     *
     * @param schemaName import schema name, can be null
     * @param tableName import table name
     * @return the byte representation of the import HTable
     */
    @VisibleForTesting
    static String getQualifiedTableName(String schemaName, String tableName) {
        if (schemaName != null) {
            return String.format("%s.%s", SchemaUtil.normalizeIdentifier(schemaName),
                    SchemaUtil.normalizeIdentifier(tableName));
        } else {
            return SchemaUtil.normalizeIdentifier(tableName);
        }
    }

    /**
     * Set configuration values based on parsed command line options.
     *
     * @param cmdLine supplied command line options
     * @param importColumns descriptors of columns to be imported
     * @param conf job configuration
     */
    @VisibleForTesting
    static void configureOptions(CommandLine cmdLine, List<ColumnInfo> importColumns,
            Configuration conf) {

        char delimiterChar = ',';
        if (cmdLine.hasOption(DELIMITER_OPT.getOpt())) {
            String delimString = cmdLine.getOptionValue(DELIMITER_OPT.getOpt());
            if (delimString.length() != 1) {
                throw new IllegalArgumentException("Illegal delimiter character: " + delimString);
            }
            delimiterChar = delimString.charAt(0);
        }
        /*
        if (cmdLine.hasOption(ZK_QUORUM_OPT.getOpt())) {
            String zkQuorum = cmdLine.getOptionValue(ZK_QUORUM_OPT.getOpt());
            LOG.info("Configuring ZK quorum to {}", zkQuorum);
            conf.set(HConstants.ZOOKEEPER_QUORUM, zkQuorum);
        }
        */
        //将增加的命令行参数设置到Configuration
        
        String rpCols = cmdLine.getOptionValue(ROW_PREFIX_COLUMNS_OPT.getOpt());
        LOG.info("Configuring row prefix columns to {}", rpCols);
    	conf.set(PhoenixCsvToKeyValueMapper.ROW_PREFIX_COLUMNS, rpCols);
        
        if (cmdLine.hasOption(ROW_PREFIX_ALG_OPT.getOpt())) {
        	String rowPrefixAlg = cmdLine.getOptionValue(ROW_PREFIX_ALG_OPT.getOpt());
            LOG.info("Configuring row prefix alg to {}", rowPrefixAlg);
        	conf.set(PhoenixCsvToKeyValueMapper.ROW_PREFIX_ALG, rowPrefixAlg);
        }
        
        String rCols = cmdLine.getOptionValue(ROW_COLUMNS_OPT.getOpt());
        LOG.info("Configuring row columns to {}", rCols);
    	conf.set(PhoenixCsvToKeyValueMapper.ROW_COLUMNS, rCols);
    	
        if (cmdLine.hasOption(UNIQUE_INDEX_COLUMNS_OPT.getOpt())) {
        	String uniqueIndexColumns = cmdLine.getOptionValue(UNIQUE_INDEX_COLUMNS_OPT.getOpt());
            LOG.info("Configuring unique index columns to {}", uniqueIndexColumns);
            conf.set(PhoenixCsvToKeyValueMapper.UNIQUE_INDEX_COLUMNS, uniqueIndexColumns);
        }
        
        CsvBulkImportUtil.initCsvImportJob(
                conf,
                getQualifiedTableName(
                        cmdLine.getOptionValue(SCHEMA_NAME_OPT.getOpt()),
                        cmdLine.getOptionValue(TABLE_NAME_OPT.getOpt())),
                delimiterChar,
                cmdLine.getOptionValue(ARRAY_DELIMITER_OPT.getOpt()),
                importColumns,
                cmdLine.hasOption(IGNORE_ERRORS_OPT.getOpt()));
    }

    /**
     * Perform any required validation on the table being bulk loaded into:
     * - ensure no column family names start with '_', as they'd be ignored leading to problems.
     * @throws java.sql.SQLException
     */
    private void validateTable(Connection conn, String schemaName,
            String tableName) throws SQLException {

        ResultSet rs = conn.getMetaData().getColumns(
                null, StringUtil.escapeLike(schemaName),
                StringUtil.escapeLike(tableName), null);
        while (rs.next()) {
            String familyName = rs.getString(PhoenixDatabaseMetaData.COLUMN_FAMILY);
            if (familyName != null && familyName.startsWith("_")) {
                if (QueryConstants.DEFAULT_COLUMN_FAMILY.equals(familyName)) {
                    throw new IllegalStateException(
                            "CSV Bulk Loader error: All column names that are not part of the " +
                                    "primary key constraint must be prefixed with a column family " +
                                    "name (i.e. f.my_column VARCHAR)");
                } else {
                    throw new IllegalStateException("CSV Bulk Loader error: Column family name " +
                            "must not start with '_': " + familyName);
                }
            }
        }
        rs.close();
    }
}
