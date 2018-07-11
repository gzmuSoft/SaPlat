package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ResultCode;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.OrganizationValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * -----------------------------
 *
 * @author LiuChuanjin
 * @version 2.0
 * -----------------------------
 * @date 23:44 2018/7/6
 */
@RequestMapping("/app/organization")
public class OrganizationController extends BaseController{
    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private OrganizationService organizationService ;

    @JbootrpcService
    private RoleService roleService;

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


    public void register(){
        render("register.html");
    }

    @Before({POST.class, OrganizationValidator.class})
    public void postRegister(){
        Organization organization = getBean(Organization.class, "organization");
        User user = getBean(User.class,"user");
        if (userService.hasUser(user.getName())) {
            renderJson(RestResult.buildError("用户名已存在"));
            throw new BusinessException("用户名已存在");
        }
        if (organizationService.hasUser(organization.getName())) {
            renderJson(RestResult.buildError("组织机构已存在"));
            throw new BusinessException("组织机构已存在");
        }
        Long[] roles = new Long[]{roleService.findByName("组织机构").getId()};
        user.setPhone(organization.getContact());
        user.setOnlineStatus("0");
        user.setUserSource(1);

        if (!organizationService.saveOrganization(organization,user,roles)){
            renderJson(RestResult.buildError("用户保存失败"));
            throw new BusinessException("用户保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void index(){
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());

        setAttr("organization",organization).setAttr("user",loginUser).render("main.html");
    }

    public void upload(){
        UploadFile upload = getFile("file",new SimpleDateFormat("YYYY-MM-DD").format(new Date()));
        ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();
        map.put("path",upload.getFile().getAbsolutePath());
        map.put("code", ResultCode.SUCCESS);
        renderJson(map);
    }

    @Before({POST.class, OrganizationValidator.class})
    public void update(){
        User loginUser = AuthUtils.getLoginUser();
        loginUser.setEmail(getPara("user.email"));
        Organization organization = organizationService.findById(loginUser.getUserID());
        organization.setContact(getPara("organization.contact"));
        organization.setAddr(getPara("organization.addr"));
        organization.setPrincipal(getPara("organization.principal"));
        if (!organizationService.update(organization ,loginUser)){
            renderJson(RestResult.buildError("用户更新失败"));
            throw new BusinessException("用户更新失败");
        }
        renderJson(RestResult.buildSuccess());
    }



    public void prove(){
        render("prove.html");
    }

    public void management() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        setAttr("organization",organization).render("management.html");
    }

    @Before(POST.class)
    public void ManagementProve() {
        Management management = getBean(Management.class,"management");
        management.setCreateTime(new Date());
        management.setLastAccessTime(new Date());
        if (managementService.save(management)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void enterprise() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        setAttr("organization",organization).render("enterprise.html");
    }

    @Before(POST.class)
    public void enterpriseProve() {
        Enterprise enterprise = getBean(Enterprise.class,"enterprise");
        enterprise.setCreateTime(new Date());
        enterprise.setLastAccessTime(new Date());
        if (enterpriseService.save(enterprise)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void facAgency() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        setAttr("organization",organization).render("fac_agency.html");
    }

    @Before(POST.class)
    public void facAgencyProve() {
        FacAgency fac_agency = getBean(FacAgency.class,"fac_agency");
        fac_agency.setCreateTime(new Date());
        fac_agency.setLastAccessTime(new Date());
        if (facAgencyService.save(fac_agency)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void profGroup() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        setAttr("organization",organization).render("prof_group.html");
    }

    @Before(POST.class)
    public void profGroupProve() {
        ProfGroup prof_group = getBean(ProfGroup.class,"prof_group");
        prof_group.setCreateTime(new Date());
        prof_group.setLastAccessTime(new Date());
        if (profGroupService.save(prof_group)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void reviewGroup() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        setAttr("organization",organization). render("review_group.html");
    }

    @Before(POST.class)
    public void reviewGroupProve() {
        ReviewGroup review_group = getBean(ReviewGroup.class,"review_group");
        review_group.setCreateTime(new Date());
        review_group.setLastAccessTime(new Date());
        if (reviewGroupService.save(review_group)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }
}
