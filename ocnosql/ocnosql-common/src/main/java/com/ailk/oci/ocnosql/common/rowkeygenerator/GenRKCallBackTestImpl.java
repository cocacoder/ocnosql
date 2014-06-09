package com.ailk.oci.ocnosql.common.rowkeygenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GenRKCallBackTestImpl implements GenRKCallBack {
	
	MessageDigest md;
	
	public GenRKCallBackTestImpl() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
	}

	
	
	@Override
	public String callback(String rowKey, String line, String[] columns) {
		StringBuffer sb = new StringBuffer(rowKey);
		sb.append(columns[4].substring(0, 8));
		return sb.toString();
	}

	
	String Md5(String plainText) {
        String result = null;
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
        return result;
    }
	
}
