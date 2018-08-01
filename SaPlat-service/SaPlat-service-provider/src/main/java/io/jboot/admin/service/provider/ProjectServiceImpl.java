package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.AuthProject;
import io.jboot.admin.service.entity.model.LeaderGroup;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ProjectServiceImpl extends JbootServiceBase<Project> implements ProjectService {
    /**
     * find all model
     *
     * @param model 项目主体
     * @return all <Project>
     */
    @Override
    public List<Project> findAll(Project model) {
        Columns columns = Columns.create();
        if (StrKit.notNull(model.getIsEnable())) {
            columns.eq("isEnable", model.getIsEnable());
        }
        if (model.getUserId() != null) {
            columns.eq("userId", model.getUserId());
        }
        if (model.getId() != null) {
            columns.eq("id", model.getId());
        }
        return DAO.findListByColumns(columns);
    }

    @Override
    public List<Project> findListByColumn(String columnName, Object value) {
        return DAO.findListByColumn(Column.create(columnName,value));
    }

    @Override
    public Project findFirstByColumn(String columnName, Object value) {
        return DAO.findFirstByColumn(Column.create(columnName,value));
    }

    @Override
    public Project findFirstByColumns(String[] columnNames, String[] values) {
        Columns columns = Columns.create();
        if (columnNames.length != values.length){
            return null;
        }
        for (int i = 0; i < columnNames.length; i++) {
            columns.eq(columnNames[i],values[i]);
        }
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public List<Project> findListByColumns(String[] columnNames, String[] values) {
        Columns columns = Columns.create();
        if (columnNames.length != values.length){
            return null;
        }

        for (int i = 0; i < columnNames.length; i++) {
            columns.eq(columnNames[i],values[i]);
        }
        return DAO.findListByColumns(columns);
    }

    @Override
    public List<Project> findByIds(List<Object> ids,String[] status) {
        List<String> statusList = Arrays.asList(status);
        List<Project> projects = Collections.synchronizedList(new ArrayList<Project>());
        for (Object id : ids) {
            Project byId = findById(id);
            if (byId != null && byId.getIsEnable() && statusList.contains(byId.getStatus())) {
                projects.add(byId);
            }
        }
        return projects;
    }

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
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<Project> findPageByIsPublic(Project project, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (project.getIsPublic()) {
            columns.eq("isPublic", project.getIsPublic());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean saveOrUpdate(Project model, AuthProject authProject, LeaderGroup leaderGroup) {
        return Db.tx(() -> {
            if (!model.saveOrUpdate()) {
                return false;
            }
            Columns columns = new Columns();
            columns.eq("name", model.getName());
            columns.eq("brief", model.getBrief());
            leaderGroup.setProjectID(DAO.findFirstByColumns(columns).getId());
            authProject.setProjectId(DAO.findFirstByColumns(columns).getId());
            return Db.tx(() -> authProject.save() && leaderGroup.save());
        });
    }

    @Override
    public List<Project> findByIsPublic(boolean isPublic) {
        return DAO.findListByColumn("isPublic", isPublic);
    }
}