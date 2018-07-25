package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ProjectUndertakeService;
import io.jboot.admin.service.entity.model.ProjectUndertake;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import javax.inject.Inject;

@Bean
@Singleton
@JbootrpcService
public class ProjectUndertakeServiceImpl extends JbootServiceBase<ProjectUndertake> implements ProjectUndertakeService {

    @Inject
    private NotificationService notificationService;

    @Override
    public boolean findIsInvite(Long facAgencyID, Long projectID) {
        Columns columns = new Columns();
        columns.eq("projectID", projectID);
        columns.eq("facAgencyID", facAgencyID);
        columns.eq("applyOrInvite", true);
        if (DAO.findFirstByColumns(columns) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean findIsReceive(Long projectID) {
        Columns columns = new Columns();
        columns.eq("projectID", projectID);
        columns.eq("status", 2);
        if (DAO.findFirstByColumns(columns) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<ProjectUndertake> findPage(ProjectUndertake project, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (project.getApplyOrInvite() != null) {
            columns.eq("applyOrInvite", project.getApplyOrInvite());
        }
        if (project.getIsEnable() != null) {
            columns.eq("isEnable", project.getIsEnable());
        }
        if (StrKit.notBlank(project.getName())) {
            columns.like("name", "%" + project.getName() + "%");
        }
        if (project.getCreateUserID() != null) {
            columns.like("createUserID", "%" + project.getCreateUserID() + "%");
        }
        if (project.getFacAgencyID() != null) {
            columns.like("facAgencyID", "%" + project.getFacAgencyID() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");

    }

    @Override
    public ProjectUndertake findByProjectIdAndFacAgencyId(Long projectId, Long facAgencyId) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectId);
        columns.eq("facAgencyID", facAgencyId);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public boolean saveOrUpdateAndSend(ProjectUndertake model, Notification notification) {
        return Db.tx(() -> saveOrUpdate(model) && notificationService.save(notification));
    }
}