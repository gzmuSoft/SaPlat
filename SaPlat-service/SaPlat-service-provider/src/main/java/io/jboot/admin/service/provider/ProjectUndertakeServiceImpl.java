package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.api.ProjectUndertakeService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.ProjectUndertake;
import io.jboot.aop.annotation.Bean;
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
    public boolean saveOrUpdateAndSend(ProjectUndertake model, Notification notification) {
        return Db.tx(() -> saveOrUpdate(model) && notificationService.save(notification));
    }
}