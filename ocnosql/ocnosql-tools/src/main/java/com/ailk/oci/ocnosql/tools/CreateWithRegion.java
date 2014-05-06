package com.ailk.oci.ocnosql.tools;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.io.hfile.Compression;

import com.ailk.oci.ocnosql.client.config.spi.Connection;
import org.apache.hadoop.hbase.regionserver.*;
import org.apache.hadoop.hbase.util.*;
import org.apache.hadoop.util.*;

import java.util.*;

public class CreateWithRegion {

    public static final String COMPREESSTYPE  = "compressType";
    public static final String DATABLOCKENCODING  = "dataBlockEncoding";
    public static final String BLOOMFILTER  = "bloomFilter";


    static Map<String,DataBlockEncoding> dataBlockEncodingTypes = new HashMap<String,DataBlockEncoding>();
    static Map<String,Compression.Algorithm> compressionTypes = new HashMap<String,Compression.Algorithm>();
    static Map<String,StoreFile.BloomType> bloomFilterTypes = new HashMap<String,StoreFile.BloomType>();

    static {
        dataBlockEncodingTypes.put("FAST_DIFF",DataBlockEncoding.FAST_DIFF);
        dataBlockEncodingTypes.put("DIFF",DataBlockEncoding.DIFF);
        dataBlockEncodingTypes.put("PREFIX",DataBlockEncoding.PREFIX);
        dataBlockEncodingTypes.put("NONE",DataBlockEncoding.NONE);

        compressionTypes.put("SNAPPY",Compression.Algorithm.SNAPPY);
        compressionTypes.put("GZ",Compression.Algorithm.GZ);
        compressionTypes.put("LZO",Compression.Algorithm.LZO);
        compressionTypes.put("LZ4",Compression.Algorithm.LZ4);
        compressionTypes.put("NONE",Compression.Algorithm.NONE);

        bloomFilterTypes.put("ROWCOL", StoreFile.BloomType.ROWCOL);
        bloomFilterTypes.put("ROW", StoreFile.BloomType.ROW);
        bloomFilterTypes.put("NONE", StoreFile.BloomType.NONE);
    }



	public static void main(String[] args) {

		try {
            Configuration conf = HBaseConfiguration.create();
            String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
            if(otherArgs.length < 3){
                System.err.println("paramter size must at lest be 3");
                usage();
                System.exit(0);
            }
            String tableName = otherArgs[0];
            String columnName = otherArgs[1];
            int regionNum = Integer.valueOf(otherArgs[2]);

            String compressType = conf.get(COMPREESSTYPE);
            String dataBlockEncodingType = conf.get(DATABLOCKENCODING);
            String bloomFilterType =  conf.get(BLOOMFILTER);
            if(compressType == null || compressType.equals("")){
                compressType = "NONE";
            }
            if(dataBlockEncodingType == null || dataBlockEncodingType.equals("")){
                dataBlockEncodingType = "NONE";
            }
            if(bloomFilterType == null || bloomFilterType.equals("")){
                bloomFilterType = "NONE";
            }

			conf.set("hbase.zookeeper.property.clientPort", Connection.get("hbase.zookeeper.property.clientPort", null));
		    conf.set("hbase.zookeeper.quorum", Connection.get("hbase.zookeeper.quorum", null));

			
			HBaseAdmin admin = new HBaseAdmin(conf);
			if(admin.tableExists(tableName)){
				System.err.println("table : " + tableName + " already exist.");
				return;
			}
			
			HTableDescriptor tableDesc = new HTableDescriptor(tableName);
			HColumnDescriptor columnDesc = new HColumnDescriptor(columnName);

			columnDesc.setCompactionCompressionType(compressionTypes.get(compressType));
            columnDesc.setCompressionType(compressionTypes.get(compressType));
            columnDesc.setDataBlockEncoding(dataBlockEncodingTypes.get(dataBlockEncodingType));
            columnDesc.setBloomFilterType(bloomFilterTypes.get(bloomFilterType));

            //columnDesc.setMaxVersions(100000);
			tableDesc.addFamily(columnDesc);
			admin.createTable(tableDesc, RegionSplitsUtil.splits(regionNum) );
			admin.getTableDescriptor(tableName.getBytes()).toString();
			
			System.out.println("table : " + tableName + " created sucessfully.");
		} catch (Exception e) {
			System.err.println("failed create region" + e);
		} 
	} 
 
	private static void usage(){
		System.out.println("paramter : -DcompressType=SNAPPY -DdataBlockEncoding=FAST_DIFF -DbloomFilter=NONE  tablename columnName regionNum");
	}
}
