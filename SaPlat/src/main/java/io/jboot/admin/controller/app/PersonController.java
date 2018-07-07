package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.api.RoleService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.Role;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.validator.PersonRegisterValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;

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
    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private PersonService personService;

    @JbootrpcService
    private RoleService roleService;


    public void register(){
        render("register.html");
    }

    @Before({POST.class, PersonRegisterValidator.class})
    public void postRegister(){
        Person person = getBean(Person.class, "person");
        if (userService.hasUser(person.getName())){
            renderJson(RestResult.buildError("用户名已存在"));
            throw new BusinessException("用户名已存在");
        }
        person.setCreateTime(new Date());
        person.setLastAccessTime(new Date());
        person.setIsEnable(1);
        User user = getBean(User.class,"user");
        Long[] roles = new Long[]{roleService.findByName("个人群体").getId()};
        user.setName(person.getName());
        user.setPhone(person.getPhone());
        if(!personService.savePerson(person,user,roles)){
            renderJson(RestResult.buildError("用户保存失败"));
            throw new BusinessException("用户保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
