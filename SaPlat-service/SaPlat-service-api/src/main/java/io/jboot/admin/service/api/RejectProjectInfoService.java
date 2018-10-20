package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.RejectProjectInfo;

import java.util.List;

public interface RejectProjectInfoService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public RejectProjectInfo findById(Object id);


    /**
     * find all model
     *
     * @return all <RejectProjectInfo
     */
    public List<RejectProjectInfo> findAll();

    /**
     * @param rejectProjectInfo
     * @return
     */
    public Page<RejectProjectInfo> findPage(RejectProjectInfo rejectProjectInfo, int pageNumber, int pageSize);


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
    public boolean delete(RejectProjectInfo model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(RejectProjectInfo model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(RejectProjectInfo model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(RejectProjectInfo model);


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