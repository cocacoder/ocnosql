package com.ailk.oci.ocnosql.model.domains;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author Administrator
 *	表操作日志
 */
@Entity @Table(name="OC_TABLE_OPERATORLOG")
public class TableOperatorLog implements Serializable{
	private static final long serialVersionUID = 915402484745789421L;

	@Column(name="OPERATOR_ID",nullable=false)
	@Id
	private String id; //ID
	@Column(name="OPERATOR_TIME",nullable=false)
	private String operatorTime; //操作时间
	@Column(name="OPERATOR_CONTENT",nullable=false)
	private String content; //操作内容
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TABLE_ID")
	@Cascade({CascadeType.REMOVE})
	private TableMetadata tableMetadata; //操作的元数据
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOperatorTime() {
		return operatorTime;
	}
	public void setOperatorTime(String operatorTime) {
		this.operatorTime = operatorTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TableMetadata getTableMetadata() {
		return tableMetadata;
	}
	public void setTableMetadata(TableMetadata tableMetadata) {
		this.tableMetadata = tableMetadata;
	}
	
}
