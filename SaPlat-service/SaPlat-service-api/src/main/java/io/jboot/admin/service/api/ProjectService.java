package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.AuthProject;
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