package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.ApplyInvite;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.StructPersonLink;

import java.util.List;

public interface ApplyInviteService  {

    /**
     * 通过架构ID和用户id查询用户的主动申请信息
     * @param structID
     * @param UserID
     * @return
     */
    public ApplyInvite findByStructIDAndUserID(Long structID, Long UserID);
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
     * 自定义条件查询
     *
     * @param model
     * @return
     */
    public ApplyInvite findFirstByModel(ApplyInvite model);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ApplyInvite findById(Object id);

    /**
     * find model by projectID
     *
     * @param projectID
     * @return
     */
    public ApplyInvite findByProjectID(Long projectID);

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
     * find page
     *
     * @param model
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<ApplyInvite> findPage(ApplyInvite model, int pageNumber, int pageSize);

    /**
     * find list
     *
     * @param model
     * @return
     */
    public List<ApplyInvite> findList(ApplyInvite model);

    /**
     * find list
     *
     * @param projectID
     * @return
     */
    public List<ApplyInvite> findLastTimeListByProjectID(Long projectID);

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