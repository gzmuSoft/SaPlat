package io.jboot.admin.service.provider;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.api.SaPlatService;
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
public class ProjectServiceImpl extends JbootServiceBase<Project> implements ProjectService, SaPlatService<Project> {

    ProjectAssTypeServiceImpl projectAssTypeService = new ProjectAssTypeServiceImpl();
    UserServiceImpl userService = new UserServiceImpl();
    OrganizationServiceImpl organizationService = new OrganizationServiceImpl();
    ManagementServiceImpl mgrService = new ManagementServiceImpl();
    /**
     * 装配单个实体对象的数据
     * @param model
     * @return
     */
    @Override
    public Project fitModel(Project model){
        if(null != model && model.getPaTypeID()>0) {
            model.setProjectAssType(projectAssTypeService.findById(model.getPaTypeID()));
            User user = userService.findById(model.getUserId());
            if(user != null) {
                model.setBuildUserName(user.getName());
                model.setBuildOrgName(organizationService.findById(user.getUserID()).getName());
            }
        }
        return model;
    }

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
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public List<Project> findListByColumn(String columnName, Object value) {
        return fitList(DAO.findListByColumn(Column.create(columnName, value)));
    }

    @Override
    public Project findFirstByColumn(String columnName, Object value) {
        return fitModel(DAO.findFirstByColumn(Column.create(columnName, value)));
    }

    @Override
    public Project findByProjectName(String ProjectName){
        Columns columns = Columns.create();
        columns.eq("name", ProjectName);
        return DAO.findFirstByColumns(columns);
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
        return fitModel(DAO.findFirstByColumns(columns));
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
        return fitList(DAO.findListByColumns(columns));
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
        return fitList(projects);
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
            columns.eq("userId", project.getUserId());
        }
        if (StrKit.notBlank(project.getStatus())) {
            columns.eq("status", project.getStatus());
        }
        if (project.getPaTypeID() != null && project.getPaTypeID() != 0) {
            columns.eq("paTypeID", project.getPaTypeID());
        }
        if(project.getIsEnable()!=null){
            columns.eq("IsEnable", project.getIsEnable());
        }
        //return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
        //Page<Project> projects = DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public Page<Project> findPageForCreater(Long userID, int pageNumber, int pageSize){
        Kv c = Kv.by("userID", userID);
        SqlPara sqlPara = Db.getSqlPara("app-project.project-by-creater", c);
        return fitPage(DAO.paginate(pageNumber,pageSize,sqlPara));
    }

    @Override
    public Page<Project> findPageForCreater(Long userID, String status, int pageNumber, int pageSize){
        Kv c = Kv.by("userID", userID).set("status", status);
        SqlPara sqlPara = Db.getSqlPara("app-project.project-by-creater-status", c);
        return fitPage(DAO.paginate(pageNumber,pageSize,sqlPara));
    }

    @Override
    public Page<Project> findPageForService(Long userID, int pageNumber, int pageSize){
        Kv c = Kv.by("userID", userID);
        SqlPara sqlPara = Db.getSqlPara("app-project.project-by-service", c);
        return fitPage(DAO.paginate(pageNumber,pageSize,sqlPara));
    }

    @Override
    public Page<Project> findPageForService(Long userID, String status, int pageNumber, int pageSize){
        Kv c = Kv.by("userID", userID).set("status", status);
        SqlPara sqlPara = Db.getSqlPara("app-project.project-by-service-status", c);
        return fitPage(DAO.paginate(pageNumber,pageSize,sqlPara));
    }

    @Override
    public Page<Project> findPageForMgr(Project project, int pageNumber, int pageSize){
        //当前用户对应的管理机构
        Management curMgr = mgrService.findByOrgId(project.getUserId());
        if(null != curMgr) {
            List<Management> result = new ArrayList<Management>();
            result.add(curMgr);
            findMgrChildren(curMgr.getId(), result);
            StringBuilder str = new StringBuilder();
            for(Management item : result){
                str.append(item.getId());
                str.append(",");
            }
            Kv c = Kv.by("mgr_list", str.substring(0,str.length()-1));
            SqlPara sqlPara = Db.getSqlPara("app-project.project-by-mgr", c);
            return fitPage(DAO.paginate(pageNumber,pageSize,sqlPara));
        }
        return  new Page<Project>();
    }

    @Override
    public Page<Project> findPageForMgr(Project project, String status, int pageNumber, int pageSize){
        //当前用户对应的管理机构
        Management curMgr = mgrService.findByOrgId(project.getUserId());
        if(null != curMgr) {
            List<Management> result = new ArrayList<Management>();
            result.add(curMgr);
            findMgrChildren(curMgr.getId(), result);
            StringBuilder str = new StringBuilder();
            for(Management item : result){
                str.append(item.getId());
                str.append(",");
            }
            Kv c = Kv.by("mgr_list", str.substring(0,str.length()-1)).set("status", status);
            SqlPara sqlPara = Db.getSqlPara("app-project.project-by-mgr-status", c);
            return fitPage(DAO.paginate(pageNumber,pageSize,sqlPara));
        }
        return  new Page<Project>();

    }

    private void findMgrChildren(long mgrId,List<Management> result){
        Columns columns = Columns.create();
        columns.eq("superiorID",mgrId);
        List<Management> list = mgrService.findListByColumns(columns);
        if((null != list) && (list.size() >=0)){
            result.addAll(list);
            for(Management item : list){
                findMgrChildren(item.getId(),result);
            }
        }
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

        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
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
        return fitList(list);
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
    public boolean saveOrUpdate(Project model, Notification notification, FileProject fileProject) {
        return Db.tx(() -> {
            if (!model.update()) {
                return false;
            }
            return Db.tx(() -> notification.save() && fileProject.saveOrUpdate());
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
        return fitList(DAO.findListByColumn("isPublic", isPublic));
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
        return fitList(DAO.findListByColumn("userId", userId));
    }

    @Override
    public Page<Project> findPageBySql(ProjectUndertake projectUndertake, int pageNumber, int pageSize) {
        Kv c;
        SqlPara sqlPara = null;
        if (projectUndertake.getFacAgencyID() != null) {
            c = Kv.by("facAgencyID", projectUndertake.getFacAgencyID()).set("status", projectUndertake.getStatus());
            if(projectUndertake.getCreateUserID()!=null) {
                c.set("userID", projectUndertake.getCreateUserID());
                sqlPara = Db.getSqlPara("app-project.project-xxx", c);
            }else {
                sqlPara = Db.getSqlPara("app-project.project-backRecord", c);
            }
        }
        else {
            c = Kv.by("status", projectUndertake.getStatus());
            sqlPara = Db.getSqlPara("app-project.project-Reviewed", c);
        }
        return fitPage(DAO.paginate(pageNumber, pageSize, sqlPara));

    }

    @Override
    public Page<Project> findReviewedPageBySql(ProjectUndertake projectUndertake, int pageNumber, int pageSize) {
        Kv c;
        SqlPara sqlPara = null;
        if (projectUndertake.getFacAgencyID() != null && projectUndertake.getCreateUserID()!=null && projectUndertake.getStatus()!=null) {
            c = Kv.by("facAgencyID", projectUndertake.getFacAgencyID()).set("status", projectUndertake.getStatus()).set("createUserID", projectUndertake.getCreateUserID());
            sqlPara = Db.getSqlPara("app-project.project-Reviewed", c);
            return fitPage(DAO.paginate(pageNumber, pageSize, sqlPara));
        }
        else {
            return new Page<Project>();
        }
    }

}