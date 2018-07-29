package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.InformationFillService;
import io.jboot.admin.service.entity.model.InformationFill;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class InformationFillServiceImpl extends JbootServiceBase<InformationFill> implements InformationFillService {

    @Override
    public List<InformationFill> findByRoleId(Long roleId) {
        Columns columns = new Columns();
        columns.eq("isEnable",1);
        columns.eq("roleID",roleId);
        return DAO.findListByColumns(columns);
    }
}