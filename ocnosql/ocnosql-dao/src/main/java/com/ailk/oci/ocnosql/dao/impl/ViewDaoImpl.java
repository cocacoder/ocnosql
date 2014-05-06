package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.hibernate.BaseHibernateDaoImpl;
import com.ailk.oci.ocnosql.dao.spi.ViewDao;
import com.ailk.oci.ocnosql.model.domains.ViewMetadata;

@Service("viewDao")
public class ViewDaoImpl extends BaseHibernateDaoImpl<ViewMetadata, String> implements ViewDao {

	@Override
	public void deleteView(ViewMetadata viewInstance) {
		this.delete(viewInstance);
	}

	@Override
	public ViewMetadata getView(String viewId) {
		return this.getPersistentInstance(viewId);
	}

	@Override
	public List<ViewMetadata> list() {
		return this.loadAll();
	}

	@Override
	public List<ViewMetadata> queryViewByCriteria(Criteria criteria) {
		return this.queryByCriteria(criteria);
	}

	@Override
	public String saveView(ViewMetadata viewInstance) {
		return this.save(viewInstance);
	}

	@Override
	public void updateView(ViewMetadata viewMetadata) {
		this.update(viewMetadata);
	}

	@Override
	protected PKGenerator<String> initKeyGenerator() {
		return null;
	}

}
