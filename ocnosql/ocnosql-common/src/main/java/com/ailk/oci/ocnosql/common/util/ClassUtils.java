package com.ailk.oci.ocnosql.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class ClassUtils {
	/**
	 * 
	 * 通过反射,获得定义Class时声明的父类的泛型参数的类型. 如public UserDao extends HibernateDao<User>
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class getSuperClassGenricType(final Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}
	/**
	 * 通过反射,获得定义Class时声明的父类的泛型参数的类型. 
	 */
	public static Class getSuperClassGenricType(final Class clazz,final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}
	//TODO 性能优化，与cgilib中的反射进行对比
	public static void copyBean(final Object source, Object target){
		if (source == null) {
			throw new UnsupportedOperationException("source bean cannot be null,please check it!!");
		}
		if (target == null) {
			throw new UnsupportedOperationException("target bean cannot be null,please check it!!");
		}
		PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
		PropertyDescriptor[] propertyArr = propertyUtilsBean.getPropertyDescriptors(source);
		String fieldName = null;
		Object propertyvalue = null;
		try {
			for (int i = 0; i < propertyArr.length; i++) {
				fieldName = propertyArr[i].getName();
				if (fieldName.equals("class")) {
					continue;
				}
				propertyvalue = propertyUtilsBean.getSimpleProperty(source,fieldName);
				if (propertyvalue == null) {
					continue;
				}
				propertyUtilsBean.setSimpleProperty(target, fieldName,propertyvalue);
			}
		} 
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
