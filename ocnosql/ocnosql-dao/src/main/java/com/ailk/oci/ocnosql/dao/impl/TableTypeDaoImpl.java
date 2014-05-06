package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.hibernate.BaseHibernateDaoImpl;
import com.ailk.oci.ocnosql.dao.spi.TableTypeDao;
import com.ailk.oci.ocnosql.model.domains.TableTypeMetadata;

@Service("tableTypeDao")
public class TableTypeDaoImpl extends BaseHibernateDaoImpl<TableTypeMetadata, String> implements TableTypeDao {

	@Override
	protected PKGenerator<String> initKeyGenerator() {
		return null;
	}
	@Override
	public void deleteTableType(TableTypeMetadata tableTypeInstance) {
		this.delete(tableTypeInstance);
	}

	@Override
	public TableTypeMetadata getTableType(String tableTypeId) {
		return this.getPersistentInstance(tableTypeId);
	}

	@Override
	public List<TableTypeMetadata> list() {
		return this.loadAll();
	}

	@Override
	public List<TableTypeMetadata> queryTableTypeByCriteria(Criteria mycriteria) {
		return this.queryByCriteria(mycriteria);
	}

	@Override
	public String saveTableType(TableTypeMetadata tableTypeInstance) {
		return this.save(tableTypeInstance);
	}

	@Override
	public void updateTableType(TableTypeMetadata tableTypeInstance) {
		this.update(tableTypeInstance);
	}

}
