package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.hibernate.BaseHibernateDaoImpl;
import com.ailk.oci.ocnosql.dao.spi.TableDao;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;

@Service("tableDao")
public class TableDaoImpl extends BaseHibernateDaoImpl<TableMetadata, String> implements TableDao {
	@Override
	protected PKGenerator<String> initKeyGenerator() {
		return null;
	}

	@Override
	public String saveTable(TableMetadata tableInstance) {
		return this.save(tableInstance);
	}

	@Override
	public TableMetadata getTable(String tableId) {
		return this.getPersistentInstance(tableId);
	}

	@Override
	public void deleteTable(TableMetadata tableInstance) {
		this.delete(tableInstance);
	}

	@Override
	public void updateTable(TableMetadata tableInstance) {
		this.update(tableInstance);
		
	}

	@Override
	public List<TableMetadata> queryTableByCriteria(Criteria criteria) {
		return this.queryByCriteria(criteria);
	}

	@Override
	public List<TableMetadata> list() {
		return this.loadAll();
	}

}
