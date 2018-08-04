package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;

import java.util.List;

public interface FileProjectService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FileProject findById(Object id);


    /**
     * find all model
     *
     * @return all <FileProject
     */
    public List<FileProject> findAll();


    /**
     * find All By ProjectID
     *
     * @param id
     * @return
     */
    public List<FileProject> findAllByProjectID(Long id);


    /**
     * 通过项目id 查询文件项目
     * @param projectId  项目id
     * @return 文件项目
     */
    public FileProject findByProjectID(Long projectId);

    /**
     * find model By projectID And fileTypeID
     *
     * @param projectID
     * @param projectFileID
     * @return
     */
    public FileProject findByProjectIDAndProjectFileID(Long projectID, Long projectFileID);

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
    public boolean delete(FileProject model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(FileProject model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(FileProject model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FileProject model);

    public boolean update(FileProject model, Files files);

    public FileProject findByProjectID(Long id);

    public FileProject findByFileTypeIdAndProjectId(Long fileTypeId, Long projectId);

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