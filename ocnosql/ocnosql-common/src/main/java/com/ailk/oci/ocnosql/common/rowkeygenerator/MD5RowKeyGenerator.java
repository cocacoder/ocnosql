package com.ailk.oci.ocnosql.common.rowkeygenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.oci.ocnosql.common.rowkeygenerator.MD5RowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGeneratorException;

public class MD5RowKeyGenerator implements RowKeyGenerator{
	static Log LOG = LogFactory.getLog(MD5RowKeyGenerator.class);
	private  MessageDigest md = null;
    @Override
    public String generate(String oriRowKey, String needHashValue, String[] currenRowdata, int[] posIndex, String appendValue) {
        return null;
    }
	public Object generate(String oriRowKey) {
		return getMD5(oriRowKey)+oriRowKey;
	}
	
	/**
	 * @param oriRowKey rowkey值
	 * @return 返回rowkey的MD5值
	 */
	private synchronized Object getMD5(String oriRowKey) {
		if(oriRowKey==null){
			throw new RowKeyGeneratorException("param of oriRowKey is null");
		}
		try {
            md = MessageDigest.getInstance("MD5");
        } 
		catch (NoSuchAlgorithmException ex) {
        	throw new RowKeyGeneratorException("failed init MD5 instance.",ex);
        }
        md.reset();
        md.update(oriRowKey.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        String result = sb.toString();
        return result.substring(1, 2) + result.substring(3, 4) + sb.toString().substring(5, 6);
    
	}
	@Override
	public Object generatePrefix(String oriRowKey) {
		return getMD5(oriRowKey);
	}

    public static void main(String[] args) {
    	LOG.info(new MD5RowKeyGenerator().getMD5("13731733108"));
    }

}
