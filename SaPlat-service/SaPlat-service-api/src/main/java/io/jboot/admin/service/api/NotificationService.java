package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Notification;

import java.util.List;

public interface NotificationService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Notification findById(Object id);

    /**
     * 分页查询信息
     * @param notification
     * @return 页
     */
    public Page<Notification> findPage(Notification notification, int pageNumber, int pageSize);

    /**
     * find all model
     *
     * @return all <Notification
     */
    public List<Notification> findAll();


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
    public boolean delete(Notification model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Notification model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Notification model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Notification model);

    /**
     * 根据当前用户ID判断是否有对应用户的未读消息
     *
     * @param id
     * @return
     */
    public boolean findMessageByUserID(Object id);

    /**
     * 当前用户标记全部已读
     * @param id
     * @return
     */
    public boolean haveReadAll(Object id);

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