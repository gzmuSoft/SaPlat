package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.User;

import java.util.Date;
import java.util.List;

public interface UserService  {
    public User findModel(User model);

    public Page<User> findPage(User sysUser, int pageNumber, int pageSize);

    public Page<User> findPage(User sysUser, Date[] dates, int pageNumber, int pageSize);

    public List<User> findAll();

    public boolean hasUser(String name);

    public User findByName(String name);

    public User findByUserIdAndUserSource(Long userID, Long userSource);

    public boolean saveUser(User user, Long[] roles);

    public boolean updateUser(User user, Long[] roles);

    public User findById(Object id);


    public boolean deleteById(Object id);

    public boolean delete(User model);


    public boolean save(User model);

    public boolean saveOrUpdate(User model);

    public boolean update(User model);

    public User findByUserIdAndUserSource(Long userid,Integer usersource);

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