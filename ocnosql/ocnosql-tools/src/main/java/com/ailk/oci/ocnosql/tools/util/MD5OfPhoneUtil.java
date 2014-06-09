package com.ailk.oci.ocnosql.tools.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5OfPhoneUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MD5OfPhoneUtil.class);


    public static void main(String[] args) {
        if(args.length < 1){
            LOG.error("must specify one or more phone,for example: 13312344321 13319870719");
            System.exit(-1);
        }
        if(args[0].length() != 11){
            LOG.error("phone length is illegal! should be 11.");
            System.exit(-1);
        }
        MessageDigest md;
		try {
            md = MessageDigest.getInstance("MD5");
            for(String phone:args){
                md.reset();
                md.update(phone.getBytes());
                byte[] digest = md.digest();
                StringBuffer sb = new StringBuffer();
                for (byte b : digest) {
                    sb.append(Integer.toHexString((int) (b & 0xff)));
                }
                String result = sb.toString();
                LOG.info(phone+"=>"+result.substring(1, 2) + result.substring(3, 4) + result.substring(5, 6));
            }
        }catch (NoSuchAlgorithmException ex) {
        	throw new RuntimeException("failed init MD5 instance.",ex);
        }
    }
}
