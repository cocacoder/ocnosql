package com.ailk.oci.ocnosql.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;

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
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 '" + text + "' exception.", e);

		}
		return str;
	}
}
