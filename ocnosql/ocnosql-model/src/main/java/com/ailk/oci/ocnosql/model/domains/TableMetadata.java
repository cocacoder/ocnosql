package com.ailk.oci.ocnosql.model.domains;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Administrator
 * 表元数据
 */
@Entity
@Table(name = "OC_TABLE")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id")
public class TableMetadata implements Serializable {
    private static final long serialVersionUID = -2367624733032283259L;
    @Id
    String id; //表ID

    @Column(name = "TABLE_NAME", nullable = false)
    String name; //表名称

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = TableTypeMetadata.class)
    @JoinColumn(name = "TABLE_TYPE")
    private TableTypeMetadata type; //表类型

    @ManyToMany(mappedBy = "tables", targetEntity = ViewMetadata.class)
    @Cascade({CascadeType.SAVE_UPDATE})
    private Set<ViewMetadata> views = new HashSet<ViewMetadata>(); //表上的视图

    @Column(name = "TABLE_STATUS", nullable = false)
    String status; //表状态

    @Column(name = "TABLE_COLUMN", nullable = false)
    String columns; //表的列

    @OneToMany(mappedBy = "tableMetadata", fetch = FetchType.LAZY, targetEntity = TableAttributeMetadata.class)
    @Cascade({CascadeType.SAVE_UPDATE})
    private Set<TableAttributeMetadata> tableAttrs = new HashSet<TableAttributeMetadata>(); //表属性

    @OneToMany(mappedBy = "tableMetadata", fetch = FetchType.LAZY, targetEntity = TableOperatorLog.class)
    @Cascade({CascadeType.SAVE_UPDATE})
    private Set<TableOperatorLog> tableOperatorLogs = new HashSet<TableOperatorLog>(); //表操作日志

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

    public TableTypeMetadata getType() {
        return type;
    }

    public void setType(TableTypeMetadata type) {
        this.type = type;
    }

    public Set<ViewMetadata> getViews() {
        return views;
    }

    public void setViews(Set<ViewMetadata> views) {
        this.views = views;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    @JsonIgnore
    public Set<TableAttributeMetadata> getTableAttrs() {
        return tableAttrs;
    }

    public void setTableAttrs(Set<TableAttributeMetadata> tableAttrs) {
        this.tableAttrs = tableAttrs;
    }

    @JsonIgnore
    public Set<TableOperatorLog> getTableOperatorLogs() {
        return tableOperatorLogs;
    }

    public void setTableOperatorLogs(Set<TableOperatorLog> tableOperatorLogs) {
        this.tableOperatorLogs = tableOperatorLogs;
    }
}
