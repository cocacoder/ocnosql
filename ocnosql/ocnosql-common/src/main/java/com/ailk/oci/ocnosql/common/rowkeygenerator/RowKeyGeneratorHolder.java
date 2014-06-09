package com.ailk.oci.ocnosql.common.rowkeygenerator;

import org.apache.commons.lang.*;

import com.ailk.oci.ocnosql.common.rowkeygenerator.BusiRowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.HashRowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.MD5RowKeyGenerator;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;

import java.util.*;
import java.util.concurrent.*;

/**
 * RowKeyGenerator的容器
 * @author Administrator
 *
 */
public class RowKeyGeneratorHolder {
   public static enum TYPE{md5,hash,busi};
	
   private static final Map<String,RowKeyGenerator> generatorCache = new ConcurrentHashMap<String,RowKeyGenerator>();
   static{
	   generatorCache.put(TYPE.md5.name(), new MD5RowKeyGenerator());
	   generatorCache.put(TYPE.hash.name(), new HashRowKeyGenerator());
//       generatorCache.put(TYPE.busi.name(), new BusiRowKeyGenerator());
   }
   public static RowKeyGenerator resolveGenerator(String generatorKey){
	   if(StringUtils.isEmpty(generatorKey)){
		   return null;
	   }
	   return generatorCache.get(generatorKey);
   }
}
