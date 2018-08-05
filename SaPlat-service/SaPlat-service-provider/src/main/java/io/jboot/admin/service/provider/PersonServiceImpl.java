package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AffectedGroupService;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class PersonServiceImpl extends JbootServiceBase<Person> implements PersonService {

    @Inject
    private UserService userService;

    @Inject
    private AffectedGroupService affectedGroupService;

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
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
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
        return findById(user.getUserID());
    }

    @Override
    public Person findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public boolean update(Person person, User user) {
        return true;
    }

    @Override
    public boolean update(Person person, User user, AffectedGroup affectedGroup) {
        return Db.tx(() -> {
            AffectedGroup group = affectedGroupService.findByPersonId(person.getId());
            if (group != null) {
                affectedGroup.setId(group.getId());
            }
            return update(person) && userService.update(user) && affectedGroupService.saveOrUpdate(affectedGroup);
        });
    }


}