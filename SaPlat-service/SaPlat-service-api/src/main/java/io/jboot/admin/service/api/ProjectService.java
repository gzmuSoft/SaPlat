package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.*;

import java.util.List;

public interface ProjectService  {
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Project findById(Object id);

    /**
     *
     * @param userId
     * @return
     */
    public List<Project> findByUserId(Long userId);
    /**
     * find all model
     * @param model 项目主体
     * @return all <Project>
     */
    public List<Project> findAll(Project model);

    /**
     * 根据列名和列值查询
     * @param columnName 列名
     * @param value 值
     * @return 列表
     */
    public List<Project> findListByColumn(String columnName,Object value);

    /**
     * 根据列名和列值查询第一个
     * @param columnName 列名
     * @param value 值
     * @return 第一个
     */
    public Project findFirstByColumn(String columnName,Object value);

    /**
     * 根据项目名称查询
     * @param ProjectName 列名
     */
    public Project findByProjectName(String ProjectName);

    /**
     * 根据多个列名和多个列值查询第一个数据
     * @param columnNames 列名
     * @param values 值
     * @return 第一个
     */
    public Project findFirstByColumns(String[] columnNames,String[] values);

    /**
     * @param projectUndertake
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Project> findPageBySql(ProjectUndertake projectUndertake, int pageNumber, int pageSize);

    /**
     * @param projectUndertake
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Project> findReviewedPageBySql(ProjectUndertake projectUndertake, int pageNumber, int pageSize);

    /**
     * 根据多个列名和多个列值查询
     * @param columnNames 列名
     * @param values 值
     * @return 列表
     */
    public List<Project> findListByColumns(String[] columnNames,String[] values);

    /**
     * 查询审查完成以及上传终审报告待审核的项目
     *
     * @param project
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Project> findCheckedPage(Project project, int pageNumber, int pageSize);

    /**
     * find List<Project> by ids and status
     *
     * @param ids ids
     * @param status
     * @return
     */
    public List<Project> findByIds(List<Object> ids,String[] status);


    /**
     * find all model
     * @return all <Project
     */
    public List<Project> findAll();

    public boolean saveOrUpdate(Project model, AuthProject authProject);

    /**
     * save Or Update Project And LeaderGroup
     *
     * @param model
     * @param leaderGroup
     * @return
     */
    public boolean saveOrUpdate(Project model, LeaderGroup leaderGroup);

    /**
     * save Or Update Project And authProject And projectUndertake
     *
     * @param model
     * @param authProject
     * @param projectUndertake
     * @return
     */
    public boolean saveOrUpdate(Project model, AuthProject authProject, ProjectUndertake projectUndertake);

    /**
     * save Or Update Project And fileProject And notification And RejectProjectInfo
     *
     * @param model
     * @param notification
     * @param fileProject
     * @return
     */
    public boolean saveOrUpdate(Project model, Notification notification, FileProject fileProject, RejectProjectInfo rejectProjectInfo);

    /**
     * save Or Update Project And notification
     *
     * @param model
     * @param notification
     * @return
     */
    public boolean saveOrUpdate(Project model, Notification notification);

    /**
     * find model by user and role and isEnable
     *
     * @param pageNumber
     * @param pageSize
     * @param project
     * @return project
     */
    public Page<Project> findPage(Project project, int pageNumber, int pageSize);


    /**
     * find model by user and role and isEnable
     *
     * @param pageNumber
     * @param pageSize
     * @param project
     * @return project
     */
    public Page<Project> findPageForMgr(Project project, int pageNumber, int pageSize);

    public Page<Project> findPageForCreater(Project project, int pageNumber, int pageSize);

    public Page<Project> findPageForService(Project project, int pageNumber, int pageSize);

    /**
     * find model by user and role and isEnable
     *
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @param project
     * @return Project
     */
    public Page<Project> findPageByIsPublic(long userId, Project project, int pageNumber, int pageSize);

    /**
     *
     * @param projectUndertakeList
     * @return
     */
    List<Project> findListByProjectUndertakeListAndStatus(List<ProjectUndertake> projectUndertakeList,String status);

    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Project model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Project model);

    /**
     * save model and authProject to database
     *
     * @param model,authProject
     * @return
     */
    public Project saveProject(Project model);

    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Project model);

    /**
     * save or update model and auth
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Project model, AuthProject authProject , LeaderGroup leaderGroup);

    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Project model);

    /**
     * update data
     *
     * @param model
     * @param applyInvite
     * @return
     */
    public boolean update(Project model, ApplyInvite applyInvite);

    public List<Project> findByIsPublic(boolean isPublic);


    public void join(Page<? extends Model> page, String joinOnField);
    public void join(Page<? extends Model> page, String joinOnField, String[] attrs);
    public void join(Page<? extends Model> page, String joinOnField, String joinName);
    public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs);
    public void join(List<? extends Model> models, String joinOnField);
    public void join(List<? extends Model> models, String joinOnField, String[] attrs);
    public void join(List<? extends Model> models, String joinOnField, String joinName);
    public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs);
    public void join(Model model, String joinOnField);
    public void join(Model model, String joinOnField, String[] attrs);
    public void join(Model model, String joinOnField, String joinName);
    public void join(Model model, String joinOnField, String joinName, String[] attrs);


    public void keep(Model model, String... attrs);
    public void keep(List<? extends Model> models, String... attrs);

}