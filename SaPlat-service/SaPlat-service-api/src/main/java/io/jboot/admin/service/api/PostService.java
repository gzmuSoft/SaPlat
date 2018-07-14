package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Post;

import java.util.List;

public interface PostService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Post findById(Object id);


    /**
     * find all model
     *
     * @return all <Post
     */
    public List<Post> findAll();

    /**
     * 分页查询 项目阶段 信息
     * @param model 项目阶段
     * @return 页
     */
    public Page<Post> findPage(Post model, int pageNumber, int pageSize);

    /**
     * 根据名称查询 项目阶段 信息
     * @param name
     * @return
     */
    public Post findByName(String name);


    /**
     * 项目阶段 是否存在
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
    public boolean delete(Post model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Post model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Post model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Post model);


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