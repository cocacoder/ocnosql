package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.hibernate.BaseHibernateDaoImpl;
import com.ailk.oci.ocnosql.dao.spi.TableTypeAttributeMetadataDao;
import com.ailk.oci.ocnosql.model.domains.TableTypeAttributeMetadata;

@Service("tableTypeAttrDao")
public class TableTypeAttributeMetadataDaoImpl extends BaseHibernateDaoImpl<TableTypeAttributeMetadata, String> implements TableTypeAttributeMetadataDao {

	@Override
	protected PKGenerator<String> initKeyGenerator() {
		return null;
	}

	@Override
	public void deleteTableTypeAttr(TableTypeAttributeMetadata tableTypeAttrIntance) {
		this.delete(tableTypeAttrIntance);
	}

	@Override
	public TableTypeAttributeMetadata getTableTypeAttr(String tableTypeAttrId) {
		return this.getPersistentInstance(tableTypeAttrId);
	}

	@Override
	public List<TableTypeAttributeMetadata> list() {
		return this.loadAll();
	}

	@Override
	public List<TableTypeAttributeMetadata> queryTableTypeAttributeByCriteria(Criteria criteria) {
		return this.queryByCriteria(criteria);
	}

	@Override
	public String saveTableTypeAttr(TableTypeAttributeMetadata tableTypeAttrIntance) {
		return this.save(tableTypeAttrIntance);
	}

	@Override
	public void updateTableTypeAttr(TableTypeAttributeMetadata tableTypeAttrIntance) {
		this.update(tableTypeAttrIntance);
	}

}
