package com.ailk.oci.ocnosql.common.exception;

/**
 * Created by IntelliJ IDEA.
 * User: lile3
 * Date: 14-3-12
 * Time: 下午7:28
 * To change this template use File | Settings | File Templates.
 */
public class BulkLoadException extends RuntimeException{

    public BulkLoadException(String message) {
        super(message);
    }

    public BulkLoadException() {
        super();
    }
}
