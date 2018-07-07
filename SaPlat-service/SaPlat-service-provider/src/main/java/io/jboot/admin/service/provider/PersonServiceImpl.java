package io.jboot.admin.service.provider;

<<<<<<< HEAD
import com.jfinal.plugin.activerecord.Db;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
=======
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.service.JbootServiceBase;

>>>>>>> origin/master
import javax.inject.Singleton;

@Bean
@Singleton
<<<<<<< HEAD
@JbootrpcService
public class PersonServiceImpl extends JbootServiceBase<Person> implements PersonService {

    @Inject
    private UserService userService;

    @Override
    public boolean savePerson(Person model, User user, Long[] roles) {
        return Db.tx(() -> userService.saveUser(user, roles) && save(model));
    }
=======
public class PersonServiceImpl extends JbootServiceBase<Person> implements PersonService {

>>>>>>> origin/master
}