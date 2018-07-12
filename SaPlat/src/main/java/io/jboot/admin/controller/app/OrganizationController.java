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
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.OrganizationValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * -----------------------------
 *
 * @author LiuChuanjin
 * @version 2.0
 *          -----------------------------
 * @date 23:44 2018/7/6
 */
@RequestMapping("/app/organization")
public class OrganizationController extends BaseController {

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private OrganizationService organizationService;

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


    /**
     * 跳转注册页面
     */
    public void register() {
        render("register.html");
    }

    /**
     * 组织结构注册功能
     */
    @Before({POST.class, OrganizationValidator.class})
    public void postRegister() {
        Organization organization = getBean(Organization.class, "organization");
        User user = getBean(User.class, "user");
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

        if (!organizationService.saveOrganization(organization, user, roles)) {
            renderJson(RestResult.buildError("用户保存失败"));
            throw new BusinessException("用户保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 组织机构基本资料初始化至信息管理界面
     */
    public void index() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());

        setAttr("organization", organization).setAttr("user", loginUser).render("main.html");
    }

    /**
     * 文件上传（未实现）
     */
    public void upload() {
        UploadFile upload = getFile("file", new SimpleDateFormat("YYYY-MM-DD").format(new Date()));
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("path", upload.getFile().getAbsolutePath());
        map.put("code", ResultCode.SUCCESS);
        renderJson(map);
    }

    /**
     * 组织机构信息编辑
     * 更新提交至数据库
     */
    @Before({POST.class, OrganizationValidator.class})
    public void update() {
        User loginUser = AuthUtils.getLoginUser();
        loginUser.setEmail(getPara("user.email"));
        Organization organization = organizationService.findById(loginUser.getUserID());
        organization.setContact(getPara("organization.contact"));
        organization.setAddr(getPara("organization.addr"));
        organization.setPrincipal(getPara("organization.principal"));
        if (!organizationService.update(organization, loginUser)) {
            renderJson(RestResult.buildError("用户更新失败"));
            throw new BusinessException("用户更新失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 左侧 组织机构认证 菜单选项事件
     * 正在认证时跳转"审核中"页面
     * 未认证时跳转"认证选项按钮"页面
     * 已认证时跳转更新后的 "认证选项按钮"页面
     */
    public void prove() {
        User loginUser = AuthUtils.getLoginUser();
        Auth auth = authService.findByUserIdAndStatus(loginUser.getId(), AuthStatus.VERIFYING);
        List<Auth> auth1 = authService.findByUserIdAndStatusToList(loginUser.getId(), AuthStatus.IS_VERIFY);
        List<String> list = new ArrayList<>();
        if (auth == null) {
            if (auth1 != null) {
                for (int i = 0; i < auth1.size(); i++) {
                    list.add(roleService.findById(auth1.get(i).getRoleId()).getName());
                }
                setAttr("nameList", list).render("prove.html");
            } else {
                render("prove.html");
            }
        } else {
            setAttr("name", roleService.findById(auth.getRoleId()).getName())
                    .setAttr("Method", roleService.findById(auth.getRoleId()).getLastUpdAcct())
                    .render("proveing.html");
        }
    }

    /**
     * 跳转认证管理机构填写信息页面
     * flag = 0时未在认证状态，页面不禁用
     * flag = 1时,页面内容禁用不可编辑
     */
    public void management() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        Management management = managementService.findByOrgID(organization.getId());
        if (management == null) {
            management = new Management();
        }
        //flag = 0时未在认证状态，页面不禁用； flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatus(loginUser.getId(), AuthStatus.VERIFYING) == null) {
            setAttr("organization", organization).setAttr("management", management)
                    .setAttr("flag", 0).render("management.html");
        } else {
            setAttr("organization", organization).setAttr("management", management)
                    .setAttr("flag", 1).render("management.html");
        }
    }

    /**
     * 提交认证管理机构数据并进入待审核状态
     */
    @Before(POST.class)
    public void ManagementProve() {
        Management management = getBean(Management.class, "management");
        management.setCreateTime(new Date());
        management.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        //若曾经取消认证则下次认证时获取id进行更新
        Management name = managementService.findByName(management.getName());
        if (name != null) {
//            if (name.getIsEnable() == 0){
//                renderJson(RestResult.buildError("管理机构已存在"));
//                throw new BusinessException("管理机构已存在");
//            }
            management.setId(name.getId());
        }
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("管理机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setStatus(AuthStatus.VERIFYING);
        if (managementService.saveOrUpdate(management, auth)) {
            renderJson(RestResult.buildSuccess("提交审核成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    /**
     * 取消管理机构认证状态并返回编辑信息
     */
    public void managementCancel() {
        User user = AuthUtils.getLoginUser();
        Management management = managementService.findByOrgID(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserAndRole(user, roleService.findByName("管理机构").getId());
        auth.setStatus(AuthStatus.NOT_VERIFY);
        management.setIsEnable(0);
        if (!managementService.saveOrUpdate(management, auth)) {
            renderJson(RestResult.buildError("修改认证状态"));
            throw new BusinessException("修改认证状态");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 跳转认证企业机构填写信息页面
     * flag = 0时未在认证状态，页面不禁用
     * flag = 1时,页面内容禁用不可编辑
     */
    public void enterprise() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        Enterprise enterprise = enterpriseService.findByOrgID(organization.getId());
        if (enterprise == null) {
            enterprise = new Enterprise();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatus(loginUser.getId(), AuthStatus.VERIFYING) == null) {
            setAttr("organization", organization).setAttr("enterprise", enterprise)
                    .setAttr("flag", 0).render("enterprise.html");
        } else {
            setAttr("organization", organization).setAttr("enterprise", enterprise)
                    .setAttr("flag", 1).render("enterprise.html");
        }
    }

    /**
     * 提交认证企业机构数据并进入待审核状态
     */
    @Before(POST.class)
    public void enterpriseProve() {
        Enterprise enterprise = getBean(Enterprise.class, "enterprise");
        enterprise.setCreateTime(new Date());
        enterprise.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        //若曾经取消认证则下次认证时获取id进行更新
        Enterprise name = enterpriseService.findByName(enterprise.getName());
        if (name != null) {
//            if (name.getIsEnable() == 0){
//                renderJson(RestResult.buildError("企业机构已存在"));
//                throw new BusinessException("企业机构已存在");
//            }
            enterprise.setId(name.getId());
        }
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("企业机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setStatus(AuthStatus.VERIFYING);
        if (enterpriseService.saveOrUpdate(enterprise, auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    /**
     * 取消企业机构认证状态并返回编辑信息
     */
    public void enterpriseCancel() {
        User user = AuthUtils.getLoginUser();
        Enterprise enterprise = enterpriseService.findByOrgID(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserAndRole(user, roleService.findByName("企业机构").getId());
        auth.setStatus(AuthStatus.NOT_VERIFY);
        enterprise.setIsEnable(0);
        if (!enterpriseService.saveOrUpdate(enterprise, auth)) {
            renderJson(RestResult.buildError("修改认证状态"));
            throw new BusinessException("修改认证状态");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 跳转认证服务机构信息填写页面
     * flag = 0时未在认证状态，页面不禁用
     * flag = 1时,页面内容禁用不可编辑
     */
    public void facAgency() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        FacAgency facAgency = facAgencyService.findByOrgID(organization.getId());
        if (facAgency == null) {
            facAgency = new FacAgency();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatus(loginUser.getId(), AuthStatus.VERIFYING) == null) {
            setAttr("organization", organization).setAttr("fac_agency", facAgency)
                    .setAttr("flag", 0).render("fac_agency.html");
        } else {
            setAttr("organization", organization).setAttr("fac_agency", facAgency)
                    .setAttr("flag", 1).render("fac_agency.html");
        }
    }

    /**
     * 提交认证服务机构数据并进入待审核状态
     */
    @Before(POST.class)
    public void facAgencyProve() {
        FacAgency fac_agency = getBean(FacAgency.class, "fac_agency");
        fac_agency.setCreateTime(new Date());
        fac_agency.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        //若曾经取消认证则下次认证时获取id进行更新
        FacAgency name = facAgencyService.findByName(fac_agency.getName());
        if (name != null) {
//            if (name.getIsEnable() == 0){
//                renderJson(RestResult.buildError("服务机构已存在"));
//                throw new BusinessException("服务机构已存在");
//            }
            fac_agency.setId(name.getId());
        }
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("服务机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setStatus(AuthStatus.VERIFYING);
        if (facAgencyService.saveOrUpdate(fac_agency, auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    /**
     * 取消服务机构认证并返回编辑信息
     */
    public void facAgencyCancel() {
        User user = AuthUtils.getLoginUser();
        FacAgency fac_agency = facAgencyService.findByOrgID(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserAndRole(user, roleService.findByName("服务机构").getId());
        auth.setStatus(AuthStatus.NOT_VERIFY);
        fac_agency.setIsEnable(0);
        if (!facAgencyService.saveOrUpdate(fac_agency, auth)) {
            renderJson(RestResult.buildError("修改认证状态"));
            throw new BusinessException("修改认证状态");
        }
        renderJson(RestResult.buildSuccess());
    }


    /**
     * 跳转认证审查团体信息填写页面
     * flag = 0时未在认证状态，页面不禁用
     * flag = 1时,页面内容禁用不可编辑
     */
    public void profGroup() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        ProfGroup profGroup = profGroupService.findByOrgID(organization.getId());
        if (profGroup == null) {
            profGroup = new ProfGroup();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatus(loginUser.getId(), AuthStatus.VERIFYING) == null) {
            setAttr("organization", organization).setAttr("prof_group", profGroup)
                    .setAttr("flag", 0).render("prof_group.html");
        } else {
            setAttr("organization", organization).setAttr("prof_group", profGroup)
                    .setAttr("flag", 1).render("prof_group.html");
        }
    }

    /**
     * 提交认证审查团体数据并进入待审核状态
     */
    @Before(POST.class)
    public void profGroupProve() {
        ProfGroup prof_group = getBean(ProfGroup.class, "prof_group");
        prof_group.setCreateTime(new Date());
        prof_group.setLastAccessTime(new Date());
        User loginUser = AuthUtils.getLoginUser();
        //若曾经取消认证则下次认证时获取id进行更新
        ProfGroup name = profGroupService.findByName(prof_group.getName());
        if (name != null) {
//            if (name.getIsEnable() == 0){
//                renderJson(RestResult.buildError("专业团体已存在"));
//                throw new BusinessException("专业团体已存在");
//            }
            prof_group.setId(name.getId());
        }
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("专业团体").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setStatus(AuthStatus.VERIFYING);
        if (profGroupService.saveOrUpdate(prof_group, auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    /**
     * 取消专业团体认证状态并返回编辑信息
     */
    public void profGroupCancel() {
        User user = AuthUtils.getLoginUser();
        ProfGroup prof_group = profGroupService.findByOrgID(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserAndRole(user, roleService.findByName("专业团体").getId());
        auth.setStatus(AuthStatus.NOT_VERIFY);
        prof_group.setIsEnable(0);
        if (!profGroupService.saveOrUpdate(prof_group, auth)) {
            renderJson(RestResult.buildError("修改认证状态"));
            throw new BusinessException("修改认证状态");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 跳转认证审查团体信息填写页面
     * flag = 0时未在认证状态，页面不禁用
     * flag = 1时,页面内容禁用不可编辑
     */
    public void reviewGroup() {
        User loginUser = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(loginUser.getUserID());
        ReviewGroup reviewGroup = reviewGroupService.findByOrgID(organization.getId());
        if (reviewGroup == null) {
            reviewGroup = new ReviewGroup();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatus(loginUser.getId(), AuthStatus.VERIFYING) == null) {
            setAttr("organization", organization).setAttr("review_group", reviewGroup)
                    .setAttr("flag", 0).render("review_group.html");
        } else {
            setAttr("organization", organization).setAttr("review_group", reviewGroup)
                    .setAttr("flag", 1).render("review_group.html");
        }
    }

    /**
     * 提交认证审查团体数据并进入待审核状态
     */
    @Before(POST.class)
    public void reviewGroupProve() {
        ReviewGroup review_group = getBean(ReviewGroup.class, "review_group");
        review_group.setCreateTime(new Date());
        review_group.setLastAccessTime(new Date());
        review_group.setIsEnable(1);
        User loginUser = AuthUtils.getLoginUser();
        //若曾经取消认证则下次认证时获取id进行更新
        ReviewGroup name = reviewGroupService.findByName(review_group.getName());
        if (name != null) {
//            if (name.getIsEnable() == 0){
//                renderJson(RestResult.buildError("审查团体已存在"));
//                throw new BusinessException("审查团体已存在");
//            }
            review_group.setId(name.getId());
        }
        Auth auth = new Auth();
        auth.setRoleId(roleService.findByName("审查团体").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus(AuthStatus.VERIFYING);
        auth.setType("1");
        if (reviewGroupService.saveOrUpdate(review_group, auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
        } else {
            renderJson(RestResult.buildError("认证失败"));
            throw new BusinessException("认证失败");
        }
    }

    /**
     * 取消审查团体认证状态并返回编辑信息
     */
    public void reviewGroupCancel() {
        User user = AuthUtils.getLoginUser();
        ReviewGroup review_group = reviewGroupService.findByOrgID(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserAndRole(user, roleService.findByName("审查团体").getId());
        auth.setStatus(AuthStatus.NOT_VERIFY);
        review_group.setIsEnable(0);
        if (!reviewGroupService.saveOrUpdate(review_group, auth)) {
            renderJson(RestResult.buildError("修改认证状态"));
            throw new BusinessException("修改认证状态");
        }
        renderJson(RestResult.buildSuccess());
    }

}
