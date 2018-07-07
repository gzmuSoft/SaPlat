package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;

import java.util.List;

public interface PersonService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Person findById(Object id);


    /**
     * find all model
     *
     * @return all <Person
     */
    public List<Person> findAll();


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
    public boolean delete(Person model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Person model);


    /**
     * 保存个人群体并创建用户
     * @param model 个人
     * @param user 用户
     * @param roles 用户权限
     * @return 执行结果
     */
    public boolean savePerson(Person model, User user, Long[] roles);

    /**
     * 通过用户获取个人群体
     * @param user 用户
     * @return 个人群体
     */
    public Person findByUser(User user);

    /**
     * 更新资料
     * @param person 个人群体
     * @param user 用户
     * @return
     */
    public boolean update(Person person,User user);

    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Person model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Person model);


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