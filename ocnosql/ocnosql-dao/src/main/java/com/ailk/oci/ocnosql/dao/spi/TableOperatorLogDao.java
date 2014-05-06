package com.ailk.oci.ocnosql.dao.spi;

import com.ailk.oci.ocnosql.model.domains.TableMetadata;
import com.ailk.oci.ocnosql.model.domains.TableOperatorLog;


/**
 * @author Administrator
 * 表操作日志DAO
 */
public interface TableOperatorLogDao {
	//保存表操作日志
    String saveTableOperatorLog(TableOperatorLog tableOperatorLogInstance);
    //追加表操作日志
    void addTableOperatorLogViaLogContent(TableMetadata tableMetadata, String logContent);
}
