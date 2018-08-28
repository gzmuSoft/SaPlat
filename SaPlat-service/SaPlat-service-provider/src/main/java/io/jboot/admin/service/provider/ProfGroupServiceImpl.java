package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.api.ProfGroupService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.ProfGroup;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ProfGroupServiceImpl extends JbootServiceBase<ProfGroup> implements ProfGroupService {
    @Inject
    private NotificationService notificationService;

    @Override
    public ProfGroup findByOrgId(Long orgId) {
        return DAO.findFirstByColumn("orgID", orgId);
    }

    @Override
    public ProfGroup findByCreateUserID(Object createUserID) {
        return DAO.findFirstByColumn("createUserID", createUserID);
    }


    @Override
    public boolean saveOrUpdate(ProfGroup model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean saveOrUpdate(ProfGroup model, Auth auth, Notification notification) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate() && notificationService.saveOrUpdate(notification));
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public ProfGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public Page<ProfGroup> findPage(ProfGroup profGroup, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (null != profGroup.getName()) {
            columns.like("name", "%" + profGroup.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }
}