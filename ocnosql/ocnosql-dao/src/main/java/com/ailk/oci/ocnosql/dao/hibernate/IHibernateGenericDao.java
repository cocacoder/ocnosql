package com.ailk.oci.ocnosql.dao.hibernate;

import java.io.Serializable;
import java.util.List;

public interface IHibernateGenericDao<T, PK extends Serializable> {

	PK save(Object transientInstance);

	<E> E getPersistentInstance(Class<E> entityClass, PK id);

	T getPersistentInstance(PK id);

	void delete(Object persistentInstance);

	void update(Object persistentInstance);
	
	<E> List<E> queryByCriteria(com.ailk.oci.ocnosql.dao.criteria.Criteria eqcriteria);
	
	List<T> loadAll();
}
