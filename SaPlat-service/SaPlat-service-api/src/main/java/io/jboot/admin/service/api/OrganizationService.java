package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Organization;
import io.jboot.admin.service.entity.model.User;

import java.util.Date;
import java.util.List;

public interface OrganizationService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Organization findById(Object id);


    /**
     * find all model
     *
     * @return all <Organization
     */
    public List<Organization> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);

    /**
     * 分页查询系统 组织
     * @param organization 组织
     * @return 页
     */
    public Page<Organization> findPage(Organization organization, int pageNumber, int pageSize);

    /**
     * 分页查询系统 组织
     * @param organization 组织
     * @return 页
     */
    public Page<Organization> findPage(Organization organization, Date[] dates, int pageNumber, int pageSize);
    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Organization model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Organization model);

    /**
     * 保存组织
     * @param model 组织
     * @param user 用户
     * @param roles 权限
     * @return 结果
     */
    public boolean saveOrganization(Organization model, User user, Long[] roles);

    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Organization model);

    public boolean useOrUnuse(Organization organization);

    /**
     * 通过组织机构名称获取组织机构
     *
     * @param name 组织机构名称
     * @return 组织机构
     */
    public Organization findByName(String name);

    /**
     * 更新资料
     *
     * @param model 组织机构
     * @param user  用户
     * @return
     */
    public boolean update(Organization model, User user);

    /**
     * 组织机构是否存在
     *
     * @param name
     * @return 存在返回true，否则返回false
     */
    public boolean hasUser(String name);

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