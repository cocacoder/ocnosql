package com.ailk.oci.ocnosql.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;

import com.ailk.oci.ocnosql.common.OCNosqlNestedRuntimeException;

public class MD5Util {
	//public String str;

	public static String md5(String text) {
		String str = null;
		if(StringUtils.isEmpty(text))
			return null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
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
			str = buf.toString();
			//System.out.println("result: " + buf.toString());// 32位的加密
			//System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			throw new OCNosqlNestedRuntimeException("MD5 '" + text + "' exception.", e);

		}
		return str;
	}
}
