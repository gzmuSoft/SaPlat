package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.admin.service.entity.model.Questionnaire;
import io.jboot.admin.service.entity.model.QuestionnaireContent;

import java.util.List;

public interface QuestionnaireService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Questionnaire findById(Object id);

    /**
     * 事物回滚
     */
    public boolean saveQuestionnairen(Questionnaire questionnaire, List<QuestionnaireContent> contents, Project project);

    /**
     * find all model
     *
     * @return all <Questionnaire
     */
    public List<Questionnaire> findAll();

    /**
     * 根据当前项目ID判断是否有对应的问卷
     * @param id
     * @return
     */
    public Questionnaire findByProjectID(Object id);

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
    public boolean delete(Questionnaire model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Questionnaire model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Questionnaire model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Questionnaire model);


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