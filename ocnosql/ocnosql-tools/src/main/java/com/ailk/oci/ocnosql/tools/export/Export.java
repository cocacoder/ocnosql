package com.ailk.oci.ocnosql.tools.export;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * Export an HBase table. Writes content to sequence files up in HDFS. Use
 * {@link org.apache.hadoop.hbase.mapreduce.Import} to read it back in again.
 */
public class Export {
	private static final Log LOG = LogFactory.getLog(Export.class);
	public final static String NAME = "export";

	/**
	 * Mapper.
	 */
	static class Exporter extends TableMapper<ImmutableBytesWritable, Result> {
		private static int pos;
		private static int len;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
			pos = context.getConfiguration().getInt("pos", 0);
			len = context.getConfiguration().getInt("len", 0);
		}

		/**
		 * @param row
		 *            The current table row key.
		 * @param value
		 *            The columns.
		 * @param context
		 *            The current context.
		 * @throws java.io.IOException
		 *             When something is broken with the data.
		 */
		@Override
        //对每一个键值对进行处理,即把rowkey的hash值去掉在输出
		public void map(ImmutableBytesWritable row, Result value,
				Context context) throws IOException {
			try {
				/**
				 * src - 源数组。 srcPos - 源数组中的起始位置。 dest - 目标数组。 destPos -
				 * 目标数据中的起始位置。 length - 要复制的数组元素的数量。
				 */
				byte[] src = row.get();
				byte[] target = new byte[len == 0 ? (src.length - pos) : len];
				System.arraycopy(src, pos, target, 0, target.length);

				//ImmutableBytesWritable r = new ImmutableBytesWritable(target);
				row.set(target);
				context.write(row, value);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static class ExporterReducer extends
			Reducer<ImmutableBytesWritable, Result, NullWritable, Text> {
		Text value = new Text();
		StringBuffer sb;
		//String sep;// 拼接分隔符
		//String mod;// 单行还是多行模式(默认为单行)
		String osep;//输出到文件时各字段的连接符
		//IHbaseCompress compress;
		int rowkeyIndex;//输出到文件时，rowkey放在哪个位置

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			//sep = context.getConfiguration().get("sep", "\t");
			//mod = context.getConfiguration().get("mod", "single");
			osep = context.getConfiguration().get("osep", "\t");
			//String compressorName = context.getConfiguration().get("c", "com.ailk.ocnosql.core.compress.impl.HbaseNullCompress");
			//Class compressorClass;
            /*
			try{
				compressorClass = Class.forName(compressorName);
			} catch(ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			*/
			//compress = (IHbaseCompress)ReflectionUtils.newInstance(compressorClass, context.getConfiguration());
			rowkeyIndex = context.getConfiguration().getInt("ri", 0);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected void reduce(ImmutableBytesWritable key,
				Iterable<Result> values, Context context) throws IOException,
				InterruptedException {
			// 循环每一条记录
			for (Result result : values) {
                sb = new StringBuffer();
                // 拼接各列的值
                for (int i = 0; i < result.raw().length; i++) {
                    KeyValue kv = result.raw()[i];
                    if(i == rowkeyIndex) {
                        sb.append(Bytes.toString(key.get())).append(osep);
                    }
                    sb.append(Bytes.toString(kv.getValue())).append(osep);
                }
                //如果rowkeyIndex大于raw最大长度，将rowkey放入数据结尾
                if(rowkeyIndex > result.raw().length) {
                    sb.append(Bytes.toString(key.get())).append(osep);
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                value.set(sb.toString());
                context.write(NullWritable.get(), value);
			}
		}
	}
	
	/**
	 * Sets up the actual job.
	 * 
	 * @param conf
	 *            The current configuration.
	 * @param args
	 *            The command line parameters.
	 * @return The newly created job.
	 * @throws java.io.IOException
	 *             When setting up the job fails.
	 */
	public static Job createSubmittableJob(Configuration conf, String[] args)
			throws Exception {
		String tableName = null;
		Path outputDir = null;
		// 参数验证
		Options options = new Options();
		options.addOption("t", true, "tablename");
		options.addOption("o", true, "outputdir");
		options.addOption("v", true, "versions");
		options.addOption("st", true, "starttime");
		options.addOption("et", true, "endtime");
		options.addOption("rn", true, "reduce task number");
		// 自己加的参数(是否单列)
		options.addOption("pos", true,
				"rowkey trunc start position,contains the start position");
		options.addOption("len", true, "rowkey trunc length");
		//数据连接符
		//options.addOption("sep", true, "separator[default is '\\t']");
		//输出连接符
		options.addOption("osep", true, "output separator[default is '\\t']");
		// 模式
		options.addOption("mod", true, "storage mode[single/multi]");
		//压缩类型
		//options.addOption("c", true, "compress class, default: com.ailk.ocnosql.core.compress.impl.HbaseNullCompress");
		
		options.addOption("ri", true, "rowkey index position, default: 0");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		String parser_error = "ERROR while parsing arguments. ";
		if (cmd.hasOption("t")) {
			tableName = cmd.getOptionValue("t");
		} else {
			System.err.println(parser_error + "Please provide tablename");
			System.exit(0);
		}
		if (cmd.hasOption("o")) {
			outputDir = new Path(cmd.getOptionValue("o"));
		} else {
			System.err.println(parser_error + "Please provide outputdir");
			System.exit(0);
		}
		// 把自己加的需要在map和reduce中用的参数放进Configuration
		if (cmd.hasOption("pos")) {
			int pos = Integer.parseInt(cmd.getOptionValue("pos"));
			if (pos < 0) {
				System.err.println(parser_error + "rowkey trunc start position must be greater than or equal to zero");
				System.exit(0);
			} else {
				// row key 截取位置默认从0开始
				conf.setInt("pos", pos);
			}
		}
		if (cmd.hasOption("len")) {
			int len = Integer.parseInt(cmd.getOptionValue("len"));
			if (len < 0) {
				System.err.println(parser_error + "rowkey trunc length must be greater than or equal to zero");
				System.exit(0);
			} else {
				// row key 截取长度默认从1开始
				conf.setInt("len", len);
			}
		}
		// 分隔符
		if (cmd.hasOption("sep")) {
			conf.setStrings("sep", cmd.getOptionValue("sep"));
		}
		// 分隔符
		if (cmd.hasOption("osep")) {
			conf.setStrings("osep", cmd.getOptionValue("osep"));
		}
		// 存储模式
		if (cmd.hasOption("mod")) {
			String mod = cmd.getOptionValue("mod");
			if (mod.equals("single") || mod.equals("multi")) {
				conf.setStrings("mod", mod);
			} else {
				System.err.println(parser_error
						+ "storage mode must be [single/multi]");
				System.exit(0);
			}
		}
		
		if(cmd.hasOption("c")) {
			conf.setStrings("c", cmd.getOptionValue("c"));
		}
		
		if (cmd.hasOption("ri")) {
			int rowkeyIndex = Integer.parseInt(cmd.getOptionValue("ri"));
			conf.setInt("ri", rowkeyIndex);
		}

		Job job = new Job(conf, NAME + "_" + tableName);
		job.setJobName(NAME + "_" + tableName);
		job.setJarByClass(Exporter.class);
		// TODO: Allow passing filter and subset of rows/columns.
		Scan s = new Scan();
		// Optional arguments.
		int versions = cmd.hasOption("v") ? Integer.parseInt(cmd
				.getOptionValue("v")) : 1;
		s.setMaxVersions(versions);
		long startTime = cmd.hasOption("st") ? Long.parseLong(cmd
				.getOptionValue("st")) : 0L;
		long endTime = cmd.hasOption("et") ? Long.parseLong(cmd
				.getOptionValue("et")) : Long.MAX_VALUE;
		s.setTimeRange(startTime, endTime);
		s.setCacheBlocks(false);
		if (conf.get(TableInputFormat.SCAN_COLUMN_FAMILY) != null) {
			s.addFamily(Bytes.toBytes(conf
					.get(TableInputFormat.SCAN_COLUMN_FAMILY)));
		}
		LOG.info("tableName=" + tableName + ",outputDir=" + outputDir
				+ ",verisons=" + versions + ", starttime=" + startTime
				+ ", endtime=" + endTime);
		TableMapReduceUtil.initTableMapperJob(tableName, s, Exporter.class,
				ImmutableBytesWritable.class, null, job);
		// No reducers. Just write straight to output files.
		
		if (cmd.hasOption("rn")) {
			int rn = Integer.parseInt(cmd.getOptionValue("rn"));
			if (rn < 0) {
				System.err.println(parser_error + "rn[reduce task number] must be greater than zero");
				System.exit(0);
			} else {
				job.setNumReduceTasks(rn);
			}
		} else {
			HTable table = new HTable(conf, tableName);
			job.setNumReduceTasks(table.getStartKeys().length);
		}
		conf = job.getConfiguration();
		 // partitioner class设置成TotalOrderPartitioner(hbase的)
		job.setPartitionerClass(TotalOrderPartitioner.class);
		//采样器(hbase的)
//		InputSampler.RandomSampler<ImmutableBytesWritable, Result> sampler =
//				new InputSampler.RandomSampler<ImmutableBytesWritable, Result>(0.1,10000,10);
		MD5RowkeyRandomSampler<ImmutableBytesWritable, Result> sampler =
				new MD5RowkeyRandomSampler<ImmutableBytesWritable, Result>(0.1,10000,1, conf);
		
		//Path partitionFile=new Path("/export/"+System.currentTimeMillis(),"_partitions");
		
		Path partitionsPath = new Path(job.getWorkingDirectory(), "partitions_" + UUID.randomUUID());
		
		LOG.info("Writing partition information to " + partitionsPath);
		
		TotalOrderPartitioner.setPartitionFile(conf, partitionsPath);
		InputSampler.writePartitionFile(job, sampler);
		
		//#的作用是以后用的时候直接_partitions就可以了 
		URI partitionUri=new URI(partitionsPath.toString() + "/#_partitions");
		DistributedCache.addCacheFile(partitionUri, conf);
		DistributedCache.createSymlink(conf);
		
		job.setReducerClass(ExporterReducer.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Result.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		FileOutputFormat.setOutputPath(job, outputDir);

		return job;
	}
	
	
	
	/*
	 * @param errorMsg Error message. Can be null.
	 */
	private static void usage(final String errorMsg) {
		if (errorMsg != null && errorMsg.length() > 0) {
			System.err.println("ERROR: " + errorMsg);
		}
		System.err
				.println("Usage: Export [-D <property=value>]* <-t tablename> <-o outputdir> [-pos position] [-len length] [-sep separator] [-mod mode] [<-v versions> "
						+ "[<-st starttime> [<-et endtime>]]]\n");
		System.err
				.println("  Note: -D properties will be applied to the conf used. ");
		System.err.println("  For example: ");
		System.err.println("   -D mapred.output.compress=true");
		System.err
				.println("   -D mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec");
		System.err.println("   -D mapred.output.compression.type=BLOCK");
		System.err
				.println("  Additionally, the following SCAN properties can be specified");
		System.err.println("  to control/limit what is exported..");
		System.err.println("   -D " + TableInputFormat.SCAN_COLUMN_FAMILY
				+ "=<familyName>");
		System.err.println();
	}

	/**
	 * Main entry point.
	 * 
	 * @param args
	 *            The command line parameters.
	 * @throws Exception
	 *             When running the job fails.
	 */
	public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
		// 这一步之后命令行中的-D参数项就不在otherArgs中了，已经被设置到conf了
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();

		int index = 0;
		for (String arg : args) {
			LOG.info("agrs[" + index + "]=" + arg);
			index++;
		}

		if (otherArgs.length < 2) {
			usage("Wrong number of arguments: " + otherArgs.length);
			System.exit(-1);
		}
		Job job = createSubmittableJob(conf, otherArgs);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

