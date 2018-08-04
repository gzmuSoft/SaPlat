package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.StructPersonLink;

import java.util.List;
import java.util.Map;

public interface StructPersonLinkService {
    /**
     * 架构人员列表
     * @param orgStructureId
     * @return
     */
    public Map<String,Object> findByStructIdAndUsername(Long orgStructureId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public StructPersonLink findById(Object id);


    /**
     * find all model
     *
     * @return all <StructPersonLink
     */
    public List<StructPersonLink> findAll();

    /**
     *根据用户名查询
     * @param orgStructureId
     * @return
     */
    public List<StructPersonLink> findByPersonID(Long personID);

    /**
     * find List<model> by orgStructureId
     *
     * @param orgStructureId
     * @return
     */
    public List<StructPersonLink> findByStructId(Long orgStructureId);

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
    public boolean delete(StructPersonLink model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(StructPersonLink model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(StructPersonLink model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(StructPersonLink model);


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