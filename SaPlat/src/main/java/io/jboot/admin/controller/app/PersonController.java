package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ResultCode;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.api.RoleService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.PersonRegisterValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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


    public void index(){
        User loginUser = AuthUtils.getLoginUser();
        Person person = personService.findByUser(loginUser);
        setAttr("person",person)
                .setAttr("user",loginUser)
                .render("main.html");
    }

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

    public void upload(){
        UploadFile upload = getFile("file",new SimpleDateFormat("YYYY-MM-DD").format(new Date()));
        ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();
        map.put("path",upload.getFile().getAbsolutePath());
        map.put("code", ResultCode.SUCCESS);
        renderJson(map);
    }

    @Before(POST.class)
    public void update(){
        User loginUser = AuthUtils.getLoginUser();
        loginUser.setEmail(getPara("user.email"));
        Person person = personService.findByUser(loginUser);
        person.setPhone(getPara("person.phone"));
        person.setAge(Integer.parseInt(getPara("person.age")));
        person.setAddr(getPara("person.addr"));
        if (!personService.update(person,loginUser)){
            renderJson(RestResult.buildError("用户更新失败"));
            throw new BusinessException("用户更新失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
