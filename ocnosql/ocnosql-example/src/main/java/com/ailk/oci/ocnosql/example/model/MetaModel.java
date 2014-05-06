package com.ailk.oci.ocnosql.example.model;

import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;

/**
 * @author Rex Wong
 * 
 *         18 Apr 2012
 * @version
 */
public class MetaModel implements Serializable{
	private static final long serialVersionUID = 5476537497209662498L;
	private Map<String, Field> fields;
	private String modelId;
	private String table;
	private String name;
	private String process;
	private String statType;
	private String statRule;
	private  String[] mergeFields;
	private  String[] mergeKeys;
	private  String[] summaryFields;
	private  String[] distinctFields;
	private  Map<String, Field> commonFields;
	private  Map<String, Field> escapeFields;
	private  Map<String, Field> queryFields;
	
	private String billType;

	public String[] getMergeFields(){
		if(ArrayUtils.isEmpty(mergeFields)){
			List<String> tempmergeFields = new ArrayList<String>();
			for(Entry<String,Field> entry:fields.entrySet()){
				Field field = entry.getValue();
				if(field.isMergeField()){
					tempmergeFields.add(field.getName());
				}
			}
			mergeFields = new String[tempmergeFields.size()];
			for(int i =0;i<tempmergeFields.size();i++){
				mergeFields[i]=tempmergeFields.get(i);
			}
		}
		return mergeFields;
	}
	

	public String[] getMergeKeys(){
		if(ArrayUtils.isEmpty(mergeKeys)){
			List<String> tempmergeKeys = new ArrayList<String>(); 
			for(Entry<String,Field> entry:fields.entrySet()){
				Field field = entry.getValue();
				if(field.isMergeKey()){
					tempmergeKeys.add(field.getName());
				}
			}
			mergeKeys = new String[tempmergeKeys.size()];
			for(int i =0;i<tempmergeKeys.size();i++){
				mergeKeys[i]=tempmergeKeys.get(i);
			}
		}
		return mergeKeys;
	}
	

	public String[] getSummaryFields(){
		if(ArrayUtils.isEmpty(summaryFields)){
			List<String> tempsummaryFields = new ArrayList<String>(); 
			for(Entry<String,Field> entry:fields.entrySet()){
				Field field = entry.getValue();
				if(field.isSummaryField()){
					tempsummaryFields.add(field.getName());
				}
			}
			summaryFields = new String[tempsummaryFields.size()];
			for(int i =0;i<tempsummaryFields.size();i++){
				summaryFields[i]=tempsummaryFields.get(i);
			}
		}
		return summaryFields;
	}

	public String[] getDistinctFields(){
		if(ArrayUtils.isEmpty(distinctFields)){
			List<String> tempsdistinctFields = new ArrayList<String>(); 
			for(Entry<String,Field> entry:fields.entrySet()){
				Field field = entry.getValue();
				if(field.isDistinct()){
					tempsdistinctFields.add(field.getName());
				}
			}
			distinctFields = new String[tempsdistinctFields.size()];
			for(int i =0;i<tempsdistinctFields.size();i++){
				distinctFields[i]=tempsdistinctFields.get(i);
			}
		}
		return distinctFields;
	}

	public Map<String, Field> getCommonFields(){
		if(MapUtils.isEmpty(commonFields)){
			commonFields = new HashMap<String,Field>(); 
			for(Entry<String,Field> entry:fields.entrySet()){
				Field field = entry.getValue();
				if(field.isCommonField()&&StringUtils.isNotEmpty(field.getCname())){
					commonFields.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return commonFields;
	}

	public Map<String, Field> getEscapeFields(){
		if(MapUtils.isEmpty(escapeFields)){
			escapeFields = new HashMap<String,Field>(); 
			for(Entry<String,Field> entry:fields.entrySet()){
				Field field = entry.getValue();
				if(StringUtils.isNotEmpty(field.getEscapeField())){
					escapeFields.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return escapeFields;
	}
	

	public Map<String, Field> getQueryFields() {
		if(MapUtils.isEmpty(queryFields)){
			queryFields = new HashMap<String,Field>(); 
			for(Entry<String,Field> entry:fields.entrySet()){
				Field field = entry.getValue();
				if(field.isMergeField()||field.isMergeKey()
						||field.isCommonField()
						||field.isDistinct()
						||field.isSummaryField()
						||StringUtils.isNotEmpty(field.getEscapeField())
						||field.isDefaultField()){
					queryFields.put(field.getName(),field);
				}
			}
		}
		return queryFields;
	}
	
	public Map<String, Field> getFields() {
		return fields;
	}

	public void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getStatRule() {
		return statRule;
	}

	public void setStatRule(String statRule) {
		this.statRule = statRule;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}
	
	
}
