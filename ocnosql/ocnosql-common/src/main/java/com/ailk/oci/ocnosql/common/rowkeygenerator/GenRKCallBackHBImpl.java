package com.ailk.oci.ocnosql.common.rowkeygenerator;


import com.ailk.oci.ocnosql.common.rowkeygenerator.GenRKCallBack;
import com.ailk.oci.ocnosql.common.rowkeygenerator.GenRKCallBackHBImpl;
import com.ailk.oci.ocnosql.common.util.CommonConstants;

import org.apache.commons.lang.*;

import java.security.*;

public class GenRKCallBackHBImpl implements GenRKCallBack {

    public String callback(String rowKey, String line, String[] columns) {
		StringBuffer lineBuf = new StringBuffer(rowKey);
		String[] arr = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, CommonConstants.DEFAULT_SEPARATOR);
		lineBuf.append(arr[10].substring(0, 14)).append(arr[19]);
		return lineBuf.toString();
	}

	private static String Md5(String plainText) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte[] b = md.digest();

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				int i = b[offset];
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

	public static void main(String[] args) {
		GenRKCallBackHBImpl impl = new GenRKCallBackHBImpl();
        String line = "13697278471\\t0\t221.177.161.250\t2\t12\t1206\t0\t1\t57797\t11767\t20140309054043\t80809312\t80809686\tucs17.tjcm.gtm.ucweb.com\t3918929170\t856.0\t1067.0\thttp://ucs17.tjcm.gtm.ucweb.com/?ucid=1308-10940023951-b645231e\t1923.0\t\t\t\t\t";
        String rsult = impl.callback("***",line,null);
		System.out.println(rsult);
	}

}