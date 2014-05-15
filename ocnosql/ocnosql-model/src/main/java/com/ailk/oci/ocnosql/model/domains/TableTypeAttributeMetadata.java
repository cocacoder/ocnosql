package com.ailk.oci.ocnosql.model.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Administrator
 * 表类型属性
 */
@Entity
@Table(name = "OC_TABLE_TYPE_ATTR")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "JavassistLazyInitializer"})
public class TableTypeAttributeMetadata implements Serializable {
    private static final long serialVersionUID = -4380953349412382822L;
    @Column(name = "TYPE_ATTRIBUTE_ID", nullable = false)
    @Id
    private String id; //ID
    @Column(name = "KEY", nullable = false)
    private String attributeKey; //属性key
    @Column(name = "VALUE", nullable = false)
    private String attributeValue; //属性值
    @Column(name = "DATA_TYPE", nullable = false)
    private String attributeType; //属性类型
    @Column(name = "SHOW_TYPE", nullable = false)
    private String showType; //展示类型
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TABLE_TYPE_ID")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE, CascadeType.REMOVE})
    private TableTypeMetadata tableTypeMeta; //表类型

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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public TableTypeMetadata getTableTypeMeta() {
        return tableTypeMeta;
    }

    public void setTableTypeMeta(TableTypeMetadata tableTypeMeta) {
        this.tableTypeMeta = tableTypeMeta;
    }

}
