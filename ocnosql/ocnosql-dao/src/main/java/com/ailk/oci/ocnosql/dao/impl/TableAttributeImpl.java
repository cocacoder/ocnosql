package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.hibernate.BaseHibernateDaoImpl;
import com.ailk.oci.ocnosql.dao.spi.TableAttributeDao;
import com.ailk.oci.ocnosql.model.domains.TableAttributeMetadata;

@Service("tableAttributeDao")
public class TableAttributeImpl extends BaseHibernateDaoImpl<TableAttributeMetadata, String> implements TableAttributeDao {

	@Override
	protected PKGenerator<String> initKeyGenerator() {
		return null;
	}

	@Override
	public String saveTableAttributeMetadata(TableAttributeMetadata tableAttributeInstance) {
		return this.save(tableAttributeInstance);
	}

	@Override
	public TableAttributeMetadata getTableAttributeMetadataInstance(String id) {
		return this.getPersistentInstance(id);
	}

	@Override
	public void deleteTableAttributeMetadata(TableAttributeMetadata tableAttributeInstance) {
		this.delete(tableAttributeInstance);
		
	}

	@Override
	public void updateTableAttributeMetadata(TableAttributeMetadata tableAttributeInstance) {
		this.update(tableAttributeInstance);
	}

	@Override
	public List<TableAttributeMetadata> queryTableAttributeMetadataByCriteria(Criteria criteria) {
		return this.queryByCriteria(criteria);
	}
}
