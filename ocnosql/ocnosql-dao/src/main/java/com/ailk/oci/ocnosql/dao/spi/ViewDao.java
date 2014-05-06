package com.ailk.oci.ocnosql.dao.spi;

import java.util.List;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.model.domains.ViewMetadata;

public interface ViewDao {

	String saveView(ViewMetadata viewInstance);

	ViewMetadata getView(String viewId);

	void deleteView(ViewMetadata viewInstance);

	void updateView(ViewMetadata viewMetadata);

	List<ViewMetadata> queryViewByCriteria(Criteria criteria);
	
	List<ViewMetadata> list();
	
	PKGenerator<String> getKeyGenerator();
}
