package com.ailk.oci.ocnosql.dao.spi;

import java.util.List;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.model.domains.TableAttributeMetadata;

/**
 * @author Administrator
 * 表属性dao
 */
public interface TableAttributeDao{

	//保存表属性元数据
	String saveTableAttributeMetadata(TableAttributeMetadata tableAttributeInstance);

	//根据ID获取表属性元数据
	TableAttributeMetadata getTableAttributeMetadataInstance(String id);

	//删除表属性元数据
	void deleteTableAttributeMetadata(TableAttributeMetadata tableAttributeInstance);

	//更新表属性
	void updateTableAttributeMetadata(TableAttributeMetadata tableAttributeInstance);
	
	//根据条件查询表属性元数据
	List<TableAttributeMetadata> queryTableAttributeMetadataByCriteria(Criteria criteria);
	PKGenerator<String> getKeyGenerator();
}
