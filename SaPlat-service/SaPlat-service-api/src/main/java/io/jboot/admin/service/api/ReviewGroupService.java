package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.ReviewGroup;

import java.util.List;

public interface ReviewGroupService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ReviewGroup findById(Object id);

    public ReviewGroup findByOrgId(Long orgId);
    public ReviewGroup findByCreateUserID(Object createUserID);
    /**
     * find all model
     *
     * @return all <ReviewGroup
     */
    public List<ReviewGroup> findAll();


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
    public boolean delete(ReviewGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ReviewGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ReviewGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ReviewGroup model);


    /**
     * 根据名称查询 审查团体 信息
     *
     * @param name
     * @return
     */
    public ReviewGroup findByName(String name);


    /**
     * 审查团体 是否存在
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
    public boolean saveOrUpdate(ReviewGroup model, Auth auth);
    public boolean saveOrUpdate(ReviewGroup model, Auth auth, Notification notification);

    public Page<ReviewGroup> findPage(ReviewGroup reviewGroup, int pageNumber, int pageSize);

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