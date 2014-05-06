package com.ailk.oci.ocnosql.dao.spi;

import java.util.List;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;

/**
 * @author Administrator
 * 表操作DAO
 */
public interface TableDao {
	
	//保存表元数据
	String saveTable(TableMetadata tableInstance);

	//根据ID获得表元数据
	TableMetadata getTable(String tableId);

	//删除表元数据
	void deleteTable(TableMetadata tableInstance);

	//更新表元数据
	void updateTable(TableMetadata tableInstance);

	//根据条件查询表元数据
	List<TableMetadata> queryTableByCriteria(Criteria criteria);
	
	List<TableMetadata> list();
	
	PKGenerator<String> getKeyGenerator();
}
