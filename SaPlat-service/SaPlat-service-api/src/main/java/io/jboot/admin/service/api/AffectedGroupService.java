package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.admin.service.entity.model.Auth;

import java.util.List;

public interface AffectedGroupService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public AffectedGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <AffectedGroup
     */
    public List<AffectedGroup> findAll();

    /**
     * 分页查询 项目阶段 信息
     * @param model 项目阶段
     * @return 页
     */
    public Page<AffectedGroup> findPage(AffectedGroup model, int pageNumber, int pageSize);

    /**
     * 根据名称查询 项目阶段 信息
     * @param name
     * @return
     */
    public AffectedGroup findByName(String name);

    /**
     * 根据个人群体 id 查询影响群体
     * @param userID 用户id
     * @return 影响群体
     */
    public AffectedGroup findByPersonId(Long userID);

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
    public boolean delete(AffectedGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(AffectedGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(AffectedGroup model);


    /**
     * 保存或更新影响群体以及全新
     * @param model 影响群体
     * @param auth 认证信息
     * @return 是否成功
     */
    public boolean saveOrUpdate(AffectedGroup model, Auth auth);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(AffectedGroup model);


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