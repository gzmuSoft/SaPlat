package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AffectedGroupService;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.api.SaPlatService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class PersonServiceImpl extends JbootServiceBase<Person> implements PersonService{

    @Inject
    private UserService userService;

    @Inject
    private AffectedGroupService affectedGroupService;

    UserServiceImpl userServiceNew = new UserServiceImpl();

    /**
     * 装配完善Page对象中所有对象的数据
     * @param page
     * @return
     */
    public Page<Person> fitPage(Page<Person> page){
        if(page != null){
            List<Person> tList = page.getList();
            for (Person item: tList) {
                fitModel(item);
            }
        }
        return page;
    }

    /**
     * 装配单个实体对象的数据
     * @param model
     * @return
     */
    public Person fitModel(Person model){
        User user = new User();
        user.setUserID(model.getId());
        user.setUserSource(0);// 0 代表个人
        model.setUser(userServiceNew.findModel(user));
        return model;
    }

    /*
     *其中的if是用来做查询的
     * 不满足即不查询
     * 返回全部数据
     */
    @Override
    public Page<Person> findPage(Person person, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(person.getName())) {
            columns.like("name", "%" + person.getName() + "%");
        }
        if (StrKit.notBlank(person.getPhone())) {
            columns.like("phone", "%" + person.getPhone() + "%");
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public boolean savePerson(Person model, User user, Long[] roles) {
        return Db.tx(() -> {
            if (!save(model)) {
                return false;
            }
            user.setUserID(model.getId());
            return userService.saveUser(user, roles);
        });
    }

    @Override
    public Person findByUser(User user) {
        return fitModel(findById(user.getUserID()));
    }

    @Override
    public Person findByName(String name) {
        return fitModel(DAO.findFirstByColumn("name", name));
    }

    @Override
    public boolean update(Person person, User user) {
        return true;
    }

    @Override
    public boolean update(Person person, User user, AffectedGroup affectedGroup, Files files, Files fileNow) {
        return Db.tx(() -> {
            if (files != null && !files.update()) {
                return false;
            }
            if (fileNow != null && !fileNow.update()) {
                return false;
            }
            return person.update() && user.update() && affectedGroupService.saveOrUpdate(affectedGroup);
        });
    }
}