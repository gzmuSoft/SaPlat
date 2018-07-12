package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.FacAgencyService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.FacAgency;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class FacAgencyServiceImpl extends JbootServiceBase<FacAgency> implements FacAgencyService {
    @Override
    public FacAgency findByOrgID(Long orgID) {
        return DAO.findFirstByColumn("orgID", orgID);
    }

    @Override
    public Page<FacAgency> findPage(FacAgency model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public FacAgency findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public boolean saveOrUpdate(FacAgency model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }
}