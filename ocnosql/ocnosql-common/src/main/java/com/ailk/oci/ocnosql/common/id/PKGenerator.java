package com.ailk.oci.ocnosql.common.id;

public interface PKGenerator<T> {
	T generateKey();
	T emptyPK();
}
