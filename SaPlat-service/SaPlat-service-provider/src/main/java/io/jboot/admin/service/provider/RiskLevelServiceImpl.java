package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.RiskLevel;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.RiskLevelService;
import io.jboot.admin.service.entity.model.RiskLevel;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class RiskLevelServiceImpl extends JbootServiceBase<RiskLevel> implements RiskLevelService {

    @Override
    public Page<RiskLevel> findPage(RiskLevel model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())){
            columns.like("name", "%" + model.getName()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public RiskLevel findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }


}