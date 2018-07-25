package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.ProjectStep;
import io.jboot.admin.service.entity.model.RiskLevel;

import java.util.List;

public interface RiskLevelService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public RiskLevel findById(Object id);

    /**
     * find all model
     *
     * @return all <RiskLevel
     */
    public List<RiskLevel> findAll();

    /**
     * find all model
     * @param model 风险级别
     * @return all <RiskLevel>
     */
    public List<RiskLevel> findAll(RiskLevel model);

    /**
     * 分页查询 风险级别 信息
     * @param model 风险级别
     * @return 页
     */
    public Page<RiskLevel> findPage(RiskLevel model, int pageNumber, int pageSize);

    /**
     * 根据名称查询 风险级别 信息
     * @param name
     * @return
     */
    public RiskLevel findByName(String name);


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
    public boolean delete(RiskLevel model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(RiskLevel model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(RiskLevel model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(RiskLevel model);


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