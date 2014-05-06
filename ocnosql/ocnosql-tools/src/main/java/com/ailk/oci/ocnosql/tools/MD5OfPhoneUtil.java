package com.ailk.oci.ocnosql.tools;

import com.ailk.oci.ocnosql.client.rowkeygenerator.*;
import org.apache.commons.lang.*;
import org.apache.hadoop.hbase.util.*;

import java.math.*;
import java.security.*;

public class MD5OfPhoneUtil {


    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("must specify one or more phone,for example: 13312344321 13319870719");
            System.exit(-1);
        }
        if(args[0].length() != 11){
            System.out.println("phone length is illegal! should be 11.");
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
                System.out.println(phone+"=>"+result.substring(1, 2) + result.substring(3, 4) + result.substring(5, 6));
            }
        }catch (NoSuchAlgorithmException ex) {
        	throw new RuntimeException("failed init MD5 instance.",ex);
        }
    }
}
