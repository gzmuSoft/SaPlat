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
import java.util.*;
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

    @JbootrpcService
    private UserRoleService userRoleService;

    @JbootrpcService
    private AuthService authService;



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
        User loginUser = AuthUtils.getLoginUser();
        Auth auth = authService.findByUserIdAndStatus(loginUser.getId(), 2);
        List<Auth> auth1 = authService.findByUserIdAndStatusToList(loginUser.getId(), 1);
        List<String> list = new ArrayList<>();
        if (auth == null) {
            if (auth1 != null) {
                for (int i = 0; i < auth1.size(); i++) {
                    list.add(roleService.findById(auth1.get(i).getRoleId()).getName());
                }
                setSessionAttr("nameList", list).render("prove.html");
            } else {
                render("prove.html");
            }
        } else {
            setAttr("name", roleService.findById(auth.getRoleId()).getName())
                    .setAttr("Method", roleService.findById(auth.getRoleId()).getLastUpdAcct())
                    .render("proveing.html");
        }
    }

    public void management() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        Management management = managementService.findByOrgID(organization.getId());
        if (management == null) {
            management = new Management();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatus(loginUser.getId(), 2) == null) {
            setAttr("organization", organization).setAttr("management", management)
                    .setAttr("flag", 0).render("management.html");
        } else {
            setAttr("organization", organization).setAttr("management", management)
                    .setAttr("flag", 1).render("management.html");
        }
    }

    @Before(POST.class)
    public void ManagementProve() {
        Management management = getBean(Management.class,"management");
        management.setCreateTime(new Date());
        management.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("管理机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus("2");
        if (managementService.save(management) && authService.save(auth)) {
            renderJson(RestResult.buildSuccess("提交审核成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void enterprise() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        Enterprise enterprise = enterpriseService.findByOrgID(organization.getId());
        if (enterprise == null) {
            enterprise = new Enterprise();
        }
        if (authService.findByUserIdAndStatus(loginUser.getId(), 2) == null) {
            setAttr("organization", organization).setAttr("enterprise", enterprise)
                    .setAttr("flag", 0).render("enterprise.html");
        } else {
            setAttr("organization", organization).setAttr("enterprise", enterprise)
                    .setAttr("flag", 1).render("enterprise.html");
        }
    }

    @Before(POST.class)
    public void enterpriseProve() {
        Enterprise enterprise = getBean(Enterprise.class,"enterprise");
        enterprise.setCreateTime(new Date());
        enterprise.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("企业机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus("2");
        if (enterpriseService.save(enterprise) && authService.save(auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void facAgency() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        FacAgency facAgency = facAgencyService.findByOrgID(organization.getId());
        if (facAgency == null) {
            facAgency = new FacAgency();
        }
        if (authService.findByUserIdAndStatus(loginUser.getId(), 2) == null) {
            setAttr("organization", organization).setAttr("fac_agency", facAgency)
                    .setAttr("flag", 0).render("fac_agency.html");
        } else {
            setAttr("organization", organization).setAttr("fac_agency", facAgency)
                    .setAttr("flag", 1).render("fac_agency.html");
        }
    }

    @Before(POST.class)
    public void facAgencyProve() {
        FacAgency fac_agency = getBean(FacAgency.class,"fac_agency");
        fac_agency.setCreateTime(new Date());
        fac_agency.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("服务机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus("2");
        if (facAgencyService.save(fac_agency) && authService.save(auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void profGroup() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        ProfGroup profGroup = profGroupService.findByOrgID(organization.getId());
        if (profGroup == null) {
            profGroup = new ProfGroup();
        }
        if (authService.findByUserIdAndStatus(loginUser.getId(), 2) == null) {
            setAttr("organization", organization).setAttr("prof_group", profGroup)
                    .setAttr("flag", 0).render("prof_group.html");
        } else {
            setAttr("organization", organization).setAttr("prof_group", profGroup)
                    .setAttr("flag", 1).render("prof_group.html");
        }
    }

    @Before(POST.class)
    public void profGroupProve() {
        ProfGroup prof_group = getBean(ProfGroup.class,"prof_group");
        prof_group.setCreateTime(new Date());
        prof_group.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("专业团体").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus("2");
        if (profGroupService.save(prof_group) && authService.save(auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    public void reviewGroup() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        ReviewGroup reviewGroup = reviewGroupService.findByOrgID(organization.getId());
        if (reviewGroup == null) {
            reviewGroup = new ReviewGroup();
        }
        if (authService.findByUserIdAndStatus(loginUser.getId(), 2) == null) {
            setAttr("organization", organization).setAttr("review_group", reviewGroup)
                    .setAttr("flag", 0).render("review_group.html");
        } else {
            setAttr("organization", organization).setAttr("review_group", reviewGroup)
                    .setAttr("flag", 1).render("review_group.html");
        }
    }

    @Before(POST.class)
    public void reviewGroupProve() {
        ReviewGroup review_group = getBean(ReviewGroup.class,"review_group");
        review_group.setCreateTime(new Date());
        review_group.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("审查团体").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus("2");
        if (reviewGroupService.save(review_group) && authService.save(auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }
}
