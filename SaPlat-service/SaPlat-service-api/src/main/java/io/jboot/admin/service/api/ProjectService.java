package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.AuthProject;
import io.jboot.admin.service.entity.model.LeaderGroup;
import io.jboot.admin.service.entity.model.Project;

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
     * 根据多个列名和多个列值查询第一个数据
     * @param columnNames 列名
     * @param values 值
     * @return 第一个
     */
    public Project findFirstByColumns(String[] columnNames,String[] values);



    /**
     * 根据多个列名和多个列值查询
     * @param columnNames 列名
     * @param values 值
     * @return 列表
     */
    public List<Project> findListByColumns(String[] columnNames,String[] values);


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

    boolean saveOrUpdate(Project model, AuthProject authProject);

    /**
     * find model by user and role and isEnable
     *
     * @param pageNumber
     * @param pageSize
     * @return Project
     */
    public Page<Project> findPage(Project project, int pageNumber, int pageSize);

    /**
     * find model by user and role and isEnable
     *
     * @param pageNumber
     * @param pageSize
     * @return Project
     */
    public Page<Project> findPageByIsPublic(Project project, int pageNumber, int pageSize);


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
    public Long saveProject(Project model);

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