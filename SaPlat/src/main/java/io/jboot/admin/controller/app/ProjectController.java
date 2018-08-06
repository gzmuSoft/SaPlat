package io.jboot.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.service.entity.status.system.ProjectStatus;
import io.jboot.admin.service.entity.status.system.ProjectTypeStatus;
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.ProjectValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private FileProjectService fileProjectService;

    @JbootrpcService
    private FilesService filesService;

    @JbootrpcService
    private EvaSchemeService evaSchemeService;

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
        List<ProjectAssType> PaTypeList = projectAssTypeService.findAll();
        List<ProjectStep> projectStepList = projectStepService.findAll();
        List<String> roleNameList = new ArrayList<>();
        for (int i = 0; i < authList.size(); i++) {
            roleNameList.add(roleService.findById(authList.get(i).getRoleId()).getName());
        }
        setAttr("roleNameList", roleNameList).setAttr("PaTypeNameList", PaTypeList).setAttr("projectStepNameList", projectStepList).render("projectInformation.html");
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
            if (getParaToBoolean("judgeFile")) {
                AuthProject authProject = new AuthProject();
                authProject.setUserId(loginUser.getId());
                authProject.setRoleId(roleService.findByName(projectService.findById(getParaToLong("projectId")).getRoleName()).getId());
                authProject.setProjectId(getParaToLong("projectId"));
                authProject.setLastUpdTime(new Date());
                authProject.setType(ProjectTypeStatus.INFORMATION_REVIEW);
                authProject.setName(loginUser.getName());
                authProject.setLastUpdUser(loginUser.getName());
                if (project.getAssessmentMode().equals("自评")) {
                    authProject.setStatus(ProjectStatus.REVIEW);
                    project.setStatus(ProjectStatus.REVIEW);
                } else if (project.getAssessmentMode().equals("委评")) {
                    authProject.setStatus(ProjectStatus.VERIFIING);
                    project.setStatus(ProjectStatus.VERIFIING);
                }
                if (authProjectService.save(authProject)) {
                    renderJson(RestResult.buildSuccess("项目状态表上传成功"));
                } else {
                    renderJson(RestResult.buildError("项目状态表上传失败"));
                    throw new BusinessException("项目状态表上传失败");
                }
            }
            if (getParaToLong("projectId") != -1) {
                project.setId(getParaToLong("projectId"));
            }
            if (projectService.update(project)) {
                renderJson(RestResult.buildSuccess("项目资料更新成功"));
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
        User user = AuthUtils.getLoginUser();
        Long id = getParaToLong("id");
        Project model = projectService.findById(id);
        model.setTypeName(projectAssTypeService.findById(model.getPaTypeID()).getName());
        setAttr("model", model).render("update.html");

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
    @NotNullPara({"id", "paTypeID"})
    public void fileTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long id = getParaToLong("id");
        ProjectAssType projectAssType = projectAssTypeService.findById(getParaToLong("paTypeID"));
        ProjectFileType parentProjectFileType = projectFileTypeService.findByName(projectAssType.getName());
        ProjectFileType childProjectFileType = new ProjectFileType();
        childProjectFileType.setParentID(parentProjectFileType.getId());
        Page<ProjectFileType> page = projectFileTypeService.findPage(childProjectFileType, pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            if (fileProjectService.findByProjectIDAndFileTypeID(id, page.getList().get(i).getId()) != null) {
                page.getList().get(i).setIsUpLoad(true);
            } else {
                page.getList().get(i).setIsUpLoad(false);
            }
        }
        renderJson(new DataTable<ProjectFileType>(page));
    }


    /**
     * 立项中-保存
     */
    @NotNullPara({"id", "assessmentMode"})
    public void saveProjectMessage() {
        Long id = getParaToLong("id");
        Project model = getBean(Project.class, "project");
        model.setId(id);
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
        LeaderGroup leaderGroup = leaderGroupService.findByProjectID(id);
        List<FileProject> fileProjects = fileProjectService.findAllByProjectID(id);
        List<ProjectFileType> projectFileTypes = projectFileTypeService.findListByParentId(1L);
        JSONObject json = new JSONObject();
        if (project != null && leaderGroup != null && projectFileTypes.size() == fileProjects.size()) {
            AuthProject authProject = new AuthProject();
            authProject.setProjectId(id);
            authProject.setRoleId(roleService.findByName(project.getRoleName()).getId());
            authProject.setUserId(user.getId());
            authProject.setType(ProjectTypeStatus.INFORMATION_REVIEW);
            authProject.setName(userService.findById(user.getId()).getName());
            if (project.getAssessmentMode().equals("委评")) {
                authProject.setStatus(ProjectStatus.VERIFIING);
                project.setStatus(ProjectStatus.VERIFIING);
            } else if (project.getAssessmentMode().equals("自评")) {
                authProject.setStatus(ProjectStatus.IS_VERIFY);
                project.setStatus(ProjectStatus.IS_VERIFY);
            }
            if (!projectService.saveOrUpdate(project, authProject)) {
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

        if (model.getFileTypeID() == 35L) {
            model.setId(null);
            model.setCreateTime(new Date());
            if (!fileProjectService.save(model)) {
                renderJson(RestResult.buildError("上传失败"));
                throw new BusinessException("上传失败");
            } else {
                Files files = filesService.findById(getParaToLong("fileTypeId"));
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
                Files files = filesService.findById(getParaToLong("fileTypeId"));
                files.setIsEnable(true);
                if (!filesService.update(files)) {
                    renderJson(RestResult.buildError("文件启用失败"));
                    throw new BusinessException("文件启用失败");
                }
            }
        }
        renderJson();

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
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setUserId(loginUser.getId());
        project.setStatus(ProjectStatus.REVIEW);
        project.setIsEnable(true);
        Page<Project> page = projectService.findPage(project, pageNumber, pageSize);
        renderJson(new DataTable<Project>(page));
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
        Project project = new Project();
        project.setUserId(loginUser.getId());
        project.setStatus(ProjectStatus.REVIEWED);
        project.setIsEnable(true);
        Page<Project> page = projectService.findPage(project, pageNumber, pageSize);
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 项目公开-填写日期
     */
    @NotNullPara({"id"})
    public void isPublicMessage() {
        Long id = getParaToLong("id");
        setAttr("id", id).render("public.html");
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
        notification.setContent("您好, " + user.getName() + " 邀请您介入项目 《" + projectService.findById(getParaToLong("projectId")).getName() + "》的评估，请及时处理！");
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
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Page<FacAgency> page = facAgencyService.findPage(pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            page.getList().get(i).setIsInvite(projectUndertakeService.findIsInvite(page.getList().get(i).getId(), getParaToLong("id")));
        }
        renderJson(new DataTable<FacAgency>(page));
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
        applyInvite.setStatus(0);
        applyInvite.setCreateTime(new Date());
        applyInvite.setDeadTime(time.getTime());
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
        Page<ExpertGroup> page = expertGroupService.findPage(pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            page.getList().get(i).setIsInvite(applyInviteService.findIsInvite(userService.findByUserIdAndUserSource(expertGroupService.findById(page.getList().get(i).getId()).getPersonID(), 0L).getId(), getParaToLong("id")));
            System.out.println("这个：" + page.getList().get(i).getIsInvite());
        }
        renderJson(new DataTable<ExpertGroup>(page));
    }
}
