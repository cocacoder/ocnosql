package com.ailk.oci.ocnosql.dao.criteria;

import java.util.Iterator;

import org.junit.Test;

import com.ailk.oci.ocnosql.dao.criterion.Criterion;

public class CriteriaTest {

	@Test
	public void testIterator() {
		Criteria criteria = new Criteria();
		criteria.addEqCriterion("a", "1");
		criteria.addEqCriterion("b", "2");
		criteria.addEqCriterion("b", "2");
		criteria.addEqCriterion("c", "3");
		criteria.addEqCriterion("b", "2");
		criteria.addEqCriterion("d", "4");
		Iterator<Criterion> iterator = criteria.iterator();
		while(iterator.hasNext()){
			Criterion criterion = iterator.next();
			System.out.println(criterion.toString());
		}
	}

}
