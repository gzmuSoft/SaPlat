package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.ProjectFileType;

import java.util.List;

public interface ProjectFileTypeService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProjectFileType findById(Object id);


    /**
     * find all model
     *
     * @return all <ProjectFileType
     */
    public List<ProjectFileType> findAll();

    /**
     * 通过名字查找
     * @param name 名字
     * @return 文件类型
     */
    public ProjectFileType findByName(String name);

    /**
     * 通过父级id查找列表
     * @param parentId 父级 id
     * @return 列表
     */
    public List<ProjectFileType> findListByParentId(Long parentId);

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
    public boolean delete(ProjectFileType model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ProjectFileType model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ProjectFileType model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProjectFileType model);

    /**
     * 分页查询
     * @param projectFileType 模型
     * @param pageNumber 页数
     * @param pageSize 大小
     * @return 页
     */
    public Page<ProjectFileType> findPage(ProjectFileType projectFileType, int pageNumber, int pageSize);

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