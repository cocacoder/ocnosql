package com.ailk.oci.ocnosql.model.domains;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


/**
 * @author Administrator
 *	表属性元数据
 */
@Entity @Table(name="OC_TABLE_ATTRIBUTE")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id")
public class TableAttributeMetadata implements Serializable{
	private static final long serialVersionUID = -8175007253810999981L;
	
	@Column(name="ATTRIBUTE_ID",nullable=false)
	@Id
	private String id;//属性ID
	@Column(name="KEY",nullable=false)
	private String attributeKey;//属性key
	@Column(name="VALUE",nullable=false)
	private String attributeValue;//属性值
	@Column(name="DATA_TYPE",nullable=false)
    private String attributeDesc;//属性描述
    @Column(name="DESCRIPTION",nullable=false)
	private String attributeType;//属性类型
	@Column(name="SHOW_TYPE",nullable=false)
	private String showType;//显示类型
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TABLE_ID")
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.DELETE,CascadeType.REMOVE})
	private TableMetadata tableMetadata; //所属表

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAttributeKey() {
		return attributeKey;
	}
	public void setAttributeKey(String attributeKey) {
		this.attributeKey = attributeKey;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	public String getShowType() {
		return showType;
	}
	public void setShowType(String showType) {
		this.showType = showType;
	}
	public TableMetadata getTableMetadata() {
		return tableMetadata;
	}
	public void setTableMetadata(TableMetadata tableMetadata) {
		this.tableMetadata = tableMetadata;
	}

    public String getAttributeDesc() {
        return attributeDesc;
    }

    public void setAttributeDesc(String attributeDesc) {
        this.attributeDesc = attributeDesc;
    }
}
