package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.Management;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.db.model.Columns;

import java.util.List;

public interface ManagementService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Management findById(Object id);

    /**
     * findByOrgId
     *
     * @param orgId
     * @return
     */
    public Management findByOrgId(Long orgId);

    /**
     * findByCreateUserID
     * @param userId
     * @return
     */
    public Management findByCreateUserID(Object userId);
    /**
     * find all model
     *
     * @return all <Management
     */
    public List<Management> findAll();

    /**
     * 获取所有数据
     * @param isEnable 根据指定的isEnable的值来获取数据
     * @return
     */
    public List<Management> findAll(boolean isEnable);

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
    public boolean delete(Management model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Management model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Management model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Management model);

    public boolean useOrUnuse(Management management);


    /**
     * 根据名称查询 管理机构 信息
     *
     * @param name
     * @return
     */
    public Management findByName(String name);


    /**
     * 管理机构 是否存在
     *
     * @param name
     * @return 存在返回-true，否则返回false
     */
    public boolean isExisted(String name);

    /**
     * save Or Update model and auth
     *
     * @param model
     * @param auth
     * @return if save or update success
     */
    public boolean saveOrUpdate(Management model, Auth auth);
    public boolean saveOrUpdate(Management model, Auth auth, Notification notification);


    public Page<Management> findPage(Management management, int pageNumber, int pageSize);
    public List<Management> findListByColumns(Columns columns);

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