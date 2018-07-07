package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Enterprise;

import java.util.List;

public interface EnterpriseService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Enterprise findById(Object id);


    /**
     * find all model
     *
     * @return all <Enterprise
     */
    public List<Enterprise> findAll();

    /**
     * 分页查询 企业机构 信息
     * @param model 项目阶段
     * @return 页
     */
    public Page<Enterprise> findPage(Enterprise model, int pageNumber, int pageSize);

    /**
     * 根据名称查询 企业机构 信息
     * @param name
     * @return
     */
    public Enterprise findByName(String name);


    /**
     * 企业机构 是否存在
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
    public boolean delete(Enterprise model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Enterprise model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Enterprise model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Enterprise model);


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