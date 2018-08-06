package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.ApplyInvite;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.StructPersonLink;

import java.util.List;

public interface ApplyInviteService  {
    /**
     * 保存或更新和发送站内信息
     * @return
     */
    public boolean saveOrUpdateAndSend(ApplyInvite applyInvite,Notification notification);
    /**
     * 保存或更新和发送站内信息
     * @return
     */
    public boolean saveAndUpdateAndSend(ApplyInvite applyInvite, Notification notification, StructPersonLink structPersonLink);
    /**
     * 通过用户id或架构名称查找申请列表
     * @param pageNumber
     * @param pageSize
     * @param applyInvite
     * @return
     */
    public Page<ApplyInvite> findApplyByUserIdOrName(int pageNumber, int pageSize, ApplyInvite applyInvite);

    /**
     * 通过用户id或架构名称查找
     * @param pageNumber
     * @param pageSize
     * @param applyInvite
     * @return
     */
    public Page<ApplyInvite> findListByUserIdOrUserName(int pageNumber, int pageSize, ApplyInvite applyInvite);
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ApplyInvite findById(Object id);

    /**
     * @param facAgencyID
     * @param projectID
     * @return
     */
    public boolean findIsInvite(Long facAgencyID, Long projectID);

    /**
     * find all model
     *
     * @return all <ApplyInvite
     */
    public List<ApplyInvite> findAll();


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
    public boolean delete(ApplyInvite model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ApplyInvite model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ApplyInvite model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ApplyInvite model);


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