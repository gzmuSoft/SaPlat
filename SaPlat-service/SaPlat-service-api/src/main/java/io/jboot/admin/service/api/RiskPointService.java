package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.RiskPoint;

import java.util.List;

public interface RiskPointService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public RiskPoint findById(Object id);


    /**
     * find all model
     *
     * @return all <RiskPoint
     */
    public List<RiskPoint> findAll();

    /**
     * find all model
     * @param model 风险点
     * @return all <RiskPoint>
     */
    public List<RiskPoint> findAll(RiskPoint model);

    /**
     * 分页查询 风险点 信息
     * @param model 风险点
     * @return 页
     */
    public Page<RiskPoint> findPage(RiskPoint model, int pageNumber, int pageSize);

    /**
     * 根据名称查询 风险点 信息
     * @param name
     * @return
     */
    public RiskPoint findByName(String name);

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
    public boolean delete(RiskPoint model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(RiskPoint model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(RiskPoint model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(RiskPoint model);


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