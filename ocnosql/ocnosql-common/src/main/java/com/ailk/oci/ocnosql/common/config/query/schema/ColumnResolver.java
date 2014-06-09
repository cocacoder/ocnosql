package com.ailk.oci.ocnosql.common.config.query.schema;

import org.apache.hadoop.hbase.client.HTable;

/**
 * User: Rex wong
 * Date: 13-4-11
 * Time: 上午11:09
 * version
 * since 1.4
 */
public interface ColumnResolver {
    public OciColumn resolveColumn (HTable table,int columnIndex);
}
