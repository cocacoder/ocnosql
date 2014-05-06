package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.hibernate.BaseHibernateDaoImpl;
import com.ailk.oci.ocnosql.dao.spi.TaskTypeDao;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;
import com.ailk.oci.ocnosql.model.domains.TaskTypeMetadata;

@Service("taskTypeDao")
public class TaskTypeDaoImpl extends BaseHibernateDaoImpl<TaskTypeMetadata, String> implements
		TaskTypeDao {

	@Override
	protected PKGenerator<String> initKeyGenerator() {
		return null;
	}

	@Override
	public void deleteTaskType(TaskTypeMetadata taskTypeInstance) {
		this.delete(taskTypeInstance);
	}

	@Override
	public List<TaskTypeMetadata> listTaskType() {
		return this.loadAll();
	}

	@Override
	public List<TaskTypeMetadata> queryTaskTypeByCriteria(Criteria criteria) {
		return this.queryByCriteria(criteria);
	}

	@Override
	public String saveTaskType(TableMetadata taskTypeInstance) {
		return this.save(taskTypeInstance);
	}

	@Override
	public TaskTypeMetadata scanTaskTypeById(String id) {
		return this.getPersistentInstance(id);
	}

	@Override
	public void updateTaskType(TaskTypeMetadata taskTypeInstance) {
		this.update(taskTypeInstance);
	}

}
