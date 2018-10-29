package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.FacAgency;
import io.jboot.admin.service.entity.model.Notification;

import java.util.Date;
import java.util.List;

public interface FacAgencyService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FacAgency findById(Object id);

    public FacAgency findByOrgId(Long orgId);
    public FacAgency findByCreateUserID(Object createUserID);

    /**
     * 关联组织和管理机构
     *
     * @param orgID
     * @return
     */
    /**
     * find all model
     *
     * @return all <FacAgency
     */
    public List<FacAgency> findAll();

    /**
     * 分页查询 项目阶段 信息
     * @param model 项目阶段
     * @return 页
     */
    public Page<FacAgency> findPage(FacAgency model, int pageNumber, int pageSize);

    /**
     * 分页查询 项目阶段 信息
     * @param model 项目阶段
     * @return 页
     */
    public Page<FacAgency> findPage(FacAgency model, Date[] dates, int pageNumber, int pageSize);

    /**
     * 分页查询 指定服务机构之外的所有服务机构 信息
     *
     * @return 页
     */
    public Page<FacAgency> findPageExcludeByOrgID(long orgID,int pageNumber, int pageSize);

    /**
     * 根据名称查询 项目阶段 信息
     * @param name
     * @return
     */
    public FacAgency findByName(String name);


    /**
     * 项目阶段 是否存在
     * @param name
     * @return 存在返回-true，否则返回false
     */
    public boolean isExisted(String name);

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
    public boolean delete(FacAgency model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(FacAgency model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(FacAgency model);

    /**
     * save Or Update model and auth
     *
     * @param model
     * @param auth
     * @return if save or update success
     */
    public boolean saveOrUpdate(FacAgency model, Auth auth);
    public boolean saveOrUpdate(FacAgency model, Auth auth, Notification notification);

    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FacAgency model);

    /*

     */
    public boolean useOrUnuse(FacAgency facAgency);


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