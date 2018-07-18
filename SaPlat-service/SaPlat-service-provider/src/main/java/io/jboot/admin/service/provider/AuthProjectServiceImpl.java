package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AuthProjectService;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.AuthProject;
import io.jboot.admin.service.entity.model.Project;
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
        if (authProject.getStatus() != null) {
            columns.like("status", "%" + authProject.getStatus() + "%");
        }
        if (authProject.getUserId() != null) {
            columns.eq("userId",authProject.getUserId());
        }
        if(authProject.getName()!=null){
            columns.like("name", "%" + authProject.getName() + "%");
        }
        columns.lt("type","4");
//        if(authProject.getType()!=null){
//
//        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "-status");
    }

    @Override
    public boolean update(AuthProject model){
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                model.setLastUpdTime(new Date());
                Project project=projectService.findById(model.getProjectId());
                project.setStatus(model.getStatus());
                return model.update()&&project.update();
            }
        });
    @Override
    public AuthProject findByProjectId(Object projectId) {
        return DAO.findFirstByColumn("projectId", projectId);
    }
}