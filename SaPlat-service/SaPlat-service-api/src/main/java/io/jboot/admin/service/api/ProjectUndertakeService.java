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
     * 返回已被承接数/用户的项目总数*100
     * @param userId
     * @return
     */
     public float findAllAndUndertakeByUserId(Long userId);

    /**
     * find all model
     *
     * @return all <ProjectUndertake
     */
    public List<ProjectUndertake> findAll();

    /**
     * find model by facAgencyID and projectID
     * @param facAgencyID
     * @param projectID
     * @return all <ProjectUndertake
     */
    public boolean findIsInvite(Long facAgencyID, Long projectID);

    /**
     * find List<ProjectUndertake> by facAgencyId and status
     *
     * @param facAgencyId facAgencyId
     * @param status <ProjectUndertakeStatus>
     * @return all List<ProjectUndertake>
     */
    public List<ProjectUndertake> findListByFacAgencyIdAndStatus(Long facAgencyId,String status);

    /**
     * find List<ProjectUndertake> by projectID and status
     *
     * @param status
     * @param projectID
     * @return all List<ProjectUndertake>
     */
    public List<ProjectUndertake> findListByProjectAndStatus(Long projectID, String status);

    /**
     * find model projectID and status to isReceive
     * @param projectID
     * @return all <ProjectUndertake
     */
    public boolean findIsReceive(Long projectID);

    /**
     * @param pageNumber
     * @param pageSize
     * @param projectUndertake
     * @return Project
     */
    public Page<ProjectUndertake> findPage(ProjectUndertake projectUndertake, int pageNumber, int pageSize);

    /**
     * @param projectUndertake
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<ProjectUndertake> findPageBySql(ProjectUndertake projectUndertake, int pageNumber, int pageSize);

    /**
     * 通过创建项目的用户编号获得其拥有的project表再获取申请介入当前用户所拥有项目的承接关联列表
     * @param buildProjectUserID 创建项目的用户编号
     * @param pageNumber 页码
     * @param pageSize 分页大小
     * @return
     */
    public Page<ProjectUndertake> findPageOfApplyIn(Long buildProjectUserID, int pageNumber, int pageSize);

    /**
     * 通过项目id和创建用户id
     * @param id
     * @param facAgencyID
     * @return
     */
    public ProjectUndertake findByProjectIdAndFacAgencyID(Long id, Long facAgencyID);

    /**
     * 通过项目编号和服务机构编号查询项目承接
     * @param projectId 项目编号
     * @param facAgencyId 服务机构编号
     * @return 项目承接
     */
    public ProjectUndertake findByProjectIdAndFacAgencyId(Long projectId,Long facAgencyId);

    /**
     *
     * @param facAgencyId
     * @param status
     * @param aoi
     * @return
     */
    List<ProjectUndertake> findListByFacAgencyIdAndStatusAndAOI(Long facAgencyId, String status, boolean aoi);

    /**
     *
     * @param projectId
     * @param projectStatus
     * @return
     */
    public ProjectUndertake findByProjectIdAndStatus(Long projectId, String projectStatus);

    /**
     * 通过创建者id和状态查询
     * @param createUserId
     * @param status
     * @param aoi 如何承接
     * @return
     */
    public List<ProjectUndertake> findByCreateUserIDAndStatusAndAOI(Long createUserId, String status, boolean aoi);

    /**
     * 通过项目编号查询项目承接
     * @param projectId 项目编号
     * @return 项目承接
     */
    public ProjectUndertake findByProjectId(Long projectId);

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