package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.*;
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
        return DAO.findListByColumn(Column.create(columnName, value));
    }

    @Override
    public Project findFirstByColumn(String columnName, Object value) {
        return DAO.findFirstByColumn(Column.create(columnName, value));
    }

    @Override
    public Project findFirstByColumns(String[] columnNames, String[] values) {
        Columns columns = Columns.create();
        if (columnNames.length != values.length) {
            return null;
        }
        for (int i = 0; i < columnNames.length; i++) {
            columns.eq(columnNames[i], values[i]);
        }
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public List<Project> findListByColumns(String[] columnNames, String[] values) {
        Columns columns = Columns.create();
        if (columnNames.length != values.length) {
            return null;
        }

        for (int i = 0; i < columnNames.length; i++) {
            columns.eq(columnNames[i], values[i]);
        }
        return DAO.findListByColumns(columns);
    }

    @Override
    public List<Project> findByIds(List<Object> ids, String[] status) {
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
            authProject.setProjectId(model.getId());

            return authProject.saveOrUpdate();
        });
    }

    @Override
    public boolean saveOrUpdate(Project model, LeaderGroup leaderGroup) {
        return Db.tx(() -> model.saveOrUpdate() && leaderGroup.saveOrUpdate());
    }

    @Override
    public boolean update(Project model, ApplyInvite applyInvite) {
        return Db.tx(() -> model.update() && applyInvite.update());
    }

    @Override
    public Page<Project> findPage(Project project, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (project.getUserId() != null && project.getUserId() != 0) {
            columns.like("userId", "%" + project.getUserId() + "%");
        }
        if (StrKit.notBlank(project.getStatus())) {
            columns.like("status", "%" + project.getStatus() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<Project> findPageByIsPublic(long userId, Project project, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if ((project.getMinAmount() != 0.0) || project.getMaxAmount() != 0.0) {
            columns.ge("amount", project.getMinAmount());
            columns.le("amount", project.getMaxAmount());
        }
        if (project.getIsPublic() != null) {
            columns.eq("isPublic", project.getIsPublic());
        }
        if (project.getIsEnable() != null) {
            columns.eq("isEnable", project.getIsEnable());
        }
        if (project.getStatus() != null) {
            columns.eq("status", project.getStatus());
        }
        columns.ne("userId", userId);

        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public List<Project> findListByProjectUndertakeListAndStatus(List<ProjectUndertake> projectUndertakeList, String status) {
        List<Project> list = Collections.synchronizedList(new ArrayList<Project>(projectUndertakeList.size()));
        for (ProjectUndertake projectUndertake : projectUndertakeList) {
            Columns columns = Columns.create();
            columns.eq("status", status);
            columns.eq("id", projectUndertake.getProjectID());
            list.add(DAO.findFirstByColumns(columns));
        }
        return list;
    }

    @Override
    public boolean saveOrUpdate(Project model, AuthProject authProject, LeaderGroup leaderGroup) {
        return Db.tx(() -> {
            if (!model.saveOrUpdate()) {
                return false;
            }
            leaderGroup.setProjectID(model.getId());
            authProject.setProjectId(model.getId());

            return Db.tx(() -> authProject.saveOrUpdate() && leaderGroup.saveOrUpdate());
        });
    }

    @Override
    public boolean saveOrUpdate(Project model, AuthProject authProject, ProjectUndertake projectUndertake) {
        return Db.tx(() -> {
            if (!model.saveOrUpdate()) {
                return false;
            }
            if (projectUndertake == null) {
                authProject.setProjectId(model.getId());
                return Db.tx(() -> authProject.saveOrUpdate());
            } else {
                authProject.setProjectId(model.getId());
                projectUndertake.setProjectID(model.getId());
                return Db.tx(() -> authProject.saveOrUpdate() && projectUndertake.saveOrUpdate());
            }
        });
    }

    @Override
    public List<Project> findByIsPublic(boolean isPublic) {
        return DAO.findListByColumn("isPublic", isPublic);
    }

    @Override
    public Project saveProject(Project model) {
        if (!model.save()) {
            return null;
        } else {
            return model;
        }
    }

    @Override
    public List<Project> findByUserId(Long userId) {
        return DAO.findListByColumn("userId", userId);
    }
}