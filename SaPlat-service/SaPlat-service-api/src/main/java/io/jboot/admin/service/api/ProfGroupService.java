package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.ProfGroup;

import java.util.List;

public interface ProfGroupService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProfGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <ProfGroup
     */
    public List<ProfGroup> findAll();

    public ProfGroup findByOrgId(Long orgId);
    public ProfGroup findByCreateUserID(Object createUserID);
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
    public boolean delete(ProfGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ProfGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ProfGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProfGroup model);

    public boolean useOrUnuse(ProfGroup profGroup);



    /**
     * 根据名称查询 专业团体 信息
     *
     * @param name
     * @return
     */
    public ProfGroup findByName(String name);


    /**
     * 专业团体 是否存在
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
    public boolean saveOrUpdate(ProfGroup model, Auth auth);
    public boolean saveOrUpdate(ProfGroup model, Auth auth, Notification notification);

    public Page<ProfGroup> findPage(ProfGroup profGroup, int pageNumber, int pageSize);

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