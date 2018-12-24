package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.admin.service.entity.model.OrgStructure;

import java.util.List;

public interface OrgStructureService {
    /**
     * Main 组织-组织架构数据显示以及分页
     */
    public Page<Record> findMainList(int pageNumber, int pageSize,Long uid,int orgTye,String structName);
    /**
     * 查找架构 - 个人主动加入
     * @param orgStructure
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> searchStructure(OrgStructure orgStructure, int pageNumber, int pageSize);
    /**
     * MAIN数据分页
     * @param orgStructure
     * @param pageNumber
     * @param pageSize
     * @param uid
     * @param orgType
     * @return
     */
    public Page<OrgStructure> findPage(OrgStructure orgStructure, int pageNumber, int pageSize,Long uid,int orgType);
    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public OrgStructure findById(Object id);


    /**
     * find model by primary key
     *
     * @param id
     * @param typeId
     * @return
     */
    public List<OrgStructure> findByOrgIdAndType(Object id,int typeId);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public List<OrgStructure> findByOrgId(Object id);

    /**
     * find all model
     *
     * @return all <OrgStructure
     */
    public List<OrgStructure> findAll();


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
    public boolean delete(OrgStructure model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(OrgStructure model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(OrgStructure model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(OrgStructure model);


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