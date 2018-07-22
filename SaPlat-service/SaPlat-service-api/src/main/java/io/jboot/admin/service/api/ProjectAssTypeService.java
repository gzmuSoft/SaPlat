package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.ProjectAssType;

import java.util.List;

public interface ProjectAssTypeService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProjectAssType findById(Object id);


    /**
     * find all model
     *
     * @return all <ProjectAssType
     */
    public List<ProjectAssType> findAll();

    /**
     * find all model
     * @param model 项目评估类型
     * @return all <ProjectAssType>
     */
    public List<ProjectAssType> findAll(ProjectAssType model);


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
    public boolean delete(ProjectAssType model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ProjectAssType model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ProjectAssType model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProjectAssType model);


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

    Page<ProjectAssType> findPage(ProjectAssType model, int pageNumber, int pageSize);

    boolean isExisted(String name);

    ProjectAssType findByName(String name);
}