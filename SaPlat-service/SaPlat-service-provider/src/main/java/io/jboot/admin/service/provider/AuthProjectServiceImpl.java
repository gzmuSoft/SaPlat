package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AuthProjectService;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.AuthProject;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.Date;

@Bean
@Singleton
@JbootrpcService
public class AuthProjectServiceImpl extends JbootServiceBase<AuthProject> implements AuthProjectService {

    @Inject
    private ProjectService projectService;

    @Override
    public Page<AuthProject> findPage(AuthProject authProject, int pageNumber, int pageSize){
        Columns columns = Columns.create();
        if(authProject.getStartTime()!=null){
            columns.ge("lastUpdTime",authProject.getStartTime());
        }
        if (authProject.getEntTime()!=null){
            columns.lt("lastUpdTime",authProject.getEntTime());
        }
        if (authProject.getStatus() != null) {
            columns.like("status", "%" + authProject.getStatus() + "%");
        }
        else {
            columns.le("status","3");
        }
        if (authProject.getUserId() != null) {
            columns.eq("userId",authProject.getUserId());
        }
        if(authProject.getName()!=null){
            columns.like("name", "%" + authProject.getName() + "%");
        }
        columns.lt("type","4");
        if(authProject.getRoleId()!=null){
            columns.eq("roleId",authProject.getRoleId());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "status desc,lastUpdTime desc");
    }

    @Override
    public boolean update(AuthProject model) {
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Notification notification = new Notification();
                notification.setName("立项审核通知 ");
                notification.setSource("/app/projectAuth/verifyPostupdate");
                if (model.getStatus().equals(AuthStatus.IS_VERIFY)) {
                    notification.setContent("您好,您的项目《" + projectService.findById(model.getProjectId()).getName() + "》立项成功");
                } else {
                    notification.setContent("您好,您的项目《" + projectService.findById(model.getProjectId()).getName() + "》立项失败");
                }
                notification.setReceiverID(Math.toIntExact(model.getUserId()));
                notification.setCreateUserID(Long.parseLong(model.getRemark()));
                notification.setCreateTime(new Date());
                notification.setLastUpdateUserID(Long.parseLong(model.getRemark()));
                notification.setLastAccessTime(new Date());
                notification.setIsEnable(true);
                notification.setStatus(0);
                model.setRemark(null);
                if (!notification.save()) {
                    return false;
                }

                model.setLastUpdTime(new Date());
                Project project = projectService.findById(model.getProjectId());
                project.setIsEnable(true);
                project.setStatus(model.getStatus());
                return model.update() && project.update();
            }
        });
    }
    @Override
    public AuthProject findByProjectId(Object projectId) {
        return DAO.findFirstByColumn("projectId", projectId);
    }
}