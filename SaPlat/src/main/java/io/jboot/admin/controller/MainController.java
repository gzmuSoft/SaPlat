package io.jboot.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.Consts;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.plugin.shiro.MuitiLoginToken;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.LoginValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.zookeeper.Login;

import java.util.List;
import java.util.Map;


/**
 * 主控制器
 *
 * @author Rlax
 */
@RequestMapping("/")
public class MainController extends BaseController {
    @JbootrpcService
    private UserRoleService userRoleService;

    @JbootrpcService
    private NotificationService notificationService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private NewsService newsService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private StructPersonLinkService structPersonLinkService;

    @JbootrpcService
    private PersonService personService;

    public void index() {
        render("index.html");
    }

    public void login() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            redirect("/");
        } else {
            Page<News> news = newsService.findReverses(5);
            DataTable newsTable = new DataTable<News>(news);
            setAttr("newsList", newsTable.getData());
            render("login.html");
        }
    }


    public void nshow() {
        int id = getParaToInt("id");
        News model = newsService.findById(id);
        System.out.println(model.toString());
        setAttr("model", model).
                render("nshow.html");
    }

    public void nlist() {
        int current = getParaToInt("current", 1);
        int pageNum = getParaToInt("pageNum", 20);
        News model = new News();
        Page<News> page = newsService.findPage(model, current, pageNum);
        DataTable newsTable = new DataTable<News>(page);
        //System.out.println("newsTable.getData():" + newsTable.getData().toString());
        setAttr("newsList", newsTable.getData()).
                setAttr("current", current).
                render("nlist.html");
    }

    public void nlistPage() {
        int current = getParaToInt("current", 1);
        int pageNum = getParaToInt("pageNum", 20);
        News model = new News();
        Page<News> page = newsService.findPage(model, current, pageNum);
        DataTable newsTable = new DataTable<News>(page);
        setAttr("newsList", newsTable.getData()).
                setAttr("current", current).
                render("nlist.html");
    }

    public void register() {
        render("register.html");
    }

    public void captcha() {
        renderCaptcha();
    }


    @Before({POST.class, LoginValidator.class})
    public void postLogin() {
        String loginName = getPara("loginName");
        String pwd = getPara("password");

        MuitiLoginToken token = new MuitiLoginToken(loginName, pwd);
        Subject subject = SecurityUtils.getSubject();

        RestResult<String> restResult = new RestResult<String>();
        restResult.success().setMsg("登录成功");

        try {
            if (!subject.isAuthenticated()) {
                token.setRememberMe(false);
                subject.login(token);

                User u = userService.findByName(loginName);
                subject.getSession(true).setAttribute(Consts.SESSION_USER, u);
            }
            if (getParaToBoolean("rememberMe") != null && getParaToBoolean("rememberMe")) {
                setCookie("loginName", loginName, 60 * 60 * 24 * 7);
            } else {
                removeCookie("loginName");
            }
        } catch (UnknownAccountException une) {
            restResult.error("用户名不存在");
        } catch (LockedAccountException lae) {
            restResult.error("用户被锁定");
        } catch (IncorrectCredentialsException ine) {
            restResult.error("用户名或密码不正确");
        } catch (ExcessiveAttemptsException exe) {
            restResult.error("账户密码错误次数过多，账户已被限制登录1小时");
        } catch (Exception e) {
            e.printStackTrace();
            restResult.error("服务异常，请稍后重试");
        }

        renderJson(restResult);
    }

    /**
     * 退出
     */
    public void logout() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            SecurityUtils.getSubject().logout();
        }
        Page<News> news = newsService.findReverses(5);
        DataTable newsTable = new DataTable<News>(news);
        setAttr("newsList", newsTable.getData()).
                render("login.html");
    }

    public void welcome() {
        User loginUser = AuthUtils.getLoginUser();
        Long role = 0L;
        List<UserRole> userRoles = userRoleService.findListByUserId(loginUser.getId());
        for (UserRole userRole:userRoles) {
            role = userRole.getRoleID();
            if (userRole.getRoleID() == 1) {
                break;
            }
            if (userRole.getRoleID() == 3) {
                break;
            }
        }
        loginUser = userService.findById(loginUser.getId());
        if (role == 1){
            //人数 个人 组织 项目数
            List<User> users = userService.findAll();
            List<UserRole> roles = userRoleService.findAll();
            Long personAmount = 0L,organizationAmount = 0L;
            for (UserRole userRole: roles) {
                if (userRole.getRoleID() == 2) {
                    personAmount++;
                }
                if (userRole.getRoleID() == 3) {
                    organizationAmount++;
                }
            }
            setAttr("userAmount",users.size()).
                    setAttr("projectAmount",projectService.findAll().size()).
                    setAttr("personAmount",personAmount).
                    setAttr("organizationAmount",organizationAmount).
                    setAttr("role",role).
                    setAttr("user",loginUser).
                    render("welcome.html");
        }else if (role == 3){
            List<Project> projectList = projectService.findByUserId(loginUser.getId());
            Long assessAmount = 0L,auditAmount = 0L, reviewAmount = 0L ;
            for (Project project:projectList) {
                if ("4".equals(project.getStatus())) {
                    assessAmount++;
                }
                if ("3".equals(project.getStatus())) {
                    auditAmount++;
                }
                if ("5".equals(project.getStatus())) {
                    reviewAmount++;
                }
            }
            setAttr("projectAmount",projectList.size()).//已有项目
                    setAttr("assessAmount",assessAmount).//待评项目
                    setAttr("auditAmount",auditAmount).//待审核
                    setAttr("reviewAmount",reviewAmount).//待审查
                    setAttr("role",role).
                    setAttr("user",loginUser).
                    render("welcome.html");
        }else {
            Map<String,Object> map = structPersonLinkService.findStructureListByPersonID(personService.findById(loginUser.getUserID()).getId());
            setAttr("role",role).
                    setAttr("count",map.get("count"));
                    setAttr("user",loginUser).
                    render("welcome.html");
        }
    }

    public void message() {
        User loginUser = AuthUtils.getLoginUser();
        boolean sta = notificationService.findMessageByUserID(loginUser.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sta", sta);
        jsonObject.put("code", 0);
        renderJson(jsonObject);
    }

    public void view() {
        User loginUser = AuthUtils.getLoginUser();
        //当前用户权限
        List<UserRole> roles = userRoleService.findListByUserId(loginUser.getId());
        for (UserRole list : roles) {
            if (list.getRoleID() == 1) {
                render("system/notification/main.html");
                break;
            } else {
                render("app/notification/main.html");
                break;
            }
        }
    }

    public void update(){
        User user = new User();
        user.setId(getParaToLong("id"));
        user.setName(getPara("name"));
        user.setPhone(getPara("phone"));
        user.setEmail(getPara("email"));
        if (!userService.update(user)) {
            throw new BusinessException("保存失败!");
        }
        renderJson(RestResult.buildSuccess());
    }
}
