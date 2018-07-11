package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AuthService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ExpertGroupService;
import io.jboot.admin.service.entity.model.ExpertGroup;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ExpertGroupServiceImpl extends JbootServiceBase<ExpertGroup> implements ExpertGroupService {

    @Inject
    private AuthService authService;

    @Override
    public Page<ExpertGroup> findPage(ExpertGroup model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())){
            columns.like("name", "%" + model.getName()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean hasExpertGroup(String name) {
        return findByName(name) != null;
    }

    @Override
    public ExpertGroup findByPersonId(Long id) {
        return DAO.findFirstByColumn("personID", id);
    }

    @Override
    public boolean saveOrUpdateExpertGroupAndAuth(ExpertGroup model, Auth auth) {
        return Db.tx(() -> saveOrUpdate(model) && authService.saveOrUpdate(auth));
    }

    @Override
    public ExpertGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }
}