package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.ZTree;
import io.jboot.admin.service.entity.model.ProAssReview;

import java.util.List;

public interface ProAssReviewService  {

    /**
     * find file URL by file id
     *
     * @param id
     * @return url
     * */
    public String findFileURLByFileId(long id);

    /**
     * get file tree by project id
     *
     * @param id project
     * @return List<ZTree>
     * */
    public List<ZTree> findFileTreeByProject(long id);

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProAssReview findById(Object id);


    /**
     * find model by fileId and projectId
     *
     * @param fileId
     * @param projectId
     * @return ProAssReview model
     */
    public List<ProAssReview> findByFileIdAndProjectId(long fileId,long projectId);

    /**
     * find all model
     *
     * @return all <ProAssReview
     */
    public List<ProAssReview> findAll();


    /**
     * find all model
     * @param model 项目审查
     * @return all <ProAssReview>
     */
    public List<ProAssReview> findAll(ProAssReview model);

    /**
     * 分页查询 项目审查 信息
     * @param model 项目审查
     * @return 页
     */
    public Page<ProAssReview> findPage(ProAssReview model, int pageNumber, int pageSize);

    /**
     * 根据名称查询 项目审查 信息
     * @param name
     * @return
     */
    public ProAssReview findByName(String name);


    /**
     * 项目审查 是否存在
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
    public boolean delete(ProAssReview model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(ProAssReview model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(ProAssReview model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProAssReview model);


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