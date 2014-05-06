package com.ailk.oci.ocnosql.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.ailk.oci.ocnosql.common.id.DefaultPKGenerator;
import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.common.util.ClassUtils;
import com.ailk.oci.ocnosql.dao.criterion.Criterion;

@SuppressWarnings("unchecked")
public abstract class BaseHibernateDaoImpl<T, PK extends Serializable> implements IHibernateGenericDao<T, PK> {
	private final static Log logger = LogFactory.getLog(BaseHibernateDaoImpl.class.getSimpleName());
	protected PKGenerator keyGenerator = new DefaultPKGenerator();
	@Autowired
	private SessionFactory sessionFactory;
	/**
	 * 设置主键生成策略，如果返回null，则取默认策略.实现类需要继承接口{@link PKGenerator}
	 */
	protected abstract PKGenerator<PK> initKeyGenerator();
	//TODO 这里是否有线程安全问题需要验证
	protected Class<T> entityClass;
	
	private static final int DEFAULT_BATCH_SIZE = 50;
	
	public BaseHibernateDaoImpl() {
		this.entityClass = ClassUtils.getSuperClassGenricType(getClass());
	}
	public BaseHibernateDaoImpl(final Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	public PKGenerator<PK> getKeyGenerator() {
		PKGenerator<PK> keyGenerator = initKeyGenerator();
		if (keyGenerator != null) {
			this.keyGenerator = keyGenerator;
		}
		return this.keyGenerator;
	}
	@Override
	public PK save(Object transientInstance) {
		PK pk = (PK) keyGenerator.emptyPK();
		//TODO 用到了反射，需要优化性能
		this.generateKey2Entity(transientInstance);
		pk =  (PK) sessionFactory.getCurrentSession().save(transientInstance);
		return pk;
	}
	//TODO 老方法，待验证
	public final <E> void batchSave(final Collection<E> transientInstances) {
		if (!CollectionUtils.isEmpty(transientInstances)) {
			Session session = this.sessionFactory.getCurrentSession();
			int max = transientInstances.size();
			int i = 0;
			for (Object t : transientInstances) {
				session.persist(t);
				if ((i != 0 && i % DEFAULT_BATCH_SIZE == 0)| i == max - 1) {
					session.flush();
					session.clear();
				}
				i++;
			}
		}
	}
	private void generateKey2Entity(Object transientInstance){
		this.getKeyGenerator();
		String keyName = this.retrievePKNameByEntityClass(transientInstance.getClass());
		Object keyValue;
		try {
			keyValue = BeanUtils.getProperty(transientInstance, keyName);
			if (keyValue == null) {
				BeanUtils.setProperty(transientInstance, keyName,this.keyGenerator.generateKey());
			}
		} 
		catch (IllegalAccessException e) {
			logger.error("Failed access entity["+transientInstance.getClass().getSimpleName()+"]'s property["+keyName+"]'s set method",e);
		} 
		catch (InvocationTargetException e) {
			logger.error("Failed invoke entity["+transientInstance.getClass().getSimpleName()+"]'s property["+keyName+"]'s set method",e);
		} 
		catch (NoSuchMethodException e) {
			logger.error("Check entity["+transientInstance.getClass().getSimpleName()+"]'s property["+keyName+"]'s set method have been defined");
		}
	}
	private <E> String retrievePKNameByEntityClass(Class<E> clz) {
		ClassMetadata meta = sessionFactory.getClassMetadata(clz);
		Assert.notNull(meta, "Class " + clz.getSimpleName()+ " not define in HibernateSessionFactory.");
		return meta.getIdentifierPropertyName();
	}
	
	public final <E> E getPersistentInstance(Class<E> entityClass, PK id) {
		try {
			return (E) this.sessionFactory.getCurrentSession().get(entityClass, id);
		} catch (DataAccessException e) {
			if (e.getCause().getMessage().indexOf("Unknown entity") != -1) {
				logger.error("No designated entity class==" + e);
			}
		}
		return null;
	}
	public List<T> loadAll(){
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(entityClass);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.setCacheable(true);
		//TODO 需要做成可配置化
		criteria.setTimeout(1000);
		List<T> list = criteria.list();
		if(logger.isDebugEnabled()){
			logger.debug("Return Row ["+list.size()+"] of"+ this.entityClass.getName()+" : load all");
		}
		return list;
	}
	
	public final T getPersistentInstance(PK id) {
		try {
			return (T) this.sessionFactory.getCurrentSession().get(entityClass, id);
		} catch (DataAccessException e) {
			if (e.getCause().getMessage().indexOf("Unknown entity") != -1) {
				throw new IllegalArgumentException("No designated entity class",e);
			}
		}
		return null;
	}
	public final void delete(Object persistentInstance) {
		this.sessionFactory.getCurrentSession().delete(persistentInstance);
	}
	
	public final void update(Object persistentInstance) {
		this.sessionFactory.getCurrentSession().update(persistentInstance);
	}
	
	public final <E> List<E> queryByCriteria(com.ailk.oci.ocnosql.dao.criteria.Criteria mycriteria) {
		Assert.notNull(mycriteria, "Equals Criteria must not be null");
		Criteria hibernatecriteria = this.getCriteria(this.entityClass);
		this.addEqCriteria(hibernatecriteria, mycriteria);
		List<E> list =  hibernatecriteria.list();
		if(logger.isDebugEnabled()){
			logger.debug("Return Row ["+list.size()+"] of"+ this.entityClass.getName()+" : "+mycriteria.toString());
		}
		return list;
	}
	private final Criteria getCriteria(Class<?>... claz) {
		if (claz == null) {
			return this.sessionFactory.getCurrentSession().createCriteria(this.entityClass);
		} 
		else {
			if (claz.length > 1) {
				throw new IllegalArgumentException("the claz's length is just one");
			}
			return this.sessionFactory.getCurrentSession().createCriteria(claz[0]);
		}
	}
	private void addEqCriteria(Criteria hibernatecriteria,com.ailk.oci.ocnosql.dao.criteria.Criteria mycriteria) {
		if (!mycriteria.isEmpty()) {
			Iterator<Criterion> iterator = mycriteria.iterator();
			while(iterator.hasNext()){
				Criterion criterion = iterator.next();
				if (criterion.getValue() != null) {
					if (StringUtils.indexOf(criterion.getKey(), ".") != -1) {
						String alias = StringUtils.split(criterion.getKey(), ".")[0];
						hibernatecriteria.createAlias(alias, alias,JoinType.FULL_JOIN);
					}
					if (StringUtils.equalsIgnoreCase(String.valueOf(criterion.getValue()), "not null")) {
						hibernatecriteria.add(Restrictions.isNotNull(criterion.getKey()));
					} 
					else {
						hibernatecriteria.add(Restrictions.eq(criterion.getKey(), criterion.getValue()));
					}
				} 
				else {
					hibernatecriteria.add(Restrictions.isNull(criterion.getKey()));
				}
			}
		}
	}

}
