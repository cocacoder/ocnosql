package com.ailk.oci.ocnosql.dao.spi;

import java.util.List;

import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;
import com.ailk.oci.ocnosql.model.domains.TaskTypeMetadata;

public interface TaskTypeDao {

	public List<TaskTypeMetadata> listTaskType();
	
	public TaskTypeMetadata scanTaskTypeById(String id);
	
	String saveTaskType(TableMetadata taskTypeInstance);

	void deleteTaskType(TaskTypeMetadata taskTypeInstance);

	void updateTaskType(TaskTypeMetadata taskTypeInstance);

	List<TaskTypeMetadata> queryTaskTypeByCriteria(Criteria criteria);
	
	PKGenerator<String> getKeyGenerator();
}
