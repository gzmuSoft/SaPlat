package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.service.entity.model.Project;

import java.util.List;

/**
 * @author ASUS
 */
public interface FileProjectService {

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
     * 通過id查询列表
     *
     * @param id id
     * @return 列表
     */
    public List<FileProject> findListById(Long id);

    /**
     * find All By ProjectID
     *
     * @param id
     * @return
     */
    public List<FileProject> findAllByProjectID(Long id);

    /**
     * 通过项目id 查询文件项目
     *
     * @param projectId 项目id
     * @return 文件项目
     */
    public FileProject findByProjectID(Long projectId);

    /**
     * 通过文件id 查询文件项目
     *
     * @param fileId 文件id
     * @return 文件项目
     */
    public FileProject findByFileID(Long fileId);

    /**
     * 通过文件类型id和项目id查询列表
     *
     * @param fileTypeID 文件类型id
     * @param projectID  项目id
     * @return 文件列表
     */
    public List<FileProject> findListByFileTypeIDAndProjectID(Long fileTypeID, Long projectID);

    /**
     * find model By projectID And fileTypeID
     *
     * @param projectID
     * @param fileTypeID
     * @return
     */
    public FileProject findByProjectIDAndFileTypeID(Long projectID, Long fileTypeID);

    /**
     * find page
     *
     * @param pageNumber
     * @param pageSize
     * @param model
     * @return
     */
    public Page<FileProject> findPage(FileProject model, int pageNumber, int pageSize);

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
     * 删除fileProject 禁用files
     *
     * @param model
     * @return
     */
    public boolean deleteFileProjectAndFiles(FileProject model);

    /**
     * 保存fileProject 启用files
     *
     * @param model
     * @return
     */

    public boolean updateFileProjectAndFiles(FileProject model);

    public boolean saveFileProjectAndFiles(FileProject model);

    public FileProject saveFileProjectAndFilesGet(FileProject model);


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
     * 更新fileproject 更新project
     *
     * @param fileProject
     * @param project
     * @return
     */
    public boolean updateFileProjectAndProject(FileProject fileProject, Project project);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FileProject model);

    public FileProject saveAndGet(FileProject model);

    public boolean update(FileProject model, Files files);

    public boolean isExists(FileProject model);

    public FileProject findByModel(FileProject model);

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

    List<FileProject> findByRemark(Long questionnaireId);
}