package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Auth;

import java.util.List;

public interface AuthService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Auth findById(Object id);


    /**
     * find all model
     *
     * @return all <Auth
     */
    public List<Auth> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);

    /**
     * 更具用户id查询认证信息
     * @param userId 用户id
     * @return 认证信息
     */
    public Auth findByUserId(Long userId);

    /**
     * 更具用户id和认证状态查询认证信息以获得认证角色
     *
     * @param userId 用户id
     * @param status
     * @return 认证信息
     */
    public Auth findByUserIdAndStatus(Long userId, int status);

    /**
     * 更具用户id和认证状态查询认证信息以列表形式存储
     *
     * @param userId 用户id
     * @param status
     * @return 认证信息
     */
    public List<Auth> findByUserIdAndStatusToList(Long userId, int status);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Auth model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Auth model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Auth model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Auth model);


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