package com.ailk.oci.ocnosql.tools.export;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler.RandomSampler;
import org.apache.hadoop.util.ReflectionUtils;

public class MD5RowkeyRandomSampler<K, V> extends RandomSampler<K, V> {

	Log LOG = LogFactory.getLog(MD5RowkeyRandomSampler.class);
	private double freq;
	private final int numSamples;
	private final int maxSplitsSampled;
	public Configuration conf;

	public MD5RowkeyRandomSampler(double freq, int numSamples,
			Configuration conf) {
		super(freq, numSamples);
		this.freq = freq;
		this.numSamples = numSamples;
		this.maxSplitsSampled = Integer.MAX_VALUE;
		this.conf = conf;
	}

	public MD5RowkeyRandomSampler(double freq, int numSamples,
			int maxSplitsSampled, Configuration conf) {
		super(freq, numSamples, maxSplitsSampled);
		this.freq = freq;
		this.numSamples = numSamples;
		this.maxSplitsSampled = maxSplitsSampled;
		this.conf = conf;
	}

	@SuppressWarnings("unchecked")
	public K[] getSample(InputFormat<K, V> inf, Job job) throws IOException,
			InterruptedException {
		// K[] k = super.getSample(inf, job);//不再调用父类的方法，将父类方法体的内容拷过来修复了bug
		List<InputSplit> splits = inf.getSplits(job);
		ArrayList<K> samples = new ArrayList<K>(numSamples);
		int splitsToSample = Math.min(maxSplitsSampled, splits.size());

		Random r = new Random();
		long seed = r.nextLong();
		r.setSeed(seed);
		LOG.debug("seed: " + seed);
		// shuffle splits
		for (int i = 0; i < splits.size(); ++i) {
			InputSplit tmp = splits.get(i);
			int j = r.nextInt(splits.size());
			splits.set(i, splits.get(j));
			splits.set(j, tmp);
		}
		// our target rate is in terms of the maximum number of sample splits,
		// but we accept the possibility of sampling additional splits to hit
		// the target sample keyset
		for (int i = 0; i < splitsToSample
				|| (i < splits.size() && samples.size() < numSamples); ++i) {
			TaskAttemptContext samplingContext = getTaskAttemptContext(job);
			RecordReader<K, V> reader = inf.createRecordReader(splits.get(i),
					samplingContext);
			reader.initialize(splits.get(i), samplingContext);
			while (reader.nextKeyValue()) {
				if (r.nextDouble() <= freq) {
					if (samples.size() < numSamples) {
						samples.add(ReflectionUtils.copy(
								job.getConfiguration(), reader.getCurrentKey(),
								null));
					} else {
						// When exceeding the maximum number of samples, replace
						// a
						// random element with this one, then adjust the
						// frequency
						// to reflect the possibility of existing elements being
						// pushed out
						int ind = r.nextInt(numSamples);
						if (ind != numSamples) {
							samples.set(ind, ReflectionUtils.copy(
									job.getConfiguration(),
									reader.getCurrentKey(), null));
						}
						freq *= (numSamples - 1) / (double) numSamples;
					}
				}
			}
			reader.close();
		}
		K[] k = (K[]) samples.toArray();

		if (conf.getInt("pos", 0) == 0) {
			return k;
		} else {
			List<K> keys = new ArrayList<K>();
			int len = conf.getInt("len", 0);
			int pos = conf.getInt("pos", 0);
			for (int i = 0; i < k.length; i++) {
				byte[] src = ((ImmutableBytesWritable) k[i]).get();
				byte[] target = new byte[len == 0 ? (src.length - pos) : len];
				System.arraycopy(src, pos, target, 0, target.length);
				ImmutableBytesWritable r1 = new ImmutableBytesWritable(target);
				keys.add((K) r1);
				// LOG.debug("sample data: " + Bytes.toString(r.get()));
			}
			return (K[]) keys.toArray();
		}

	}
	
	public static TaskAttemptContext getTaskAttemptContext(final Job job)
			throws IOException {
		Constructor<?> c;
		try {
			if (TaskAttemptContext.class.isInterface()) {
				// Hadoop 2.x
				Class<?> clazz = Class
						.forName("org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl");
				c = clazz.getConstructor(Configuration.class,
						TaskAttemptID.class);
			} else {
				// Hadoop 1.x
				c = TaskAttemptContext.class.getConstructor(
						Configuration.class, TaskAttemptID.class);
			}
		} catch (Exception e) {
			throw new IOException("Failed getting constructor", e);
		}
		try {
			return (TaskAttemptContext) c.newInstance(job.getConfiguration(),
					new TaskAttemptID());
		} catch (Exception e) {
			throw new IOException("Failed creating instance", e);
		}
	}

}
