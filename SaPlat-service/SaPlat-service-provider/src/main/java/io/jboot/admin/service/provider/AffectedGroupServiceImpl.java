package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AuthService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.AffectedGroupService;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class AffectedGroupServiceImpl extends JbootServiceBase<AffectedGroup> implements AffectedGroupService {

    @Inject
    private AuthService authService;

    @Override
    public Page<AffectedGroup> findPage(AffectedGroup model, int pageNumber, int pageSize) {
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
    public AffectedGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public AffectedGroup findByPersonId(Long userID) {
        return DAO.findFirstByColumn("personID", userID);
    }

    @Override
    public boolean saveOrUpdate(AffectedGroup model, Auth auth) {
        return Db.tx(() -> saveOrUpdate(model) && authService.saveOrUpdate(auth));
    }
}