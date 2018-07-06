package io.jboot.admin.controller.app;

import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 23:44 2018/7/6
 */
@RequestMapping("/app/person")
public class PersonController extends BaseController{
    public void register(){
        render("register.html");
    }

    public void postRegister(){
        Person person = getBean(Person.class, "person");
        System.out.println(person.toJson());
    }
}
