package com.ailk.oci.ocnosql.example.model;


import com.ailk.oci.ocnosql.example.exception.*;
import com.ailk.oci.ocnosql.example.util.*;
import org.apache.commons.beanutils.*;
import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.dom4j.*;
import org.dom4j.io.*;
import org.springframework.core.io.*;
import org.springframework.core.io.support.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;

public class ModelReader{
	
	private final static Log log = LogFactory.getLog(ModelReader.class);
	private static SAXReader reader = new SAXReader();
	private static Map<String,MetaModel> metaModels;
	private static Map<String,String> allBusiType = new WeakHashMap<String,String>();
	
	public static Map<String, MetaModel> getMetaModels() {
		return metaModels;
	}
	
   	public static Map<String,String> getAllBusiType() {
		return allBusiType;
	}
   	
	public void loadModel(String path) throws Exception{
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	    Resource[] resources=resolver.getResources(path);
		if (resources.length!=0) {
			if(log.isInfoEnabled()){
				log.info("model path:"+resources[0].getURL().getPath().replaceAll(resources[0].getFilename(), ""));
			}
			metaModels = new WeakHashMap<String,MetaModel>();
			for(Resource resource:resources){
				try{
					MetaModel metaModel = readFile2MetaModel(resource);
					if(metaModel!=null){
						metaModels.put(metaModel.getModelId(), metaModel);
					}
				}
				catch(Exception e){
					if (log.isErrorEnabled()) {
						log.error(e);
					}
				}
				
			}
		}
	}
	private MetaModel readFile2MetaModel(Resource resource) throws Exception{
		InputStream inputStream = resource.getInputStream();
		if (log.isInfoEnabled()) {
			log.info("mappring model>>>" + resource.getFilename());
		}
		Document document = null;
		try {
			document = reader.read(inputStream);
		} 
		catch (DocumentException e) {
			if (log.isErrorEnabled()) {
				log.error("loader meta model exception: read model["+resource.getFilename()+"] file failed");
			}
			throw new ModelRuntimeException("loader meta model exception: read model["+resource.getFilename()+"] file failed",e);
		}
		Element root = document.getRootElement();
		MetaModel metaModel = new MetaModel();
		Attribute modelId = root.attribute("modelId");
		if(modelId!=null){
			metaModel.setModelId(modelId.getValue());
		}
		Attribute table = root.attribute("table");
		if(table!=null){
			metaModel.setTable(table.getValue());
		}
		Attribute name = root.attribute("name");
		if(name!=null){
			metaModel.setName(name.getValue());
		}
		Attribute process = root.attribute("process");
		if(process!=null){
			metaModel.setProcess(process.getValue());
		}
		else{
			throw new ModelRuntimeException("The model xml not set process field. process value cant be null!");
		}
		Attribute statType = root.attribute("statType");
		if(statType!=null){
			metaModel.setStatType(statType.getValue());
		}
		Attribute statRule = root.attribute("statRule");
		if(statRule!=null){
			metaModel.setStatRule(statRule.getValue());
		}
		allBusiType.put(metaModel.getModelId(), metaModel.getName());
		
		List<Element> nodeList = root.elements();
		if (!CollectionUtils.isEmpty(nodeList)) {
			Map<String,Field> fields = new HashMap<String,Field>();
			for (Element element : nodeList) {
				List<Attribute> attrs = element.attributes();
				Field field = new Field();
				for(Attribute attr:attrs){
					if(log.isDebugEnabled()){
						log.debug("set field["+attr.getName()+"-"+attr.getValue()+"] to "+modelId.getValue());
					}
					String value = attr.getValue();
					String fieldName = attr.getName();
					if(StringUtils.contains(value, " ")){
						log.error("model["+metaModel.getModelId()+"] field["+fieldName+"]'s value["+value+"] contain blank space");
						value = StringUtils.trim(value);
					}
					BeanUtils.setProperty(field, attr.getName(), attr.getValue());
				}
				fields.put(field.getName(), field);
			}
			metaModel.setFields(fields);
			return metaModel;
		}
		if(log.isWarnEnabled()){
			log.warn("the model["+resource.getFilename()+"] is null");
		}
	    return null;
	}
	public void afterPropertiesSet() throws Exception {
		loadModel("classpath:drquery.model/mapping/*.xml");
	}
	public static void main(String[] args){
		ModelReader reader = new ModelReader();
		try {
			reader.loadModel("classpath:mapping/*.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Properties prop = PropertiesUtil.getProperties("gbase_sql.properties");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("gbase_sql_after.properties");
			Map<String, MetaModel> models = ModelReader.getMetaModels();
			Properties propinput = new Properties();
			for(Entry<String,MetaModel> entry:models.entrySet()){
				Map<String, Field> fields = entry.getValue().getFields();
				String sql = "SELECT ";
				for(Entry<String,Field> fieldEntry:fields.entrySet()){
					sql+=fieldEntry.getValue().getName()+",";
				}
				sql=sql.substring(0, sql.length()-1)+" FROM";
				String key = entry.getValue().getModelId();
				String value = (String) prop.get(key);
				sql = sql+" "+value;
				propinput.setProperty(key, sql);
			}
			propinput.store(fos, "Copyright (c) Rexwong");   
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
}
