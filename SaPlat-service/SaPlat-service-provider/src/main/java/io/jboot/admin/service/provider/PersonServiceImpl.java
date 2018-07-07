package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class PersonServiceImpl extends JbootServiceBase<Person> implements PersonService {

    @Inject
    private UserService userService;

    @Override
    public boolean savePerson(Person model, User user, Long[] roles) {
        return Db.tx(() -> userService.saveUser(user, roles) && save(model));
    }

    //@Override
    public Person findByUser(User user) {
        return DAO.findFirstByColumn("name", user.getName());
    }

    @Override
    public boolean update(Person person, User user) {
        return Db.tx(() -> person.update() && user.update());
    }

}