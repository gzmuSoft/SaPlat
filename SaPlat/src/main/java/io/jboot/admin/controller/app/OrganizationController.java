package io.jboot.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.service.entity.status.system.ProjectStatus;
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.OrganizationValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * -----------------------------
 *
 * @author LiuChuanjin
 * @version 2.0
 * -----------------------------
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

    @JbootrpcService
    private FileFormService fileFormService;

    @JbootrpcService
    private FilesService filesService;


    /**
     * 跳转注册页面
     */
    public void register() {
        render("template/register.html");
    }

    /**
     * 组织机构注册功能
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
        loginUser = userService.findById(loginUser.getId());
        Organization organization = organizationService.findById(loginUser.getUserID());
        setAttr("organization", organization).setAttr("user", loginUser).render("main.html");
    }

    /**
     * 报表文件关联
     */
    @NotNullPara({"tableName", "fieldName", "fileId"})
    public void upload() {
        User loginUser = AuthUtils.getLoginUser();
        String tableName = getPara("tableName");
        String fieldName = getPara("fieldName");
        Long fileId = getParaToLong("fileId");
        FileForm model = new FileForm();
        Files files = null;
        if ("organization".equals(tableName)) {
            model = fileFormService.findFirstByTableNameAndRecordIDAndFileName(tableName, fieldName, loginUser.getUserID());
            if (model == null) {
                model = new FileForm();
                model.setRecordID(loginUser.getUserID());
                model.setCreateTime(new Date());
                files = filesService.findById(fileId);
                if (files != null) {
                    files.setIsEnable(true);
                }
            }
            model.setStatus(false);
        } else if ("facAgency".equals(tableName)) {
            FacAgency facAgency = facAgencyService.findByOrgId(loginUser.getUserID());
            if (facAgency != null) {
                Long id = facAgency.getId();
                model = fileFormService.findFirstByTableNameAndRecordIDAndFileName(tableName, fieldName, id);
            }
            if (model == null) {
                model = new FileForm();
                model.setCreateTime(new Date());
            }
        } else if ("enterprise".equals(tableName)) {
            Enterprise enterprise = enterpriseService.findByOrgId(loginUser.getUserID());
            if (enterprise != null) {
                Long id = enterprise.getId();
                model = fileFormService.findFirstByTableNameAndRecordIDAndFileName(tableName, fieldName, id);
            }
            if (model == null) {
                model = new FileForm();
                model.setCreateTime(new Date());
            }
        } else if ("profGroup".equals(tableName)) {
            ProfGroup profGroup = profGroupService.findByOrgId(loginUser.getUserID());
            if (profGroup != null) {
                Long id = profGroup.getId();
                model = fileFormService.findFirstByTableNameAndRecordIDAndFileName(tableName, fieldName, id);
            }
            if (model == null) {
                model = new FileForm();
                model.setCreateTime(new Date());
            }
        }
        model.setTableName(tableName);
        model.setFieldName(fieldName);
        model.setFileID(fileId);

        model.setLastAccessTime(new Date());
        model.setLastUpdateUserID(loginUser.getId());
        model.setCreateUserID(loginUser.getId());
        if (model.getId() != null) {
            files = filesService.findById(model.getFileID());
            if (files != null) {
                files.setIsEnable(false);
                if (!filesService.update(files)) {
                    renderJson(RestResult.buildError("文件禁用失败"));
                    throw new BusinessException("文件禁用失败");
                }
            }
        }
        JSONObject json = new JSONObject();

        model = fileFormService.saveOrUpdateAndGet(model, files);
        if (model == null) {
            renderJson(RestResult.buildError("保存失败"));
            throw new BusinessException("保存失败");
        } else {
            json.put("fileFromID", model.getId());
        }
        renderJson(json);
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
        boolean flag = false;
        if (fileFormService.findFirstByTableNameAndRecordIDAndFileName("organization", "营业执照", loginUser.getUserID()) == null) {
            setAttr("flag", flag).render("prove.html");
            return;
        } else {
            flag = true;
            Auth auth = authService.findByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.VERIFYING, TypeStatus.ORGANIZATION);
            List<Auth> auth1 = authService.findListByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.IS_VERIFY, TypeStatus.ORGANIZATION);
            List<String> list = new ArrayList<>();
            if (auth == null) {
                auth = authService.findByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.NOT_VERIFY, TypeStatus.ORGANIZATION);
                if (auth != null) {
                    setAttr("name", roleService.findById(auth.getRoleId()).getName())
                            .setAttr("Method", roleService.findById(auth.getRoleId()).getRemark())
                            .setAttr("auth", auth)
                            .render("proveing.html");
                } else if (auth1 != null) {
                    for (int i = 0; i < auth1.size(); i++) {
                        list.add(roleService.findById(auth1.get(i).getRoleId()).getName());
                    }
                    setAttr("flag", flag).setAttr("nameList", list).render("prove.html");
                } else {
                    setAttr("flag", flag).render("prove.html");
                }
            } else {
                setAttr("name", roleService.findById(auth.getRoleId()).getName())
                        .setAttr("Method", roleService.findById(auth.getRoleId()).getRemark())
                        .setAttr("auth", auth)
                        .render("proveing.html");
            }
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
        Management model = managementService.findByOrgId(organization.getId());
        if (model == null) {
            model = new Management();
        }
        //flag = 0时未在认证状态，页面不禁用； flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.VERIFYING, TypeStatus.ORGANIZATION) == null) {
            setAttr("organization", organization).setAttr("management", model)
                    .setAttr("flag", 0).render("management.html");
        } else {
            setAttr("organization", organization).setAttr("management", model)
                    .setAttr("flag", 1).render("management.html");
        }
    }

    /**
     * 提交认证管理机构数据并进入待审核状态
     */
    @Before(POST.class)
    public void managementProve() {
        User loginUser = AuthUtils.getLoginUser();
        Management model = getBean(Management.class, "management");
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setCreateUserID(loginUser.getId());
        model.setLastUpdateUserID(loginUser.getId());
        //若曾经取消认证则下次认证时获取id进行更新
        Management name = managementService.findByName(model.getName());
        Auth auth;
        if (name != null) {
            model.setId(name.getId());
            auth = authService.findByUserAndRole(loginUser, roleService.findByName("管理机构").getId());
        } else {
            auth = new Auth();
        }
        auth.setRoleId(roleService.findByName("管理机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setName(loginUser.getName());
        auth.setStatus(AuthStatus.VERIFYING);
        if (managementService.saveOrUpdate(model, auth)) {
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
        Management model = managementService.findByOrgId(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserIdAndStatusAndType(user.getId(), ProjectStatus.VERIFIING, TypeStatus.ORGANIZATION);
        auth.setStatus(AuthStatus.CANCEL_VERIFY);
        model.setIsEnable(false);
        if (!managementService.saveOrUpdate(model, auth)) {
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
        Enterprise model = enterpriseService.findByOrgId(organization.getId());
        if (model == null) {
            model = new Enterprise();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.VERIFYING, TypeStatus.ORGANIZATION) == null) {
            setAttr("organization", organization).setAttr("enterprise", model)
                    .setAttr("flag", 0).render("enterprise.html");
        } else {
            setAttr("organization", organization).setAttr("enterprise", model)
                    .setAttr("flag", 1).render("enterprise.html");
        }
    }

    /**
     * 提交认证企业机构数据并进入待审核状态
     */
    @Before(POST.class)
    @NotNullPara("fileFromID")
    public void enterpriseProve() {
        User loginUser = AuthUtils.getLoginUser();
        Enterprise model = getBean(Enterprise.class, "enterprise");
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setCreateUserID(loginUser.getId());
        model.setLastUpdateUserID(loginUser.getId());
        //若曾经取消认证则下次认证时获取id进行更新
        Enterprise name = enterpriseService.findByName(model.getName());
        Auth auth;
        if (name != null) {
            model.setId(name.getId());
            auth = authService.findByUserAndRole(loginUser, roleService.findByName("企业机构").getId());
        } else {
            auth = new Auth();
        }
        auth.setRoleId(roleService.findByName("企业机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setName(loginUser.getName());
        auth.setStatus(AuthStatus.VERIFYING);
        if (enterpriseService.saveOrUpdate(model, auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
            FileForm fileForm = fileFormService.findById(getParaToLong("fileFromID"));
            fileForm.setRecordID(enterpriseService.findByOrgId(loginUser.getUserID()).getId());
            Files files = filesService.findById(fileForm.getFileID());
            files.setIsEnable(true);
            if (fileFormService.saveOrUpdateAndGet(fileForm, files) != null) {
                renderJson(RestResult.buildSuccess("保存成功"));
            } else {
                renderJson(RestResult.buildSuccess("保存失败"));
                throw new BusinessException("失败");
            }
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
        Enterprise model = enterpriseService.findByOrgId(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserIdAndStatusAndType(user.getId(), ProjectStatus.VERIFIING, TypeStatus.ORGANIZATION);
        auth.setStatus(AuthStatus.CANCEL_VERIFY);
        model.setIsEnable(false);
        if (!enterpriseService.saveOrUpdate(model, auth)) {
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
        FacAgency model = facAgencyService.findByOrgId(organization.getId());
        if (model == null) {
            model = new FacAgency();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.VERIFYING, TypeStatus.ORGANIZATION) == null) {
            setAttr("organization", organization).setAttr("facAgency", model)
                    .setAttr("flag", 0).render("facAgency.html");
        } else {
            setAttr("organization", organization).setAttr("facAgency", model)
                    .setAttr("flag", 1).render("facAgency.html");
        }
    }

    /**
     * 提交认证服务机构数据并进入待审核状态
     */
    @Before(POST.class)
    @NotNullPara({"fileFromID", "fileFromID1"})
    public void facAgencyProve() {
        User loginUser = AuthUtils.getLoginUser();
        FacAgency model = getBean(FacAgency.class, "facAgency");
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setCreateUserID(loginUser.getId());
        model.setLastUpdateUserID(loginUser.getId());
        //若曾经取消认证则下次认证时获取id进行更新
        FacAgency name = facAgencyService.findByName(model.getName());
        Auth auth;
        if (name != null) {
            model.setId(name.getId());
            auth = authService.findByUserAndRole(loginUser, roleService.findByName("服务机构").getId());
        } else {
            auth = new Auth();
        }
        auth.setRoleId(roleService.findByName("服务机构").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setName(loginUser.getName());
        auth.setStatus(AuthStatus.VERIFYING);
        if (facAgencyService.saveOrUpdate(model, auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
            Long recordId = loginUser.getUserID();
            FileForm fileForm = fileFormService.findById(getParaToLong("fileFromID"));
            fileForm.setRecordID(facAgencyService.findByOrgId(recordId).getId());
            Files files = filesService.findById(fileForm.getFileID());
            files.setIsEnable(true);
            if (fileFormService.saveOrUpdateAndGet(fileForm, files) != null) {
                renderJson(RestResult.buildSuccess("保存成功"));
            } else {
                renderJson(RestResult.buildSuccess("保存失败"));
                throw new BusinessException("认证失败");
            }
            fileForm = fileFormService.findById(getParaToLong("fileFromID1"));
            fileForm.setRecordID(facAgencyService.findByOrgId(recordId).getId());
            files = filesService.findById(fileForm.getFileID());
            files.setIsEnable(true);
            if (fileFormService.saveOrUpdateAndGet(fileForm, files) != null) {
                renderJson(RestResult.buildSuccess("保存成功"));
            } else {
                renderJson(RestResult.buildSuccess("保存失败"));
                throw new BusinessException("认证失败");
            }
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
        FacAgency model = facAgencyService.findByOrgId(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserIdAndStatusAndType(user.getId(), ProjectStatus.VERIFIING, TypeStatus.ORGANIZATION);
        auth.setStatus(AuthStatus.CANCEL_VERIFY);
        model.setIsEnable(false);
        if (!facAgencyService.saveOrUpdate(model, auth)) {
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
        ProfGroup model = profGroupService.findByOrgId(organization.getId());
        if (model == null) {
            model = new ProfGroup();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.VERIFYING, TypeStatus.ORGANIZATION) == null) {
            setAttr("organization", organization).setAttr("profGroup", model)
                    .setAttr("flag", 0).render("profGroup.html");
        } else {
            setAttr("organization", organization).setAttr("profGroup", model)
                    .setAttr("flag", 1).render("profGroup.html");
        }
    }

    /**
     * 提交认证审查团体数据并进入待审核状态
     */
    @Before(POST.class)
    @NotNullPara("fileFromID")
    public void profGroupProve() {
        User loginUser = AuthUtils.getLoginUser();
        ProfGroup model = getBean(ProfGroup.class, "profGroup");
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setCreateUserID(loginUser.getId());
        model.setLastUpdateUserID(loginUser.getId());
        //若曾经取消认证则下次认证时获取id进行更新
        ProfGroup name = profGroupService.findByName(model.getName());
        Auth auth;
        if (name != null) {
            model.setId(name.getId());
            auth = authService.findByUserAndRole(loginUser, roleService.findByName("专业团体").getId());
        } else {
            auth = new Auth();
        }
        auth.setRoleId(roleService.findByName("专业团体").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setType("1");
        auth.setName(loginUser.getName());
        auth.setStatus(AuthStatus.VERIFYING);
        if (profGroupService.saveOrUpdate(model, auth)) {
            renderJson(RestResult.buildSuccess("认证成功"));
            FileForm fileForm = fileFormService.findById(getParaToLong("fileFromID"));
            fileForm.setRecordID(profGroupService.findByOrgId(loginUser.getUserID()).getId());
            Files files = filesService.findById(fileForm.getFileID());
            files.setIsEnable(true);
            if (fileFormService.saveOrUpdateAndGet(fileForm, files) != null) {
                renderJson(RestResult.buildSuccess("保存成功"));
            } else {
                renderJson(RestResult.buildSuccess("保存失败"));
                throw new BusinessException("保存失败");
            }
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
        ProfGroup model = profGroupService.findByOrgId(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserIdAndStatusAndType(user.getId(), ProjectStatus.VERIFIING, TypeStatus.ORGANIZATION);
        auth.setStatus(AuthStatus.CANCEL_VERIFY);
        model.setIsEnable(false);
        if (!profGroupService.saveOrUpdate(model, auth)) {
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
        ReviewGroup model = reviewGroupService.findByOrgId(organization.getId());
        if (model == null) {
            model = new ReviewGroup();
        }
        //flag = 0时未在认证状态，页面不禁用 flag = 1时页面内容禁用不可编辑
        if (authService.findByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.VERIFYING, TypeStatus.ORGANIZATION) == null) {
            setAttr("organization", organization).setAttr("reviewGroup", model)
                    .setAttr("flag", 0).render("reviewGroup.html");
        } else {
            setAttr("organization", organization).setAttr("reviewGroup", model)
                    .setAttr("flag", 1).render("reviewGroup.html");
        }
    }

    /**
     * 提交认证审查团体数据并进入待审核状态
     */
    @Before(POST.class)
    public void reviewGroupProve() {
        User loginUser = AuthUtils.getLoginUser();
        ReviewGroup model = getBean(ReviewGroup.class, "reviewGroup");
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setCreateUserID(loginUser.getId());
        model.setLastUpdateUserID(loginUser.getId());
        //若曾经取消认证则下次认证时获取id进行更新
        ReviewGroup name = reviewGroupService.findByName(model.getName());
        Auth auth;
        if (name != null) {
            model.setId(name.getId());
            auth = authService.findByUserAndRole(loginUser, roleService.findByName("审查团体").getId());
        } else {
            auth = new Auth();
        }
        auth.setRoleId(roleService.findByName("审查团体").getId());
        auth.setUserId(loginUser.getId());
        auth.setLastUpdTime(new Date());
        auth.setName(loginUser.getName());
        auth.setStatus(AuthStatus.VERIFYING);
        auth.setType("1");
        if (reviewGroupService.saveOrUpdate(model, auth)) {
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
        ReviewGroup model = reviewGroupService.findByOrgId(organizationService.findById(user.getUserID()).getId());
        Auth auth = authService.findByUserIdAndStatusAndType(user.getId(), ProjectStatus.VERIFIING, TypeStatus.ORGANIZATION);
        auth.setStatus(AuthStatus.CANCEL_VERIFY);
        model.setIsEnable(false);
        if (!reviewGroupService.saveOrUpdate(model, auth)) {
            renderJson(RestResult.buildError("修改认证状态"));
            throw new BusinessException("修改认证状态");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 立项资格申请
     */
    public void projectGet() {
        User user = AuthUtils.getLoginUser();
        List<Role> roleList = roleService.findByNames("服务机构立项", "管理机构立项", "企业机构立项", "审查团体立项", "专业团体立项");
        List<Auth> authList = authService.findByUserAndType(user, TypeStatus.PROJECT_VERIFY);
        if (authList == null) {
            authList = Collections.synchronizedList(new ArrayList<Auth>());
        }
        List<Auth> noVerify = Collections.synchronizedList(new ArrayList<Auth>());
        List<Auth> verify = Collections.synchronizedList(new ArrayList<Auth>());
        List<Auth> verifying = Collections.synchronizedList(new ArrayList<Auth>());
        authList.forEach(auth -> {
            for (int i = 0; i < roleList.size(); i++) {
                Role role = roleList.get(i);
                if (role.getId().equals(auth.getRoleId())) {
                    auth.setRemark(role.getName());
                    roleList.remove(i);
                }
            }
            switch (auth.getStatus()) {
                case AuthStatus.IS_VERIFY:
                    verify.add(auth);
                    break;
                case AuthStatus.NOT_VERIFY:
                    noVerify.add(auth);
                    break;
                case AuthStatus.VERIFYING:
                    verifying.add(auth);
                    break;
                default:
                    break;
            }
        });

        setAttr("noVerify", noVerify)
                .setAttr("verify", verify)
                .setAttr("verifying", verifying)
                .setAttr("roleList", roleList)
                .render("projectGet.html");
    }


    @Before(POST.class)
    public void postProjectGet() {
        Long id = getParaToLong("id");
        User user = AuthUtils.getLoginUser();
        Role role = roleService.findById(id);
        UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), role.getParentID());
        if (userRole == null) {
            renderJson(RestResult.buildError("亲，请先去认证成为当前组织再来申请哦~~~"));
            throw new BusinessException("亲，请先去认证成为当前组织再来申请哦~~~");
        }
        Auth auth = authService.findByUserAndRole(user, id);
        if (auth == null) {
            auth = new Auth();
            auth.setName(user.getName());
            auth.setType(TypeStatus.PROJECT_VERIFY);
            auth.setRoleId(id);
            auth.setUserId(user.getId());
        }
        auth.setLastUpdTime(new Date());
        auth.setStatus(AuthStatus.VERIFYING);
        if (!authService.saveOrUpdate(auth)) {
            renderJson(RestResult.buildError("申请失败"));
            throw new BusinessException("申请失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
