package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import io.jboot.admin.base.common.AuthStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.ExpertGroup;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.PersonRegisterValidator;
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
public class PersonController extends BaseController {
    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private PersonService personService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private ExpertGroupService expertGroupService;

    @JbootrpcService
    private AuthService authService;

    /**
     * 初始化
     */
    public void index() {
        User loginUser = AuthUtils.getLoginUser();
        Person person = personService.findByUser(loginUser);
        System.out.println(loginUser.toJson());
        System.out.println(person.toJson());
        setAttr("person", person)
                .setAttr("user", loginUser)
                .render("main.html");
    }

    /**
     * 注册页面
     */
    public void register() {
        render("register.html");
    }

    /**
     * 注册方法
     */
    @Before({POST.class, PersonRegisterValidator.class})
    public void postRegister() {
        Person person = getBean(Person.class, "person");
        User user = getBean(User.class, "user");
        user.setPhone(person.getPhone());
        if (userService.hasUser(user.getName())) {
            renderJson(RestResult.buildError("用户名已存在"));
            throw new BusinessException("用户名已存在");
        }
        person.setCreateTime(new Date());
        person.setLastAccessTime(new Date());
        person.setIsEnable(1);
        Long[] roles = new Long[]{roleService.findByName("个人群体").getId()};
        if (!personService.savePerson(person, user, roles)) {
            renderJson(RestResult.buildError("用户保存失败"));
            throw new BusinessException("用户保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

//    public void upload(){
//        UploadFile upload = getFile("file",new SimpleDateFormat("YYYY-MM-DD").format(new Date()));
//        ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();
//        map.put("path",upload.getFile().getAbsolutePath());
//        map.put("code", ResultCode.SUCCESS);
//        renderJson(map);
//    }

    /**
     * 更新用户资料
     */
    @Before(POST.class)
    public void postUpdate() {
        User loginUser = AuthUtils.getLoginUser();
        loginUser.setEmail(getPara("user.email"));
        Person person = personService.findByUser(loginUser);
        person.setPhone(getPara("person.phone"));
        person.setAge(Integer.parseInt(getPara("person.age")));
        person.setAddr(getPara("person.addr"));
        if (!personService.update(person, loginUser)) {
            renderJson(RestResult.buildError("用户更新失败"));
            throw new BusinessException("用户更新失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 专家团体页面
     * 如果存在 设置 status
     */
    public void expertGroup() {
        User user = AuthUtils.getLoginUser();
        ExpertGroup expertGroup = expertGroupService.findByPersonId(user.getUserID());
        Auth auth = new Auth();
        if (expertGroup != null) {
            auth = authService.findByUser(user);
        }
        setAttr("auth",auth);
        setAttr("expertGroup", expertGroup);
        render("expertGroup.html");
    }

    /**
     * 专家团体认证
     */
    public void expertGroupVerify() {
        ExpertGroup expertGroup = getBean(ExpertGroup.class, "expertGroup");
        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        expertGroup.setName(person.getName());
        expertGroup.setPhone(person.getPhone());
        expertGroup.setMail(user.getEmail());
        expertGroup.setCreateTime(new Date());
        expertGroup.setLastAccessTime(new Date());
        expertGroup.setPersonID(person.getId());
        expertGroup.setIsEnable(1);
        ExpertGroup name = expertGroupService.findByName(expertGroup.getName());
        if ( name != null ) {
            if (name.getIsEnable() == 1){
                renderJson(RestResult.buildError("专家团体已存在"));
                throw new BusinessException("专家团体已存在");
            }
            expertGroup.setId(name.getId());
        }


        Auth auth = new Auth();
        auth.setUserId(user.getId());
        auth.setRoleId(roleService.findByName("专家团体").getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus(AuthStatus.VERIFYING);
        if (!expertGroupService.saveOrUpdateExpertGroupAndAuth(expertGroup, auth)) {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 专家团体取消认证
     */
    public void cancelExpertGroupAuth(){
        User user = AuthUtils.getLoginUser();
        ExpertGroup expertGroup = expertGroupService.findByPersonId(personService.findByUser(user).getId());
        Auth auth = authService.findByUser(user);
        auth.setStatus(AuthStatus.NOT_VERIFY);
        expertGroup.setIsEnable(0);
        if (!expertGroupService.saveOrUpdateExpertGroupAndAuth(expertGroup,auth)){
            renderJson(RestResult.buildError("修改认证状态是啊比"));
            throw new BusinessException("修改认证状态是啊比");
        }
        renderJson(RestResult.buildSuccess());
    }
}
