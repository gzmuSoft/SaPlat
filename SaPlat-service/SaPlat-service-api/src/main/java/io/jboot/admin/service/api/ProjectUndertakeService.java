package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.ProjectUndertake;

import java.util.List;

public interface ProjectUndertakeService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProjectUndertake findById(Object id);


    /**
     * find all model
     *
     * @return all <ProjectUndertake
     */
    public List<ProjectUndertake> findAll();

    /**
     * find model by facAgencyID and projectID
     *
     * @return all <ProjectUndertake
     */
    public boolean findIsInvite(Long facAgencyID, Long projectID);

    /**
     * find model projectID and status to isReceive
     *
     * @return all <ProjectUndertake
     */
    public boolean findIsReceive(Long projectID);

    /**
     *
     * @param pageNumber
     * @param pageSize
     * @return Project
     */
    public Page<ProjectUndertake> findPage(ProjectUndertake project, int pageNumber, int pageSize);


    /**
     * 通过项目编号和服务机构编号查询项目承接
     * @param projectId 项目编号
     * @param facAgencyId 服务机构编号
     * @return 项目承接
     */
    public ProjectUndertake findByProjectIdAndFacAgencyId(Long projectId,Long facAgencyId);

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
    public boolean delete(ProjectUndertake model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ProjectUndertake model);


    /**
     * 保存或者更新并发送通知
     *
     * @param model        保存的项目承接
     * @param notification 通知者
     * @return 结果
     */
    public boolean saveOrUpdateAndSend(ProjectUndertake model, Notification notification);

    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ProjectUndertake model);




    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProjectUndertake model);


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