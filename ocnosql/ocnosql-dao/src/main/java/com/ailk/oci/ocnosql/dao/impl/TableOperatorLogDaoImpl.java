package com.ailk.oci.ocnosql.dao.impl;


import com.ailk.oci.ocnosql.common.id.PKGenerator;
import com.ailk.oci.ocnosql.common.util.DateUtil;
import com.ailk.oci.ocnosql.dao.hibernate.BaseHibernateDaoImpl;
import com.ailk.oci.ocnosql.dao.spi.TableOperatorLogDao;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;
import com.ailk.oci.ocnosql.model.domains.TableOperatorLog;
import org.springframework.stereotype.Service;

@Service("tableOperatorLogDao")
public class TableOperatorLogDaoImpl extends BaseHibernateDaoImpl<TableOperatorLog, String> implements TableOperatorLogDao{
    /**
     * 设置主键生成策略，如果返回null，则取默认策略.实现类需要继承接口{@link com.ailk.oci.ocnosql.common.id.PKGenerator}
     */
    @Override
    protected PKGenerator<String> initKeyGenerator() {
        return null;
    }
    @Override
    public String saveTableOperatorLog(TableOperatorLog tableOperatorLogInstance) {
        return this.save(tableOperatorLogInstance);
    }
    @Override
    public void addTableOperatorLogViaLogContent(TableMetadata tableMetadata, String logContent){
        TableOperatorLog tableOperatorLog = new TableOperatorLog();
        tableOperatorLog.setContent(logContent);
        tableOperatorLog.setOperatorTime(DateUtil.getCurrentTimestamp());
        tableOperatorLog.setId(this.getKeyGenerator().generateKey());
        tableOperatorLog.setTableMetadata(tableMetadata);
        this.saveTableOperatorLog(tableOperatorLog);
    }
}
