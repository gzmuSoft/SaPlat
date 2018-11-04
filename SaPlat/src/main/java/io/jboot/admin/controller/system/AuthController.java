package io.jboot.admin.controller.system;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.CacheKey;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.plugin.shiro.ShiroCacheUtils;
import io.jboot.admin.base.plugin.shiro.cache.ShiroCacheManager;
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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

@RequestMapping("/system/auth")
public class AuthController extends BaseController {
    @JbootrpcService
    private AuthService authService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private RoleService roleService;


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

    @JbootrpcService
    private FileFormService fileFormService;


//    @JbootrpcService
//    private ExpertGroupService expertGroupService;

    @Before(GET.class)
    public void index() {
        List<Role> roleList = roleService.findByStatusUsed();
        BaseStatus roleStatus = new RoleStatus();
        for (Role role : roleList) {
            roleStatus.add(role.getId().toString(), role.getName());
        }
        BaseStatus typeStatus = new BaseStatus() {
        };
        typeStatus.add("0", "个人审核");
        typeStatus.add("1", "团体审核");
        setAttr("typeStatus", typeStatus).setAttr("roleStatus", roleStatus).render("main.html");
    }

    @Before(GET.class)
    @NotNullPara("id")
    public void view() {
        Long id = getParaToLong("id");
        Auth auth = authService.findById(id);
        if (auth == null) {
            throw new BusinessException("没有这个审核");
        }
        User user = userService.findById(auth.getUserId());
        Person person = personService.findById(user.getUserID());
        Organization organization = organizationService.findById(user.getUserID());
        if (!(auth.getType().equals(TypeStatus.ORGANIZATION) || auth.getType().equals(TypeStatus.PERSON))) {
            throw new BusinessException("请求参数非法");
        }

        Role role = roleService.findById(auth.getRoleId());
        if ("expertGroup".equals(role.getRemark())) {
            ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
            setAttr("person", person).setAttr("expertGroup", expertGroup).render("expertGroup.html");
        } else if ("facAgency".equals(role.getRemark())) {
            FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
            FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("facAgency", "法人身份证照片", facAgency.getId());
            setAttr("pictrue", fileForm.getFileID());
            fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("facAgency", "维稳备案文件照片", facAgency.getId());
            setAttr("regDocsFilePath", fileForm.getFileID());
            setAttr("organization", organization).setAttr("facAgency", facAgency).render("fac_agency.html");
        } else if ("management".equals(role.getRemark())) {
            Management management = managementService.findByOrgId(organization.getId());
            setAttr("organization", organization).setAttr("management", management).render("management.html");
        } else if ("enterprise".equals(role.getRemark())) {
            Enterprise enterprise = enterpriseService.findByOrgId(organization.getId());
            FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("enterprise", "法人身份证照片", enterprise.getId());
            setAttr("pictrue", fileForm.getFileID());
            setAttr("organization", organization).setAttr("enterprise", enterprise).render("enterprise.html");
        } else if ("reviewGroup".equals(role.getRemark())) {
            ReviewGroup reviewGroup = reviewGroupService.findByOrgId(organization.getId());
            setAttr("organization", organization).setAttr("reviewGroup", reviewGroup).render("review_group.html");
        } else if ("profGroup".equals(role.getRemark())) {
            ProfGroup profGroup = profGroupService.findByOrgId(organization.getId());
            FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("profGroup", "管理员身份证照片", profGroup.getId());
            setAttr("identity", fileForm.getFileID());
            setAttr("organization", organization).setAttr("profGroup", profGroup).render("prof_group.html");
        } else {
            throw new BusinessException("请求参数非法");
        }
    }

    @Before(GET.class)
    @NotNullPara({"id"})
    public void update() {
        Long id = getParaToLong("id");
        Auth auth = authService.findById(id);
        if (auth == null) {
            throw new BusinessException("没有这个审核");
        }
        if (auth.getStatus().equals(AuthStatus.CANCEL_VERIFY)) {
            throw new BusinessException("用户已取消审核");
        }
        /*BaseStatus authStatus = new BaseStatus() {
        };
        authStatus.add("2", "审核成功");
        authStatus.add("1", "审核失败");
        setAttr("authStatus", authStatus).setAttr("auth", auth).render("update.html");*/
        setAttr("auth", auth).render("update.html");
    }

    @Before(POST.class)
    public void postupdate() {
        Auth auth = getBean(Auth.class, "auth");
        Auth model = authService.findById(auth.getId());
        if (model == null) {
            throw new BusinessException("没有这个审核");
        }
/*        if (!(auth.getStatus().equals(AuthStatus.IS_VERIFY) || auth.getStatus().equals(AuthStatus.NOT_VERIFY))) {
            throw new BusinessException("请求参数非法");
        }*/
        User user = AuthUtils.getLoginUser();
        model.setLastUpdUser(user.getName());
        model.setReply(auth.getReply());
        if (auth.getStatus() == null) {
            model.setStatus("1");
        } else {
            model.setStatus("2");
        }
        if (!authService.update(model)) {
            throw new BusinessException("审核失败");
        }

        //角色审核成功后删除所有缓存
        if (model.getStatus().equals(AuthStatus.IS_VERIFY)) {
            ShiroCacheUtils.clearAuthorizationInfoAll();
        }
        renderJson(RestResult.buildSuccess());
    }

    @Before(GET.class)
    @NotNullPara({"pageNumber", "pageSize"})
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Auth auth = new Auth();
        if (getParaToLong("userId") != null) {
            auth.setUserId(getParaToLong("userId"));
        }
        if (!"".equals(getPara("status"))) {
            auth.setStatus(getPara("status"));
        }
        if (!"".equals(getPara("type"))) {
            auth.setType(getPara("type"));
        }
        if (!"".equals(getPara("name"))) {
            auth.setName(getPara("name"));
        }
        Page<Auth> page = authService.findPage(auth, pageNumber, pageSize);
        renderJson(new DataTable<Auth>(page));
    }
}




