package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Occupation;

import java.util.List;

public interface OccupationService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Occupation findById(Object id);


    /**
     * find all model
     *
     * @return all <Occupation
     */
    public List<Occupation> findAll();

    /**
     * find all model
     * @param model 职业
     * @return all <Occupation>
     */
    public List<Occupation> findAll(Occupation model);

    /**
     * 分页查询 职业 信息
     * @param model 职业
     * @return 页
     */
    public Page<Occupation> findPage(Occupation model, int pageNumber, int pageSize);

    /**
     * 根据名称查询 职业 信息
     * @param name
     * @return
     */
    public Occupation findByName(String name);


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
    public boolean delete(Occupation model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Occupation model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Occupation model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Occupation model);


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