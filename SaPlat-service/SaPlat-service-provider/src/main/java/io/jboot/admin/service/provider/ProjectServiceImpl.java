package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.AuthProject;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ProjectServiceImpl extends JbootServiceBase<Project> implements ProjectService {

    @Override
    public boolean saveOrUpdate(Project model, AuthProject authProject) {
        return Db.tx(() -> {
            if (!model.saveOrUpdate()) {
                return false;
            }
            Columns columns = new Columns();
            columns.eq("name", model.getName());
            columns.eq("brief", model.getBrief());
            authProject.setProjectId(DAO.findFirstByColumns(columns).getId());
            return authProject.saveOrUpdate();
        });
    }


    @Override
    public Page<Project> findPage(Project project, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (project.getUserId() != 0 && StrKit.notBlank(project.getStatus())) {
            columns.like("status", "%" + project.getStatus() + "%");
            columns.like("userId", "%" + project.getUserId() + "%");
            columns.like("isEnable", "%" + project.getIsEnable() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "lastAccessTime desc");
    }


}