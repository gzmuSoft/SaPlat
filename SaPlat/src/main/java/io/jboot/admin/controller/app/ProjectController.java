package io.jboot.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.*;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.system.ProjectValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.*;

/**
 * -----------------------------
 *
 * @author LiuChuanjin
 * @version 2.0
 * -----------------------------
 * @date 12:04 2018/7/15
 */

@RequestMapping("/app/project")
public class ProjectController extends BaseController {

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private AuthService authService;

    @JbootrpcService
    private ProjectAssTypeService projectAssTypeService;

    @JbootrpcService
    private ProjectStepService projectStepService;

    @JbootrpcService
    private AuthProjectService authProjectService;

    @JbootrpcService
    private FacAgencyService facAgencyService;

    @JbootrpcService
    private ProjectUndertakeService projectUndertakeService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private LeaderGroupService leaderGroupService;

    @JbootrpcService
    private UserRoleService userRoleService;

    @JbootrpcService
    private OrganizationService organizationService;

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private FilesService filesService;

    @JbootrpcService
    private EvaSchemeService evaSchemeService;

    @JbootrpcService
    private ManagementService managementService;

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @JbootrpcService
    private ExpertGroupService expertGroupService;

    @JbootrpcService
    private ApplyInviteService applyInviteService;

    /**
     * 项目立项基本资料初始化至信息管理界面
     */
    public void index() {
        User loginUser = AuthUtils.getLoginUser();
        List<Auth> authList = authService.findListByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.IS_VERIFY, TypeStatus.PROJECT_VERIFY);
        ProjectAssType paTypeModel = new ProjectAssType();
        paTypeModel.setIsEnable(true);
        List<ProjectAssType> PaTypeList = projectAssTypeService.findAll(paTypeModel);
        ProjectStep psModel = new ProjectStep();
        psModel.setIsEnable(true);
        List<ProjectStep> projectStepList = projectStepService.findAll(psModel);
        List<String> roleNameList = new ArrayList<>();
        for (int i = 0; i < authList.size(); i++) {
            roleNameList.add(roleService.findById(authList.get(i).getRoleId()).getName());
        }
        if (roleNameList.size() != 0) {
            setAttr("roleNameList", roleNameList).setAttr("flag", true)
                    .setAttr("PaTypeNameList", PaTypeList)
                    .setAttr("projectStepNameList", projectStepList)
                    .render("projectInformation.html");
        } else {
            setAttr("flag", false).render("projectInformation.html");
        }
    }

    /**
     * 立项文件上传页面
     */
    @NotNullPara({"id", "projectId"})
    public void fileUploading() {
        Long id = getParaToLong("id");
        ProjectFileType model = projectFileTypeService.findById(id);
        setAttr("projectId", getParaToLong("projectId")).setAttr("model", model).render("fileUploading.html");
    }

    /**
     * project资料上传
     */
    @Before({GET.class, ProjectValidator.class})
    public void projectUploading() {
        User loginUser = AuthUtils.getLoginUser();
        Project project = getBean(Project.class, "project");
        project.setUserId(loginUser.getId());
        project.setCreateTime(new Date());
        project.setLastAccessTime(new Date());
        project.setStatus(ProjectStatus.BUILDING);
        project.setCreateUserID(loginUser.getId());
        project.setLastUpdateUserID(loginUser.getId());
        project.setIsEnable(true);
        int saveOrUpdate = getParaToInt("saveOrUpdate");
        if (saveOrUpdate == 1) {
            Project model = projectService.saveProject(project);
            Long projectId = -1L;
            if (model != null) {
                projectId = model.getId();
            }
            JSONObject json = new JSONObject();
            json.put("projectId", projectId);
            if (projectId != -1L) {
                renderJson(json);
            } else {
                renderJson(RestResult.buildError("项目资料上传失败"));
                throw new BusinessException("项目资料上传失败");
            }
        } else if (saveOrUpdate == 0) {
            JSONObject jsonData = new JSONObject();
            if (getParaToBoolean("judgeFile")) {
                AuthProject authProject = new AuthProject();
                authProject.setUserId(loginUser.getId());
                authProject.setRoleId(roleService.findByName(projectService.findById(getParaToLong("projectId")).getRoleName()).getId());
                authProject.setProjectId(getParaToLong("projectId"));
                authProject.setLastUpdTime(new Date());
                authProject.setType(ProjectTypeStatus.INFORMATION_REVIEW);
                authProject.setName(loginUser.getName());
                authProject.setLastUpdUser(loginUser.getName());
                if ("自评".equals(project.getAssessmentMode())) {
                    authProject.setStatus(ProjectStatus.REVIEW);
                    project.setStatus(ProjectStatus.REVIEW);

                    ProjectUndertake projectUndertake = new ProjectUndertake();
                    projectUndertake.setName(projectService.findById(getParaToLong("projectId")).getName());
                    projectUndertake.setCreateUserID(loginUser.getId());
                    projectUndertake.setProjectID(getParaToLong("projectId"));
                    projectUndertake.setFacAgencyID(loginUser.getId());
                    projectUndertake.setApplyOrInvite(true);
                    projectUndertake.setStatus(2);
                    projectUndertake.setCreateTime(new Date());
                    projectUndertake.setDeadTime(new Date());
                    projectUndertake.setLastAccessTime(new Date());
                    projectUndertake.setLastUpdateUserID(loginUser.getId());
                    projectUndertake.setIsEnable(true);
                    if (!projectUndertakeService.saveOrUpdate(projectUndertake)) {
                        throw new BusinessException("保存失败");
                    }

                    jsonData.put("flag", 0);
                } else if ("委评".equals(project.getAssessmentMode())) {
                    authProject.setStatus(ProjectStatus.VERIFIING);
                    project.setStatus(ProjectStatus.VERIFIING);
                    jsonData.put("flag", 1);
                }
                if (authProjectService.saveOrUpdate(authProject)) {
                    renderJson(RestResult.buildSuccess("项目状态表上传成功"));
                } else {
                    renderJson(RestResult.buildError("项目状态表上传失败"));
                    throw new BusinessException("项目状态表上传失败");
                }
            } else {
                jsonData.put("flag", 2);
            }
            if (getParaToLong("projectId") != -1) {
                project.setId(getParaToLong("projectId"));
            }
            if (projectService.update(project)) {
                renderJson(RestResult.buildSuccess("项目资料更新成功"));
                renderJson(jsonData);
            } else {
                renderJson(RestResult.buildError("项目资料更新失败"));
                throw new BusinessException("项目资料更新失败");
            }
        }
    }

    /**
     * leaderGroup资料上传
     */
    @Before({GET.class, ProjectValidator.class})
    public void leaderGroupUploading() {
        User loginUser = AuthUtils.getLoginUser();
        LeaderGroup leaderGroup = getBean(LeaderGroup.class, "leaderGroup");
        leaderGroup.setCreateTime(new Date());
        leaderGroup.setLastAccessTime(new Date());
        leaderGroup.setCreateUserID(loginUser.getId());
        leaderGroup.setLastUpdateUserID(loginUser.getId());
        leaderGroup.setProjectID(getParaToLong("projectId"));
        int saveOrUpdate = getParaToInt("saveOrUpdate");
        if (saveOrUpdate == 1) {
            if (leaderGroupService.save(leaderGroup)) {
                renderJson(RestResult.buildError("稳评小组资料上传成功"));
            } else {
                renderJson(RestResult.buildError("稳评小组资料上传失败"));
                throw new BusinessException("稳评小组资料上传失败");
            }
        } else if (saveOrUpdate == 0) {
            LeaderGroup leaderGroup1 = leaderGroupService.findByProjectID(getParaToLong("projectId"));
            leaderGroup.setId(leaderGroup1.getId());
            if (leaderGroupService.update(leaderGroup)) {
                renderJson(RestResult.buildSuccess("稳评小组资料更新成功"));
            } else {
                renderJson(RestResult.buildError("稳评小组资料更新失败"));
                throw new BusinessException("稳评小组资料更新失败");
            }
        }
    }

    /**
     * 查看立项信息
     */
    @NotNullPara({"id"})
    public void see() {
//        User user = AuthUtils.getLoginUser();
//        Long id = getParaToLong("id");
//        Project model = projectService.findById(id);
//        model.setTypeName(projectAssTypeService.findById(model.getPaTypeID()).getName());
//        Organization organization=organizationService.findById(user.getUserID());
//        setAttr("organization",organization);
//        setAttr("model", model).render("update.html");

        Long id = getParaToLong("id");
        AuthProject apModel = authProjectService.findByProjectId(id);//获取项目的立项审核信息
        Project pModel = projectService.findById(id); //获取项目信息
        pModel.setTypeName(projectAssTypeService.findById(pModel.getPaTypeID()).getName());
        Organization organization = organizationService.findById(userService.findById(pModel.getUserId()).getUserID()); //获取组织信息
        String strRoleName = roleService.findById(apModel.getRoleId()).getName();
        setAttr("organization",organization)
                .setAttr("model", pModel)
                .setAttr("roleName", strRoleName)
                .render("update.html");
    }

    /**
     * 取消立项
     */
    @NotNullPara({"id"})
    public void cancel() {
        Project model = projectService.findById(getParaToLong("id"));
        AuthProject authProject = authProjectService.findByProjectId(model.getId());
        authProject.setStatus(AuthStatus.CANCEL_VERIFY);
        model.setStatus(ProjectStatus.CANCEL_VERIFY);
        if (projectService.saveOrUpdate(model, authProject)) {
            renderJson(RestResult.buildSuccess("取消本次立项资料成功"));
        } else {
            renderJson(RestResult.buildError("取消立项失败"));
            throw new BusinessException("取消立项失败");
        }
    }


    /**
     * 通往项目管理界面-立项中
     */
    public void toBuildProject() {
        render("buildProject.html");
    }

    /**
     * 立项中-表格渲染
     */
    public void buildProject() {
        User loginUser = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setUserId(loginUser.getId());
        project.setStatus(ProjectStatus.BUILDING);
        project.setIsEnable(true);
        Page<Project> page = projectService.findPage(project, pageNumber, pageSize);
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 立项中-资料补充
     */
    @NotNullPara({"id"})
    public void projectMessage() {
        Long id = getParaToLong("id");
        User loginUser = AuthUtils.getLoginUser();
        Project project = projectService.findById(id);
        LeaderGroup leaderGroup = leaderGroupService.findByProjectID(id);
        if (leaderGroup == null) {
            leaderGroup = new LeaderGroup();
        }
        List<Auth> authList = authService.findListByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.IS_VERIFY, TypeStatus.PROJECT_VERIFY);
        List<ProjectAssType> PaTypeList = projectAssTypeService.findAll();
        List<ProjectStep> projectStepList = projectStepService.findAll();
        List<String> roleNameList = new ArrayList<>();
        for (int i = 0; i < authList.size(); i++) {
            roleNameList.add(roleService.findById(authList.get(i).getRoleId()).getName());
        }
        String paTypeName = projectAssTypeService.findById(project.getPaTypeID()).getName();
        String pStepName = projectStepService.findById(project.getPStepID()).getName();
        int i = 0;
        for (ProjectAssType p : PaTypeList) {
            if (p.getName().equals(paTypeName)) {
                PaTypeList.remove(i);
                break;
            }
            i++;
        }
        i = 0;
        for (ProjectStep p : projectStepList) {
            if (p.getName().equals(pStepName)) {
                projectStepList.remove(i);
                break;
            }
            i++;
        }
        i = 0;
        for (String p : roleNameList) {
            if (p.equals(project.getRoleName())) {
                roleNameList.remove(i);
                break;
            }
            i++;
        }
        setAttr("paTypeName", paTypeName).setAttr("pStepName", pStepName)
                .setAttr("paTypeId", project.getPaTypeID()).setAttr("pStepId", project.getPStepID())
                .setAttr("projectID", id).setAttr("roleNameList", roleNameList)
                .setAttr("PaTypeNameList", PaTypeList).setAttr("projectStepNameList", projectStepList)
                .setAttr("project", project).setAttr("leaderGroup", leaderGroup)
                .render("projectAssessmentInfor.html");

    }

    /**
     * 立项中-文件列表表格渲染
     */
    @NotNullPara({"id"})
    public void fileTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long id = getParaToLong("id");
        ProjectAssType projectAssType = projectAssTypeService.findById(getParaToLong("paTypeID"));
        if (projectAssType != null) {
            ProjectFileType parentProjectFileType = projectFileTypeService.findByName(projectAssType.getName());
            ProjectFileType childProjectFileType = new ProjectFileType();
            childProjectFileType.setParentID(parentProjectFileType.getId());
            Page<ProjectFileType> page = projectFileTypeService.findPage(childProjectFileType, pageNumber, pageSize);
            for (int i = 0; i < page.getList().size(); i++) {
                FileProject fileProject = fileProjectService.findByProjectIDAndFileTypeID(id, page.getList().get(i).getId());
                if (fileProject != null) {
                    page.getList().get(i).setFileID(fileProject.getFileID());
                    page.getList().get(i).setIsUpLoad(true);
                } else {
                    page.getList().get(i).setIsUpLoad(false);
                }
            }
            renderJson(new DataTable<ProjectFileType>(page));
        } else {

        }


    }


    /**
     * 立项中-保存
     */
    @NotNullPara({"id", "assessmentMode"})
    public void saveProjectMessage() {
        Long id = getParaToLong("id");
        Project model = getBean(Project.class, "project");
        model.setId(id);
        model.setStatus(ProjectStatus.BUILDING);
        model.setAssessmentMode(getPara("assessmentMode"));
        LeaderGroup leaderGroup = getBean(LeaderGroup.class, "leaderGroup");
        if (leaderGroupService.findByProjectID(id) != null) {
            leaderGroup.setId(leaderGroupService.findByProjectID(id).getId());
        }
        if (!projectService.saveOrUpdate(model, leaderGroup)) {
            renderJson(RestResult.buildError("保存失败"));
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess("保存成功"));
    }

    /**
     * 立项中-发起审核
     */
    @NotNullPara({"id"})
    public void sendAssessment() {
        Long id = getParaToLong("id");
        User user = AuthUtils.getLoginUser();
        Project project = projectService.findById(id);
        ProjectAssType projectAssType = projectAssTypeService.findById(project.getPaTypeID());
        LeaderGroup leaderGroup = leaderGroupService.findByProjectID(id);
        List<FileProject> fileProjects = fileProjectService.findAllByProjectID(id);
        ProjectFileType projectFileType = projectFileTypeService.findByName(projectAssType.getName());
        List<ProjectFileType> projectFileTypes = projectFileTypeService.findListByParentId(projectFileType.getId());
        JSONObject json = new JSONObject();
        ProjectUndertake projectUndertake = null;
        if (project != null && leaderGroup != null && projectFileTypes.size() == fileProjects.size()) {
            AuthProject authProject = new AuthProject();
            authProject.setProjectId(id);
            authProject.setRoleId(roleService.findByName(project.getRoleName()).getId());
            authProject.setUserId(user.getId());
            authProject.setType(ProjectTypeStatus.INFORMATION_REVIEW);
            authProject.setName(userService.findById(user.getId()).getName());
            if ("委评".equals(project.getAssessmentMode())) {
                authProject.setStatus(ProjectStatus.VERIFIING);
                project.setStatus(ProjectStatus.VERIFIING);
            } else if ("自评".equals(project.getAssessmentMode())) {
                authProject.setStatus(ProjectStatus.REVIEW);
                project.setStatus(ProjectStatus.REVIEW);
                project.setIsEnable(true);

                projectUndertake = new ProjectUndertake();
                projectUndertake.setName(projectService.findById(getParaToLong("id")).getName());
                projectUndertake.setCreateUserID(user.getId());
                projectUndertake.setProjectID(getParaToLong("id"));
                projectUndertake.setFacAgencyID(user.getId());
                projectUndertake.setApplyOrInvite(true);
                projectUndertake.setStatus(2);
                projectUndertake.setCreateTime(new Date());
                projectUndertake.setDeadTime(new Date());
                projectUndertake.setLastAccessTime(new Date());
                projectUndertake.setLastUpdateUserID(user.getId());
                projectUndertake.setIsEnable(true);
            } else {
                json.put("status", false);
            }
            if (!projectService.saveOrUpdate(project, authProject, projectUndertake)) {
                renderJson(RestResult.buildError("保存失败"));
                throw new BusinessException("保存失败");
            } else {
                json.put("status", true);
            }
        } else {
            json.put("status", false);
        }
        renderJson(json);
    }

    /**
     * 项目文件关联
     */
    @NotNullPara({"fileId", "projectId", "fileTypeId"})
    public void upFile() {
        User user = AuthUtils.getLoginUser();
        FileProject model = fileProjectService.findByProjectIDAndFileTypeID(getParaToLong("projectId"), getParaToLong("fileTypeId"));
        if (model == null) {
            model = new FileProject();
            model.setProjectID(getParaToLong("projectId"));
            model.setFileTypeID(getParaToLong("fileTypeId"));
            model.setCreateTime(new Date());
            model.setCreateUserID(user.getId());

        } else {
            Files files = filesService.findById(model.getFileID());
            if (files != null) {
                files.setIsEnable(false);
                if (!filesService.update(files)) {
                    renderJson(RestResult.buildError("文件禁用失败"));
                    throw new BusinessException("文件禁用失败");
                }
            }
        }
        model.setFileID(getParaToLong("fileId"));
        model.setLastAccessTime(new Date());
        model.setLastUpdateUserID(user.getId());

        Long q_fileTypeId = projectFileTypeService.findByName("风险跟踪管理登记表").getId();
        if (model.getFileTypeID().equals(q_fileTypeId)) {
            model.setId(null);
            model.setCreateTime(new Date());
            if (!fileProjectService.save(model)) {
                renderJson(RestResult.buildError("上传失败"));
                throw new BusinessException("上传失败");
            } else {
                Files files = filesService.findById(getParaToLong("fileId"));
                files.setIsEnable(true);
                if (!filesService.update(files)) {
                    renderJson(RestResult.buildError("文件启用失败"));
                    throw new BusinessException("文件启用失败");
                }
            }
        } else {
            if (!fileProjectService.saveOrUpdate(model)) {
                renderJson(RestResult.buildError("上传失败"));
                throw new BusinessException("上传失败");
            } else {
                Files files = filesService.findById(getParaToLong("fileId"));
                files.setIsEnable(true);
                if (!filesService.update(files)) {
                    renderJson(RestResult.buildError("文件启用失败"));
                    throw new BusinessException("文件启用失败");
                }
            }
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 判断当前项目文件是否上传完毕
     */
    public void judgeFile() {
        List<FileProject> fileProjects = fileProjectService.findAllByProjectID(getParaToLong("projectId"));
        ProjectAssType projectAssType = projectAssTypeService.findById(getParaToLong("paTypeID"));
        ProjectFileType parentProjectFileType = projectFileTypeService.findByName(projectAssType.getName());
        List<ProjectFileType> childProjectFileType = projectFileTypeService.findListByParentId(parentProjectFileType.getId());
        JSONObject json = new JSONObject();
        if (childProjectFileType == null || childProjectFileType.size() == fileProjects.size()) {
            json.put("judgeFile", true);
            renderJson(json);
        } else {
            json.put("judgeFile", false);
            renderJson(json);
        }
    }

    /**
     * 通往项目管理界面-审核中
     */
    public void toVerfiing() {
        render("verfiing.html");
    }

    /**
     * 项目管理界面-审核中-表格渲染
     */
    public void verfiing() {
        User loginUser = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setUserId(loginUser.getId());
        project.setStatus(ProjectStatus.VERIFIING);
        project.setIsEnable(true);
        Page<Project> page = projectService.findPage(project, pageNumber, pageSize);
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 通往项目管理界面-已审核-成功
     */
    public void toVerfedSuccess() {
        render("verfedSuccess.html");
    }

    /**
     * 项目管理界面-已审核-成功-表格渲染
     */
    public void verfedSuccess() {
        User loginUser = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setUserId(loginUser.getId());
        project.setStatus(ProjectStatus.IS_VERIFY);
        project.setIsEnable(true);
        Page<Project> page = projectService.findPage(project, pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            page.getList().get(i).setIsReceive(projectUndertakeService.findIsReceive(page.getList().get(i).getId()));
        }
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 通往项目管理界面-已审核-成功
     */
    public void toVerfedDefeat() {
        render("verfedDefeat.html");
    }

    /**
     * 项目管理界面-已审核-失败-表格渲染
     */
    public void verfedDefeat() {
        User loginUser = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setUserId(loginUser.getId());
        project.setStatus(ProjectStatus.NOT_VERIFY);
        project.setIsEnable(true);
        Page<Project> page = projectService.findPage(project, pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            page.getList().get(i).setReply(authProjectService.findByProjectId(page.getList().get(i).getId()).getReply());
        }
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 通往项目管理界面-评估中
     */
    public void evaluation() {
        render("evaluation.html");
    }

    /**
     * 项目管理界面-评估中-表格渲染
     */
    public void evaluationTable() {
        User loginUser = AuthUtils.getLoginUser();
        List<UserRole> roles = userRoleService.findListByUserId(loginUser.getId());
        Role role = roleService.findByName("服务机构");
        boolean flag = false;
        for (UserRole userRole : roles) {
            if (userRole.getRoleID().equals(role.getId())) {
                flag = true;
                break;
            }
        }

        // 初始化评估列表
        List<Project> undertake = Collections.synchronizedList(new ArrayList<>());
        // 如果是服务机构,查找委评的项目
        if (flag) {
            // 查找服务机构
            Organization organization = organizationService.findById(loginUser.getUserID());
            FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());

            // 当他为申请的时候,以当前用户登录的 id 作为创建者 id 查找 申请 成功的项目
            List<ProjectUndertake> projectUndertakeList1 = projectUndertakeService.findByCreateUserIDAndStatusAndAOI(loginUser.getId(), ProjectUndertakeStatus.ACCEPT,false);
            undertake = projectService.findListByProjectUndertakeListAndStatus(projectUndertakeList1, ProjectStatus.REVIEW);
            if (undertake == null) {
                undertake = Collections.synchronizedList(new ArrayList<>());
            }

            // 当他为邀请的时候,以当前用户的服务机构 id 作为服务机构 id 查找 邀请 成功的项目
            List<ProjectUndertake> projectUndertakeList2 = projectUndertakeService.findListByFacAgencyIdAndStatusAndAOI(facAgency.getId(), ProjectUndertakeStatus.ACCEPT,true);
            List<Project> undertakeTmp = projectService.findListByProjectUndertakeListAndStatus(projectUndertakeList2, ProjectStatus.REVIEW);
            if (undertakeTmp != null && undertakeTmp.size() > 0){
                undertake.addAll(undertakeTmp);
            }
        }
        // 排除是空的类
        undertake.removeAll(Collections.singleton(null));
        // 不论是不是服务机构，都要查找自评的项目。
        List<Project> projects = projectService.findListByColumns(new String[]{"userId", "status", "isEnable"},
                new String[]{loginUser.getId().toString(), ProjectStatus.REVIEW, "1"});
        // 防止查出的是 null 的情况
        if (projects == null) {
            projects = Collections.synchronizedList(new ArrayList<>());
        }
        projects.removeAll(Collections.singleton(null));
        if (projects.size() < 1 && undertake.size() > 0) {
            // 当委评的不为空，自评的为空
            renderJson(RestResult.buildSuccess(undertake));
        } else if (projects.size() > 0 && undertake.size() < 1) {
            // 当委评的为空，自评的不为空
            renderJson(RestResult.buildSuccess(projects));
        } else if (projects.size() > 0 && undertake.size() > 0) {
            // 当委评的不为空，自评的不为空
            if (!projects.addAll(undertake)) {
                // 合并失败
                throw new BusinessException("数据加载失败。。。");
            }
            renderJson(RestResult.buildSuccess(projects));
        } else {
            // 当两个都为空
            renderJson(RestResult.buildSuccess(projects));
        }
    }

    /**
     * 通往项目管理界面-已评估（待审查）
     */
    public void review() {
        render("review.html");

    }

    /**
     * 项目管理界面-待审查-表格渲染
     */
    public void reviewTable() {
        User loginUser = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        ProjectUndertake projectUndertake = new ProjectUndertake();
        Organization organization = organizationService.findById(loginUser.getUserID());
        if(organization != null) {
            FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
            if (facAgency != null) {// && facAgency.getIsEnable()
                projectUndertake.setFacAgencyID(facAgency.getId());
                projectUndertake.setCreateUserID(loginUser.getId());
            }
        }else {
            projectUndertake.setCreateUserID(loginUser.getId());
        }
        Page<ProjectUndertake> projectUndertakePage = projectUndertakeService.findPageBySql(projectUndertake, pageNumber, pageSize);
        List<Project> pageList = Collections.synchronizedList(new ArrayList<>());
        List<ProjectUndertake> list = projectUndertakePage.getList();
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<>());
        }
        for (ProjectUndertake p : list) {
            Project project = projectService.findById(p.getProjectID());
            if (project != null && project.getStatus().equals(ProjectStatus.REVIEWED)) {
                pageList.add(project);
            }
        }
        Page<Project> page = new Page<>(pageList, pageNumber, pageSize, projectUndertakePage.getTotalPage(), projectUndertakePage.getTotalRow());
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 通往项目管理界面-审查完成
     */
    public void toReviewed() {
        render("reviewed.html");
    }

    /**
     * 项目管理界面-审查完成-表格渲染
     */
    public void reviewed() {
        User loginUser = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
//        Project project = new Project();
//        project.setStatus(ProjectStatus.REVIEWED);
//        project.setUserId(loginUser.getId());
//        List<Project> pageList = projectService.findPage(project, pageNumber, pageSize).getList();
//        for (Project p : pageList) {
//            ApplyInvite applyInvite = applyInviteService.findByProjectID(p.getId());
//            if (applyInvite != null && applyInvite.getDeadTime().before(new Date())) {
//                float allNum, noPassNum, rate;
//                applyInvite = new ApplyInvite();
//                applyInvite.setModule(1);
//                applyInvite.setProjectID(p.getId());
//                applyInvite.setCreateUserID(loginUser.getId());
//                applyInvite.setIsEnable(true);
//                List<ApplyInvite> list = applyInviteService.findList(applyInvite);
//                allNum = list.size();
//                applyInvite.setStatus(ApplyInviteStatus.NOPASS);
//                list = applyInviteService.findList(applyInvite);
//                noPassNum = list.size();
//                rate = 1 - (noPassNum / allNum);
//                if (rate > 0.8) {
//                    p.setStatus(ProjectStatus.CHECKED);
//                } else {
//                    p.setStatus(ProjectStatus.REVIEW);
//                }
//                if (allNum <= 3) {
//                    p.setStatus(ProjectStatus.REVIEW);
//                }
//                if (!projectService.update(p)) {
//                    throw new BusinessException("更新失败！");
//                }
//            }
//        }

        ProjectUndertake projectUndertake = new ProjectUndertake();

        FacAgency facAgency = facAgencyService.findByOrgId(loginUser.getUserID());//找到组织机构对应的服务机构信息
        if (facAgency != null) {
            projectUndertake.setFacAgencyID(facAgency.getId());
        }
        /*else {
            projectUndertake.setFacAgencyID(Long.parseLong("0"));
        }*/
        projectUndertake.setCreateUserID(loginUser.getId());
        projectUndertake.setStatus(Integer.valueOf(ProjectStatus.CHECKED));
        Page<Project> page = projectService.findPageBySql(projectUndertake, pageNumber, pageSize);
        renderJson(new DataTable<Project>(page));
    }
    @NotNullPara("id")
    public void finishUpload() {
        Project project = projectService.findById(getParaToLong("id"));
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        ProjectFileType projectFileType = projectFileTypeService.findByName("终审报告");
        FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(projectFileType.getId(), project.getId());
        if (fileProject == null) {
            fileProject = new FileProject();
            fileProject.setName(projectFileType.getName());
            fileProject.setProjectID(project.getId());
            fileProject.setFileTypeID(projectFileType.getId());
            fileProject.setIsEnable(false);
            fileProject.setCreateUserID(AuthUtils.getLoginUser().getId());
            fileProject = fileProjectService.saveAndGet(fileProject);
        }
        setAttr("fileProject", fileProject);
        render("finishUpload.html");
    }

    public void finishUploadSave() {
        FileProject fileProject = getBean(FileProject.class, "fileProject");
        Project project = projectService.findById(fileProject.getProjectID());
        fileProject.setIsEnable(false);
        FileProject model = fileProjectService.findByFileTypeIdAndProjectId(fileProject.getFileTypeID(), fileProject.getProjectID());
        model.setFileID(fileProject.getFileID());
        if (!fileProjectService.updateFileProjectAndFiles(model)) {
            throw new BusinessException("保存失败,请重试");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"fileProjectID"})
    public void finishUploadSub() {
        FileProject fileProject = fileProjectService.findById(getParaToLong("fileProjectID"));
        Project project = projectService.findById(fileProject.getProjectID());
        fileProject.setIsEnable(true);
        project.setCreateTime(new Date());
        if (!fileProjectService.updateFileProjectAndProject(fileProject, project)) {
            throw new BusinessException("提交失败");
        }
        renderJson(RestResult.buildSuccess());

    }

    @NotNullPara("id")
    public void finishView(){
        Project project = projectService.findById(getParaToLong("id"));
        ProjectFileType projectFileType = projectFileTypeService.findByName("终审报告");
        FileProject fileProject=fileProjectService.findByFileTypeIdAndProjectId(projectFileType.getId(),project.getId());
        setAttr("fileProject",fileProject);
        if(fileProject!=null) {
            setAttr("fileID", fileProject.getFileID());
        }
        render("finishView.html");

    }


    /**
     * 项目公开-填写日期
     */
    @NotNullPara({"id"})
    public void isPublicMessage() {
        Long id = getParaToLong("id");
        Project model = projectService.findById(id);
        setAttr("model", model).render("public.html");
    }

    /**
     * 项目公开
     */
    @NotNullPara({"id"})
    public void isPublic() {
        Long id = getParaToLong("id");
        Project model = projectService.findById(id);
        if (model != null) {
            model.setPublicTime(getParaToDate("publicTime"));
            model.setEndPublicTime(getParaToDate("endTime"));
            model.setIsPublic(true);
        }
        if (projectService.update(model)) {
            renderJson(RestResult.buildSuccess("项目公开成功"));
        } else {
            renderJson(RestResult.buildError("项目公开失败"));
            throw new BusinessException("项目公开失败");
        }
    }

    /**
     * 邀请介入
     */
    @NotNullPara({"id", "projectId"})
    public void invite() {
        User user = AuthUtils.getLoginUser();
        Notification notification = new Notification();
        notification.setName("项目邀请通知");
        notification.setSource("/app/project/invite");
        notification.setContent("您好, \"" + user.getName() + " \"邀请您介入项目 《" + projectService.findById(getParaToLong("projectId")).getName() + "》的评估，请及时处理！");
        notification.setReceiverID(userService.findByUserIdAndUserSource(facAgencyService.findById(getParaToLong("id")).getOrgID(), 1L).getId().intValue());
        notification.setCreateUserID(user.getId());
        notification.setCreateTime(new Date());
        notification.setLastUpdateUserID(userService.findByUserIdAndUserSource(facAgencyService.findById(getParaToLong("id")).getOrgID(), 1L).getId());
        notification.setLastAccessTime(new Date());
        notification.setIsEnable(true);
        notification.setStatus(0);

        ProjectUndertake projectUndertake = new ProjectUndertake();
        projectUndertake.setName(projectService.findById(getParaToLong("projectId")).getName());
        projectUndertake.setCreateUserID(user.getId());
        projectUndertake.setProjectID(getParaToLong("projectId"));
        projectUndertake.setFacAgencyID(getParaToLong("id"));
        projectUndertake.setApplyOrInvite(true);
        projectUndertake.setStatus(0);
        projectUndertake.setCreateTime(new Date());
        projectUndertake.setDeadTime(projectService.findById(getParaToLong("projectId")).getEndPublicTime());
        projectUndertake.setLastAccessTime(new Date());
        projectUndertake.setLastUpdateUserID(user.getId());
        projectUndertake.setIsEnable(true);
        if (!projectUndertakeService.saveOrUpdateAndSend(projectUndertake, notification)) {
            throw new BusinessException("邀请失败");
        }
    }

    /**
     * 通往邀请页面
     */
    public void projectAppleSOption() {
        render("projectApplyView.html");
    }

    /**
     * 选择邀请的机构
     * 参数type
     * type=0 邀请评估
     * type=1 邀请审查
     */
    @NotNullPara({"id", "type"})
    public void inviteChoose() {
        Long id = getParaToLong("id");
        Long type = getParaToLong("type");
        if (type == 0) {
            setAttr("id", id).render("invite.html");
        } else if (type == 1) {
            setAttr("id", id).render("inviteExpertGroup.html");
        }

    }

    /**
     * 查看服务机构详细信息
     * 参数flag
     * true   邀请第三方介入时查看
     * false  第三方申请介入时查看
     */
    @NotNullPara({"id"})
    public void seeFacAgency() {
        FacAgency facAgency = facAgencyService.findById(getParaToLong("id"));
        setAttr("facAgency", facAgency).render("facAgency.html");
    }

    /**
     * 渲染服务机构表格数据
     */
    @NotNullPara({"id"})
    public void facAgencyTable() {
        User user = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        try{
            Organization organization = organizationService.findById(user.getUserID());//找到组织机构信息
            //获取当前服务机构之外的其他所有服务机构信息
            Page<FacAgency> page = facAgencyService.findPageExcludeByOrgID(organization.getId(), pageNumber, pageSize);
            List<FacAgency> faList = page.getList();
            for (int i = 0; i < faList.size(); i++) {
                //设置第i个服务机构是否已经被项目id为指定id值的项目邀请的信息
                faList.get(i).setIsInvite(projectUndertakeService.findIsInvite(faList.get(i).getId(), getParaToLong("id")));
            }
            renderJson(new DataTable<FacAgency>(page));
        }
        catch (Exception ex){
            System.out.println("渲染服务机构表格数据失败，原因："+ ex.getMessage());
        }
    }


    /**
     * 邀请审查
     */
    @NotNullPara({"id", "projectId"})
    public void inviteReview() {
        User user = AuthUtils.getLoginUser();
        Notification notification = new Notification();
        notification.setName("项目邀请审查通知");
        notification.setSource("/app/project/inviteReview");
        notification.setContent("您好, " + user.getName() + " 邀请您介入项目 《" + projectService.findById(getParaToLong("projectId")).getName() + "》的审查，请及时处理！");
        notification.setReceiverID(userService.findByUserIdAndUserSource(expertGroupService.findById(getParaToLong("id")).getPersonID(), 0L).getId().intValue());
        notification.setCreateUserID(user.getId());
        notification.setCreateTime(new Date());
        notification.setLastUpdateUserID(user.getId());
        notification.setLastAccessTime(new Date());
        notification.setIsEnable(true);
        notification.setStatus(0);

        Date nowTime = new Date();
        Calendar time = Calendar.getInstance();
        //获取七天以后的日期作为申请的失效日期
        time.setTime(nowTime);
        time.add(Calendar.DATE, 7);

        ApplyInvite applyInvite = new ApplyInvite();
        applyInvite.setName(projectService.findById(getParaToLong("projectId")).getName());
        applyInvite.setModule(1);
        applyInvite.setCreateUserID(user.getId());
        applyInvite.setProjectID(getParaToLong("projectId"));
        applyInvite.setUserID(notification.getReceiverID().longValue());
        applyInvite.setApplyOrInvite(1);
        applyInvite.setStatus(ApplyInviteStatus.WAITE);
        applyInvite.setCreateTime(new Date());
        ApplyInvite timeTmp = applyInviteService.findByProjectID(getParaToLong("projectId"));
        if (timeTmp != null) {
            applyInvite.setDeadTime(timeTmp.getDeadTime());
        } else {
            applyInvite.setDeadTime(time.getTime());
        }
        applyInvite.setLastAccessTime(new Date());
        applyInvite.setLastUpdateUserID(user.getId());
        applyInvite.setIsEnable(true);
        if (!applyInviteService.saveOrUpdateAndSend(applyInvite, notification)) {
            throw new BusinessException("邀请失败");
        }
        renderJson();
    }

    /**
     * 渲染专家团体表格数据
     */
    @NotNullPara({"id"})
    public void expertGroupTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        ExpertGroup expertGroup = new ExpertGroup();
        expertGroup.setIsEnable(true);
        Page<ExpertGroup> page = expertGroupService.findPage(expertGroup, pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            page.getList().get(i).setIsInvite(applyInviteService.findIsInvite(userService.findByUserIdAndUserSource(expertGroupService.findById(page.getList().get(i).getId()).getPersonID(), 0L).getId(), getParaToLong("id")));
        }
        renderJson(new DataTable<ExpertGroup>(page));
    }

    /**
     * 更新狀態
     */
    @NotNullPara("id")
    public void updateStatus() {
        Long id = getParaToLong("id");
        AuthProject authProject = authProjectService.findByProjectId(id);
        authProject.setStatus(ProjectStatus.VERIFIING);
        if (!authProjectService.update(authProject)) {
            throw new BusinessException("请求错误");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 专家团体审查项目
     */
    public void reviewProject() {
        render("reviewProject.html");
    }

    /**
     * 专家团体审查项目表格渲染
     * 参数flag
     * 0 查看邀请审查的项目
     * 1 查看正在审查的项目
     * 2 查看审查完成的项目
     */
    @NotNullPara("flag")
    public void reviewProjectTable() {
        User user = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Integer flag = getParaToInt("flag");
        ApplyInvite applyInvite = new ApplyInvite();
        applyInvite.setIsEnable(true);
        applyInvite.setUserID(user.getId());
        applyInvite.setModule(1);
        if (flag == 0) {
            applyInvite.setStatus(ApplyInviteStatus.WAITE);
        } else if (flag == 1) {
            applyInvite.setStatus(ApplyInviteStatus.AGREE);
        } else if (flag == 2) {
            applyInvite.setStatus(null);
            applyInvite.setRemark("审查完成");
        }
        Page<ApplyInvite> page = applyInviteService.findPage(applyInvite, pageNumber, pageSize);
//        int size = page.getList().size();
//        for (int i = 0; i < size; i++) {
//
//        }
        renderJson(new DataTable<ApplyInvite>(page));
    }

    /**
     * 处理邀请/申请请求，
     * 参数 invite：
     * 1   拒绝
     * 2   同意
     * id：承接的 id
     * reply: 拒绝时回显
     * 参数type
     * 0   通过/不通过
     * 1   同意/拒绝
     */
    @NotNullPara({"id", "invite", "type"})
    public void saveInviteReview() {
        Integer invite = getParaToInt("invite");
        Long id = getParaToLong("id");
        Integer type = getParaToInt("type");
        if (invite == null || id == null) {
            renderJson(RestResult.buildError("请求参数错误"));
            throw new BusinessException("请求参数错误");
        }
        ApplyInvite applyInvite = applyInviteService.findById(id);
        if (applyInvite == null || !applyInvite.getIsEnable()) {
            renderJson(RestResult.buildError("请求参数错误"));
            throw new BusinessException("请求参数错误");
        }
        User user = AuthUtils.getLoginUser();
        String reply = null;
        Notification notification = new Notification();

        if (type == 1 && invite == 1) {
            reply = getPara("reply");
            notification.setName("邀请审查通知");
            notification.setContent(user.getName() + "已拒绝您的项目审查邀请！");
            applyInvite.setStatus(ApplyInviteStatus.REFUSE);
        } else if (type == 0 && invite == 1) {
            reply = getPara("reply");
            notification.setName("审查结果通知");
            notification.setContent(user.getName() + "对您的项目《" + projectService.findById(applyInvite.getProjectID()).getName() + "》的审查意见为：不通过！ 原因是：" + reply);
            applyInvite.setStatus(ApplyInviteStatus.NOPASS);
            applyInvite.setRemark("审查完成");
        } else if (type == 1 && invite == 2) {
            notification.setName("邀请审查通知");
            notification.setContent(user.getName() + "已接收您的项目审查邀请！");
            applyInvite.setStatus(ApplyInviteStatus.AGREE);
        } else if (type == 0 && invite == 2) {
            notification.setName("审查结果通知");
            notification.setContent(user.getName() + "对您的项目《" + projectService.findById(applyInvite.getProjectID()).getName() + "》的审查意见为：通过！");
            applyInvite.setStatus(ApplyInviteStatus.PASS);
            applyInvite.setRemark("审查完成");
        }
        Long projectID = applyInvite.getProjectID();

        applyInvite.setReply(reply);
        applyInvite.setLastUpdateUserID(user.getId());
        applyInvite.setLastAccessTime(new Date());


        notification.setSource("/app/project/saveInviteReview");
        notification.setRecModule("");
        notification.setReceiverID(Math.toIntExact(projectService.findById(projectID).getUserId()));
        notification.setCreateUserID(user.getId());
        notification.setCreateTime(new Date());
        notification.setLastUpdateUserID(user.getId());
        notification.setLastAccessTime(new Date());
        notification.setIsEnable(true);
        notification.setStatus(0);

        if (!applyInviteService.saveOrUpdateAndSend(applyInvite, notification)) {
            renderJson(RestResult.buildError("请求失败，请重新尝试！"));
            throw new BusinessException("请求失败，请重新尝试！");
        }
//        Project project = projectService.findById(projectID);
//        if (project != null) {
//            float allNum, passNum, rate;
//            applyInvite = new ApplyInvite();
//            applyInvite.setModule(1);
//            applyInvite.setProjectID(projectID);
//            applyInvite.setUserID(user.getId());
//            applyInvite.setIsEnable(true);
//            List<ApplyInvite> list = applyInviteService.findList(applyInvite);
//            allNum = list.size();
//            if (allNum >= 3) {
//                applyInvite.setStatus(ApplyInviteStatus.NOPASS);
//                list = applyInviteService.findList(applyInvite);
//                passNum = list.size();
//                rate = 1 - (passNum / allNum);
//                if (rate > 0.8) {
//                    project.setStatus(ProjectStatus.CHECKED);
//                } else {
//                    project.setStatus(ProjectStatus.REVIEW);
//                }
//                if (!projectService.update(project)) {
//                    renderJson(RestResult.buildError("更新项目状态失败！"));
//                    throw new BusinessException("更新项目状态失败！");
//                }
//            }
//        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 服务机构查询承接的审查完成的项目-表格渲染
     */
    @NotNullPara("projectFileTypeID")
    public void checkedTable() {
        User loginUser = AuthUtils.getLoginUser();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
//
        ProjectUndertake projectUndertake = new ProjectUndertake();
        Organization organization = organizationService.findById(loginUser.getUserID());//找到组织机构信息
        FacAgency facAgency = null;
        if(organization != null) {
            facAgency = facAgencyService.findByOrgId(organization.getId());//找到组织机构对应的服务机构信息
            if (facAgency != null) {
                projectUndertake.setFacAgencyID(facAgency.getId());
            }
        }else {
            projectUndertake.setFacAgencyID(loginUser.getId());
        }
        projectUndertake.setCreateUserID(loginUser.getId());
        projectUndertake.setStatus(Integer.valueOf(ProjectStatus.CHECKED));
        Page<Project> page = projectService.findPageBySql(projectUndertake, pageNumber, pageSize);

        if (managementService.findByOrgId(loginUser.getUserID()) != null) {
            Project project = new Project();
            project.setIsEnable(true);
            project.setStatus(ProjectStatus.CHECKED);
            page = projectService.findPage(project, pageNumber, pageSize);
        }
        Long projectFileTypeID = getParaToLong("projectFileTypeID");
        if (projectFileTypeID != 0) {
            for (int i = 0; i < page.getList().size(); i++) {
                FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(projectFileTypeID, page.getList().get(i).getId());
                if (fileProject != null) {
                    page.getList().get(i).setIsUpload(true);
                    page.getList().get(i).setFileID(fileProject.getFileID());
                } else {
                    page.getList().get(i).setIsUpload(false);
                }
                ProjectUndertake projectUndertake1;
                if (facAgency != null) {
                    projectUndertake1 = projectUndertakeService.findByProjectIdAndFacAgencyId(page.getList().get(i).getId(), facAgency.getId());
                } else {
                    projectUndertake1 = projectUndertakeService.findByProjectIdAndFacAgencyId(page.getList().get(i).getId(), loginUser.getId());

                }
                if (projectUndertake1 != null) {
                    page.getList().get(i).setRemark("1");
                }
            }
        }
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 审查汇总主页
     */
    @Before(GET.class)
    public void collectView(){
        render("collect.html");
    }

    /**
     * 审查汇总
     */
    @Before(GET.class)
    @NotNullPara({"pageNumber","pageSize"})
    public void collectTableData(){
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project=new Project();
        project.setStatus(ProjectStatus.CHECKED);
        project.setIsEnable(true);
        Page<Project> page=projectService.findPage(project,pageNumber,pageSize);
        renderJson(new DataTable<Project>(page));
    }
/**
 *  项目汇总
 */

    @Before(GET.class)
    public void projectCollect(){
        BaseStatus baseStatus=new BaseStatus() {};
//        baseStatus.add("4","评估中");
//        baseStatus.add("5","审查中");
//        baseStatus.add("7","审查通过");
        ProjectAssType model = new ProjectAssType();
        model.setIsEnable(true);
        List<ProjectAssType> PaTypeList = projectAssTypeService.findAll(model);
        if(PaTypeList != null) {
            for (ProjectAssType item :
                    PaTypeList) {
                baseStatus.add(item.getId().toString(), item.getName());
            }
        }
        setAttr("PaTypeNameList",baseStatus);
        render("projectCollect.html");
    }


    @Before(GET.class)
    @NotNullPara({"pageNumber","pageSize"})
    public void projectCollectTableData(){
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project=new Project();
        if(StrKit.notBlank(getPara("projectType"))) {
            project.setPaTypeID(Long.parseLong(getPara("projectType")));
        }
        project.setUserId(AuthUtils.getLoginUser().getId());
        project.setIsEnable(true);
        Page<Project> page=projectService.findPage(project,pageNumber,pageSize);
        renderJson(new DataTable<Project>(page));
    }
}
