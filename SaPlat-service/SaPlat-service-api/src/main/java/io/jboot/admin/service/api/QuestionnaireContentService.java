package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Data;
import io.jboot.admin.service.entity.model.QuestionnaireContent;

import java.util.Date;
import java.util.List;

public interface QuestionnaireContentService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public QuestionnaireContent findById(Object id);

    /**
     * 分页查询系统信息
     * @param ids ids
     * @return 页
     */
    public Page<QuestionnaireContent> findPageById(Long[] ids, int pageNumber, int pageSize);

    /**
     * find model by primary key
     *
     * @param model
     * @return
     */
    public QuestionnaireContent findByModel(QuestionnaireContent model);

    /**
     * find all model
     *
     * @return all <QuestionnaireContent
     */
    public List<QuestionnaireContent> findAll();


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
    public boolean delete(QuestionnaireContent model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(QuestionnaireContent model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(QuestionnaireContent model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(QuestionnaireContent model);


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