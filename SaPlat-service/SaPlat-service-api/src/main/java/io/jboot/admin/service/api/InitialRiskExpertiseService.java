package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.InitialRiskExpertise;

import java.util.List;

public interface InitialRiskExpertiseService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public InitialRiskExpertise findById(Object id);


    /**
     * find all model
     *
     * @return all <InitialRiskExpertise
     */
    public List<InitialRiskExpertise> findAll();

    /**
     * 分页查询
     * @param model 条件
     * @param pageNumber 页数
     * @param pageSize 页码大小
     * @return 页码
     */
    public Page<InitialRiskExpertise> findPage(InitialRiskExpertise model, int pageNumber, int pageSize);

    /**
     * 通过项目id查询列表
     * @param projectId 项目id
     * @return 结合
     */
    public List<InitialRiskExpertise> findByProjectId(Long projectId);

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
    public boolean delete(InitialRiskExpertise model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(InitialRiskExpertise model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(InitialRiskExpertise model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(InitialRiskExpertise model);


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