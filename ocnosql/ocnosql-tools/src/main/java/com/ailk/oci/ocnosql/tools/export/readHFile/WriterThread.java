package com.ailk.oci.ocnosql.tools.export.readHFile;

import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.ailk.oci.ocnosql.common.compress.Compress;

public class WriterThread implements Runnable {
	private FileSystem fs;
	private Path hfilePath;
	private Compress decompress;
	private String destPath;
	private CountDownLatch runningThreadNum;
	
	public WriterThread(FileSystem fs, Path hfilePath, Compress decompress, String destPath, CountDownLatch runningThreadNum){
		this.fs = fs;
		this.hfilePath = hfilePath;
		this.decompress = decompress;
		this.destPath = destPath;
		this.runningThreadNum = runningThreadNum;
	}

	public void run() {
//		BufferedWriter bw = null;
//		long time = 0l;
//		try {
//			HFile.Reader reader = new HFile.Reader(fs, hfilePath, null, false);
//			HFileScanner scan  = reader.getScanner(false, false);
//			reader.loadFileInfo();
//			scan.seekTo();
//			StringBuffer sbuffer = new StringBuffer();
//			int counter = 0;
//			int index = 0;
//			do{
//				KeyValue kv = scan.getKeyValue();
//				Map<String, String> param = new HashMap<String, String>();
//				param.put(CommonConstants.SEPARATOR, HFileReaderSample.SEPERATOR);
//				List<String[]> result = decompress.deCompress(kv, param);
//				String rowKey = new String(kv.getRow());
//				String rowkeyGennerator = HFileReaderSample.prop.getProperty(
//						"rowkey.generator")==null? "md5" : HFileReaderSample.prop.getProperty("rowkey.generator");
//				if(rowkeyGennerator.equalsIgnoreCase("md5")){
//					rowKey = rowKey.substring(3);
//				}
//				for(String[] strs : result){
//					if(counter == HFileReaderSample.RECORDE_NUM_PER_FILE){
//						counter = 0;
//					}
//					if(counter == 0){
//						if(bw != null){
//							bw.flush();
//							bw.close();
//						}
//						String fileName = destPath + File.separator + HFileReaderSample.TABLE_NAME + "_" + hfilePath.getName() + "_" + index;
//						File file = new File(fileName);
//						bw = new BufferedWriter(new FileWriter(file, true));
//						time = System.currentTimeMillis();
//						index ++;
//					}
//					int k = 0;
//					for(String str : strs){
//						if(k == HFileReaderSample.ROWKEY_INDEX ){
//							sbuffer.append(rowKey + HFileReaderSample.SEPERATOR);
//						}else {
//							sbuffer.append(str + HFileReaderSample.SEPERATOR);
//						}
//                      k++;
//					}
//					sbuffer.deleteCharAt(sbuffer.length() - 1);
//					bw.write(sbuffer.toString() + "\n");
//					sbuffer.setLength(0);
//					counter ++;
//				}
//			}while(scan.next());
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			runningThreadNum.countDown();
//			try {
//				if(bw != null){
//					bw.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

}
