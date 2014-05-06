package com.ailk.oci.ocnosql.dao.criteria;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.oci.ocnosql.dao.criterion.Criterion;


/**
 * @author Administrator
 * 查询过滤条件组合 
 */
public class Criteria {
	private final static Log log = LogFactory.getLog(Criteria.class.getSimpleName());
	private String entityName;
	private List<Criterion> criterionEntries=new ArrayList<Criterion>();
	
	public String getEntityName() {
		return entityName;
	}


	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public void addEqCriterion(String key,String value){
		Criterion criterion = new Criterion(key,value);
		if(criterionEntries.contains(criterion)){
			log.warn(criterion.toString()+" have exist in criteria");
			return;
		}
		criterionEntries.add(criterion);
	}

	public Iterator<Criterion> iterator() {
		return criterionEntries.iterator();
	}

	public boolean isEmpty() {
		return criterionEntries.isEmpty();
	}
	@Override
	public String toString() {
		return "Criteria [entityName=" + entityName + ", criterionEntries="+ criterionEntries + "]";
	}
}
