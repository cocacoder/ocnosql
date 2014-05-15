package com.ailk.oci.ocnosql.model.domains;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author Administrator
 * 视图元数据
 */
@Entity @Table(name="OC_VIEW")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ViewMetadata implements Serializable{
	private static final long serialVersionUID = 8442814385217238625L;

	@Id
	private String id;//ID
	
	@Column(name = "VIEW_NAME", nullable = false)
	private String name; //视图名称
	
	@ManyToMany(targetEntity=TableMetadata.class)  
	@JoinTable(name="OC_VIEW_TABLE",joinColumns=@JoinColumn(name="VIEW_ID"),
			inverseJoinColumns=@JoinColumn(name="TABLE_ID")) 
	@Cascade({CascadeType.SAVE_UPDATE})
	private Set<TableMetadata> tables = new HashSet<TableMetadata>(); //视图基于的表

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<TableMetadata> getTables() {
		return tables;
	}

	public void setTables(Set<TableMetadata> tables) {
		this.tables = tables;
	}
	
	
}
