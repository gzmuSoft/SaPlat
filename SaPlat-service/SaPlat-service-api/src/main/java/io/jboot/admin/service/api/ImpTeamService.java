package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.*;

import java.util.List;

public interface ImpTeamService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ImpTeam findById(Object id);


    /**
     * find all model
     *
     * @return all <ImpTeam
     */
    public List<ImpTeam> findAll();

    /**
     * 通过 project Id查询
     *
     * @param id
     * @return
     */
    public ImpTeam findByProjectId(Long id);

    /**
     * 通过专家团体查找实现小组
     *
     * @param expertGroups 专家团体
     * @return 所有符合条件的 <ImpTeam
     */
    List<ImpTeam> findByExpertGroup(ExpertGroup... expertGroups);

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
    public boolean delete(ImpTeam model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ImpTeam model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ImpTeam model);


    /**
     * save  model and evaScheme
     *
     * @param model,evaScheme
     * @return if save success
     */
    public boolean save(ImpTeam model, EvaScheme evaScheme, List<ScheduledPlan> scheduledPlans, FileForm fileForm);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ImpTeam model);

    public List<ImpTeam> findByUserID(Long id);

    public ImpTeam findByUserIDAndProjectID(Long userId, Long projectId);

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