package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.api.ProjectUndertakeService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.ProjectUndertake;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ProjectUndertakeServiceImpl extends JbootServiceBase<ProjectUndertake> implements ProjectUndertakeService {

    @Inject
    private NotificationService notificationService;

    @Override
    public boolean findIsInvite(Long facAgencyID, Long projectID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("facAgencyID", facAgencyID);
        columns.eq("applyOrInvite", true);
        return DAO.findFirstByColumns(columns) != null;
    }

    @Override
    public List<ProjectUndertake> findListByFacAgencyIdAndStatus(Long facAgencyId, String status) {
        Columns columns = Columns.create();
        columns.eq("facAgencyID", facAgencyId);
        columns.eq("status", status);
        return DAO.findListByColumns(columns);
    }

    @Override
    public boolean findIsReceive(Long projectID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("status", 2);
        return DAO.findFirstByColumns(columns) != null;
    }

    @Override
    public Page<ProjectUndertake> findPage(ProjectUndertake projectUndertake, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (projectUndertake.getApplyOrInvite() != null) {
            columns.eq("applyOrInvite", projectUndertake.getApplyOrInvite());
        }
        if (projectUndertake.getIsEnable() != null) {
            columns.eq("isEnable", projectUndertake.getIsEnable());
        }
        if (StrKit.notBlank(projectUndertake.getName())) {
            columns.like("name", "%" + projectUndertake.getName() + "%");
        }
        if (projectUndertake.getFacAgencyID() != null) {
            columns.eq("facAgencyID", projectUndertake.getFacAgencyID());
        }
        if (projectUndertake.getCreateUserID() != null) {
            columns.like("createUserID", "%" + projectUndertake.getCreateUserID() + "%");
        }
        if (projectUndertake.getFacAgencyID() != null) {
            columns.like("facAgencyID", "%" + projectUndertake.getFacAgencyID() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");

    }

    @Override
    public ProjectUndertake findByProjectIdAndCreateUserID(Long id, Long userId) {
        Columns columns = Columns.create();
        columns.eq("projectID", id);
        columns.eq("createUserID", userId);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public ProjectUndertake findByProjectIdAndFacAgencyId(Long projectId, Long facAgencyId) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectId);
        columns.eq("facAgencyID", facAgencyId);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public ProjectUndertake findByProjectIdAndStatus(Long projectId, String projectStatus) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectId);
        columns.eq("status", projectStatus);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public ProjectUndertake findByProjectId(Long projectId) {
        return DAO.findFirstByColumn(Column.create("projectID", projectId));
    }

    @Override
    public boolean saveOrUpdateAndSend(ProjectUndertake model, Notification notification) {
        return Db.tx(() -> saveOrUpdate(model) && notificationService.save(notification));
    }

    @Override
    public List<ProjectUndertake> findListByProjectAndStatus(Long projectID, String status) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("status", status);
        return DAO.findListByColumns(columns);
    }

    @Override
    public float findAllAndUndertakeByUserId(Long userId) {
        Columns columns = Columns.create("createUserID", userId);
        List<ProjectUndertake> list = DAO.findListByColumns(columns);
        float projectAmount = list.size(), undertakeAmount = 0, undertakeRate = 0;
        for (ProjectUndertake projectUndertake : list) {
            if (projectUndertake.getStatus() == 2) {
                undertakeAmount++;
            }
        }
        return projectAmount == 0 ? undertakeRate : undertakeAmount / projectAmount * 100;
    }
}