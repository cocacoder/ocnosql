package com.ailk.oci.ocnosql.dao.spi;

import java.util.List;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.model.domains.TableTypeAttributeMetadata;

public interface TableTypeAttributeMetadataDao {
	
	String saveTableTypeAttr(TableTypeAttributeMetadata tableTypeAttrIntance);

	TableTypeAttributeMetadata getTableTypeAttr(String tableTypeAttrId);

	void deleteTableTypeAttr(TableTypeAttributeMetadata tableTypeAttrIntance);

	void updateTableTypeAttr(TableTypeAttributeMetadata tableTypeAttrIntance);

	List<TableTypeAttributeMetadata> queryTableTypeAttributeByCriteria(Criteria criteria);
	
	List<TableTypeAttributeMetadata> list();

    PKGenerator<String> getKeyGenerator();
}
