package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Diagnoses;

import java.util.List;

public interface DiagnosesService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Diagnoses findById(Object id);


    /**
     * find all model
     *
     * @return all <Diagnoses
     */
    public List<Diagnoses> findAll();

    /**
     * 通过项目id查询
     * @param projectId 项目id
     * @return 列表
     */
    public List<Diagnoses> findListByProjectId(Long projectId);

    /**
     * 分页查询
     * @param diagnoses 调查分析条件
     * @param pageNumber 页码
     * @param pageSize  页面大小
     * @return 页
     */
    public Page<Diagnoses> findPage(Diagnoses diagnoses, int pageNumber, int pageSize);


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
    public boolean delete(Diagnoses model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Diagnoses model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Diagnoses model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Diagnoses model);


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