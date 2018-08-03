package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.News;

import java.util.List;

public interface NewsService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public News findById(Object id);
    /**
     * find model by primary key
     *
     * @param model,pageNumber,pageSize
     * @return Page
     */
    public Page<News> findPage(News model, int pageNumber, int pageSize);
    /**
     * find model by primary key
     *
     * @param size
     * @return
     */
    public Page<News> findReverses(int size);
    /**
     * find all model
     *
     * @return all <News
     */
    public List<News> findAll();


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
    public boolean delete(News model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(News model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(News model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(News model);


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