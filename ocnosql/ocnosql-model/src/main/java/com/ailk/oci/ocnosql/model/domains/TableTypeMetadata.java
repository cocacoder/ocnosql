package com.ailk.oci.ocnosql.model.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Administrator
 *	表类型
 */
@Entity
@Table(name = "OC_TABLE_TYPE")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class TableTypeMetadata implements Serializable {
    private static final long serialVersionUID = -6548320385399012878L;
    @Id
    private String id; //类型ID
    @Column(name = "TYPE_NAME", nullable = false)
    private String name; //类型名
    @OneToMany(fetch = FetchType.LAZY, targetEntity = TableTypeAttributeMetadata.class)
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "TABLE_TYPE_ID")
    private Set<TableTypeAttributeMetadata> tableTypeAttrs = new HashSet<TableTypeAttributeMetadata>(); //表类型属性

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

    @JsonIgnore
    public Set<TableTypeAttributeMetadata> getTableTypeAttrs() {
        return tableTypeAttrs;
    }

    public void setTableTypeAttrs(Set<TableTypeAttributeMetadata> tableTypeAttrs) {
        this.tableTypeAttrs = tableTypeAttrs;
    }
}
