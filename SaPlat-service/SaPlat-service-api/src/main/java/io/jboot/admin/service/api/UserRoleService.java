package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.UserRole;

import java.util.List;

public interface UserRoleService {

    /**
     * 根据userId删除model
     *
     * @param userId
     */
    public int deleteByUserId(Long userId);

    /**
     * 批量保存 model
     *
     * @param list
     * @return
     */
    public int[] batchSave(List<UserRole> list);

    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public UserRole findById(Object id);

    /**
     * 通过用户id和权限id查询
     *
     * @param userId 用户id
     * @param roleId 权限id
     * @return 用户权限
     */
    public UserRole findByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 根据用户id查询用户权限
     *
     * @param userId
     * @return
     */

    public List<UserRole> findListByUserId(Long userId);


    /**
     * 根据是否禁用查询
     *
     * @param isEnable
     * @return List<UserRole>
     */
    public List<UserRole> findListByUserIDAndIsEnable(Long userId,boolean isEnable);

    /**
     * 根据ID删除model
     *
     * @param id
     * @return
     */
    public boolean deleteById(Object id);

    /**
     * 删除
     *
     * @param model
     * @return
     */
    public boolean delete(UserRole model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(UserRole model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(UserRole model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(UserRole model);

    public List<UserRole> findAll();

    public List<UserRole> findAllByRoleId(Object roleID);

    public void join(Page<? extends Model> page, String joinOnField);

    public void join(Page<? extends Model> page, String joinOnField, String[] attrs);

    public void join(Page<? extends Model> page, String joinOnField, String joinName);

    public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs);


    public void join(List<? extends Model> models, String joinOnField);

    public void join(List<? extends Model> models, String joinOnField, String[] attrs);

    public void join(List<? extends Model> models, String joinOnField, String joinName);

    public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs);

    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model       要添加到的model
     * @param joinOnField model对于的关联字段
     */
    public void join(Model model, String joinOnField);

    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param attrs
     */
    public void join(Model model, String joinOnField, String[] attrs);


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param joinName
     */
    public void join(Model model, String joinOnField, String joinName);


    /**
     * 添加关联数据到某个model中去，避免关联查询，提高性能。
     *
     * @param model
     * @param joinOnField
     * @param joinName
     * @param attrs
     */
    public void join(Model model, String joinOnField, String joinName, String[] attrs);


    public void keep(Model model, String... attrs);

    public void keep(List<? extends Model> models, String... attrs);
}