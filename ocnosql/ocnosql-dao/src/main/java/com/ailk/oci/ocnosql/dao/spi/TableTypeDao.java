package com.ailk.oci.ocnosql.dao.spi;

import java.util.List;

import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.model.domains.TableTypeMetadata;

public interface TableTypeDao {
	
	String saveTableType(TableTypeMetadata tableTypeInstance);

	TableTypeMetadata getTableType(String tableTypeId);

	void deleteTableType(TableTypeMetadata tableTypeInstance);

	void updateTableType(TableTypeMetadata tableTypeInstance);

	List<TableTypeMetadata> queryTableTypeByCriteria(Criteria mycriteria);
	
	List<TableTypeMetadata> list();
}
