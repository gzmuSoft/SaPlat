package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.ExpertGroup;

import java.util.List;

public interface ExpertGroupService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ExpertGroup findById(Object id);


    public ExpertGroup findByOrgId(Long orgId);

    /**
     * 分页查询系统 专家团体 信息
     *
     * @param expert_group 专家团体
     * @return 页
     */
    public Page<ExpertGroup> findPage(ExpertGroup expert_group, int pageNumber, int pageSize);

    /**
     * 根据书名查询 专家团体 信息
     *
     * @param name
     * @return
     */
    public ExpertGroup findByName(String name);


    /**
     * 专家团体 是否存在
     *
     * @param name
     * @return 存在返回-true，否则返回false
     */
    public boolean hasExpertGroup(String name);

    /**
     * find all model
     *
     * @return all <ExpertGroup
     */
    public List<ExpertGroup> findAll();


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
    public boolean delete(ExpertGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ExpertGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ExpertGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ExpertGroup model);


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