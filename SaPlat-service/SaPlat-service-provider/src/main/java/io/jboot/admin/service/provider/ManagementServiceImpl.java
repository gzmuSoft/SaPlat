package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ManagementService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.Management;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ManagementServiceImpl extends JbootServiceBase<Management> implements ManagementService {
    @Override
    public Management findByOrgId(Long orgId) {
        return DAO.findFirstByColumn("orgID", orgId);
    }

    @Override
    public boolean saveOrUpdate(Management model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public Management findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }


    @Override
    public Page<Management> findPage(Management management, int pageNumber, int pageSize){
        Columns columns = Columns.create();
        if(null!=management.getName()){
            columns.like("name", "%" + management.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }
}