package com.ailk.oci.ocnosql.example.model;

import java.io.*;

/**
 * 
 * @author Rex Wong
 *
 * @version
 */
public class Field implements Serializable , Comparable<Field> {
	
	private static final long serialVersionUID = -3884879199227489962L;
	private String name;
	private String cname;
	private String escapeField;
	private boolean commonField;
	private Integer index;
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	private String type;
	private boolean summaryField;
	private boolean mergeField;
	private boolean mergeKey;
	private boolean distinct;
    private boolean defaultField;
    private String mappingField;
    public Field(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getEscapeField() {
		return escapeField;
	}

	public void setEscapeField(String escapeField) {
		this.escapeField = escapeField;
	}

	public boolean isCommonField() {
		return commonField;
	}

	public void setCommonField(boolean commonField) {
		this.commonField = commonField;
	}

	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSummaryField() {
		return summaryField;
	}

	public void setSummaryField(boolean summaryField) {
		this.summaryField = summaryField;
	}

	public boolean isMergeField() {
		return mergeField;
	}

	public void setMergeField(boolean mergeField) {
		this.mergeField = mergeField;
	}

	public boolean isMergeKey() {
		return mergeKey;
	}

	public void setMergeKey(boolean mergeKey) {
		this.mergeKey = mergeKey;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDefaultField() {
		return defaultField;
	}

	public void setDefaultField(boolean defaultField) {
		this.defaultField = defaultField;
	}

	public int compareTo(Field o) {
		return this.getIndex().compareTo(o.getIndex());
	}

	public String getMappingField() {
		return mappingField;
	}

	public void setMappingField(String mappingField) {
		this.mappingField = mappingField;
	}
	
}