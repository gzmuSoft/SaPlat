package io.jboot.admin.controller.system;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.service.entity.status.system.RoleStatus;
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Administrator on 2018/7/13.
 */
@RequestMapping("/system/projectauth")
public class ProjectAuthController extends BaseController {

    @JbootrpcService
    private AuthService authService;


    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private AuthProjectService authProjectService;


    @JbootrpcService
    private UserService userService;

    //团队
    @JbootrpcService
    private OrganizationService organizationService;


    //个人
    @JbootrpcService
    private PersonService personService;

    @JbootrpcService
    private ExpertGroupService expertGroupService;

    @JbootrpcService
    private ManagementService managementService;

    @JbootrpcService
    private EnterpriseService enterpriseService;

    @JbootrpcService
    private FacAgencyService facAgencyService;

    @JbootrpcService
    private ProfGroupService profGroupService;

    @JbootrpcService
    private ReviewGroupService reviewGroupService;

    @Before(GET.class)
    public void index() {
        render("main.html");
    }


    @Before(GET.class)
    public void verifyIndex() {
        List<Role> roleList = roleService.findByStatusUsed();
        BaseStatus roleStatus = new RoleStatus();
        for (Role role : roleList) {
            roleStatus.add(role.getId().toString(), role.getName());
        }


        setAttr("roleStatus", roleStatus).render("verifyIndex.html");
    }

    @Before(GET.class)
    public void dataIndex() {
        List<Role> roleList = roleService.findByStatusUsed();
        BaseStatus roleStatus = new RoleStatus();
        for (Role role : roleList) {
            roleStatus.add(role.getId().toString(), role.getName());
        }
        setAttr("roleStatus", roleStatus).render("dataIndex.html");
    }

    @Before(GET.class)
    @NotNullPara("id")
    public void verifyUpdate() {
        Long id = getParaToLong("id");
        Auth auth = authService.findById(id);
        if (auth == null) {
            throw new BusinessException("没有这个审核");
        }
        BaseStatus authStatus = new BaseStatus() {
        };
        authStatus.add("2", "审核成功");
        authStatus.add("1", "审核失败");
        setAttr("authStatus", authStatus).setAttr("auth", auth).render("verifyupdate.html");
    }

    @Before(GET.class)
    @NotNullPara("id")
    public void dataUpdate() {
        Long id = getParaToLong("id");
        AuthProject authProject = authProjectService.findById(id);
        if (authProject == null) {
            throw new BusinessException("没有这个审核");
        }
        BaseStatus authStatus = new BaseStatus() {
        };
        authStatus.add("2", "审核成功");
        authStatus.add("1", "审核失败");
        setAttr("authStatus", authStatus).setAttr("authProject", authProject).render("dataupdate.html");
    }


    @Before(POST.class)
    public void verifyPostupdate() {
        Auth auth = getBean(Auth.class, "auth");
        Auth model = authService.findById(auth.getId());
        if (model == null) {
            throw new BusinessException("没有这个审核");
        }
        if (!(auth.getStatus().equals(AuthStatus.IS_VERIFY) || auth.getStatus().equals(AuthStatus.NOT_VERIFY))) {
            throw new BusinessException("请求参数非法");
        }
        User user = AuthUtils.getLoginUser();
        model.setLastUpdUser(user.getName());
        model.setReply(auth.getReply());
        model.setStatus(auth.getStatus());
        if (!authService.update(model)) {
            throw new BusinessException("审核失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @Before(POST.class)
    public void dataPostupdate() {
        AuthProject authProject = getBean(AuthProject.class, "authProject");
        AuthProject model = authProjectService.findById(authProject.getId());
        if (model == null) {
            throw new BusinessException("没有这个审核");
        }
        if (!(authProject.getStatus().equals(AuthStatus.IS_VERIFY) || authProject.getStatus().equals(AuthStatus.NOT_VERIFY))) {
            throw new BusinessException("请求参数非法");
        }
        User user = AuthUtils.getLoginUser();
        model.setLastUpdUser(user.getName());
        model.setReply(authProject.getReply());
        model.setStatus(authProject.getStatus());
        if (!authProjectService.update(model)) {
            throw new BusinessException("审核失败");
        }
        renderJson(RestResult.buildSuccess());
    }


    @Before(GET.class)
    @NotNullPara({"pageNumber", "pageSize"})
    public void verifyTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Auth auth = new Auth();
        if (getParaToLong("userId") != null) {
            auth.setUserId(getParaToLong("userId"));
        }
        if (!"".equals(getPara("status"))) {
            auth.setStatus(getPara("status"));
        }
        if (!"".equals(getPara("name"))) {
            auth.setName(getPara("name"));
        }
        auth.setType(TypeStatus.PROJECT_VERIFY);
        Page<Auth> page = authService.findPage(auth, pageNumber, pageSize);
        renderJson(new DataTable<Auth>(page));
    }


    @Before(GET.class)
    @NotNullPara({"pageNumber", "pageSize"})
    public void dataTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        AuthProject authProject = new AuthProject();
        if (getParaToLong("userId") != null) {
            authProject.setUserId(getParaToLong("userId"));
        }
        if (!"".equals(getPara("status"))) {
            authProject.setStatus(getPara("status"));
        }
        if (!"".equals(getPara("name"))) {
            authProject.setName(getPara("name"));
        }
        authProject.setType(TypeStatus.PROJECT_VERIFY);
        Page<AuthProject> page = authProjectService.findPage(authProject, pageNumber, pageSize);
        renderJson(new DataTable<AuthProject>(page));
    }


    @Before(GET.class)
    @NotNullPara("id")
    public void verifyView() {
        Long id = getParaToLong("id");
        Auth auth = authService.findById(id);
        if (auth == null) {
            throw new BusinessException("没有这个审核");
        }
        User user = userService.findById(auth.getUserId());
        Person person = personService.findById(user.getUserID());
        Organization organization = organizationService.findById(user.getUserID());

        if (!auth.getType().equals(TypeStatus.PROJECT_VERIFY)) {
            throw new BusinessException("请求参数非法");
        }

        Role role = roleService.findById(auth.getRoleId());
        if ("expertGroup".equals(role.getRemark())) {
            ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
            setAttr("person", person).setAttr("expertGroup", expertGroup).render("expertGroup.html");
        } else if ("fac_agency".equals(role.getRemark())) {
            FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
            setAttr("organization", organization).setAttr("fac_agency", facAgency).render("fac_agency.html");
        } else if ("management".equals(role.getRemark())) {
            Management management = managementService.findByOrgId(organization.getId());
            setAttr("organization", organization).setAttr("management", management).render("management.html");
        } else if ("enterprise".equals(role.getRemark())) {
            Enterprise enterprise = enterpriseService.findByOrgId(organization.getId());
            setAttr("organization", organization).setAttr("enterprise", enterprise).render("enterprise.html");
        } else if ("review_group".equals(role.getRemark())) {
            ReviewGroup reviewGroup = reviewGroupService.findByOrgId(organization.getId());
            setAttr("organization", organization).setAttr("review_group", reviewGroup).render("review_group.html");
        } else if ("prof_group".equals(role.getRemark())) {
            ProfGroup profGroup = profGroupService.findByOrgId(organization.getId());
            setAttr("organization", organization).setAttr("prof_group", profGroup).render("review_group.html");
        } else {
            throw new BusinessException("请求参数非法");
        }
    }
//
//    @Before(GET.class)
//    @NotNullPara("id")
//    public void dataView() {
//    }
}
