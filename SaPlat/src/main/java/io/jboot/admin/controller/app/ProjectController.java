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
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.ProjectValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.ArrayList;
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
    private ProjectFileTypeService projectFileTypeService;

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
     * 立项文件列表表格渲染
     */
    public void fileTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        ProjectFileType projectFileType = new ProjectFileType();
        projectFileType.setParentID(1L);
        Page<ProjectFileType> page = projectFileTypeService.findPage(projectFileType, pageNumber, pageSize);
        renderJson(new DataTable<ProjectFileType>(page));
    }

    /**
     * 立项文件上传页面
     */
    @NotNullPara({"id","projectId"})
    public void fileUploading() {
        Long id = getParaToLong("id");
        ProjectFileType model = projectFileTypeService.findById(id);
        setAttr("projectId",getParaToLong("projectId")).setAttr("model", model).render("fileUploading.html");
    }

    /**
     * 项目文件关联
     */
    @NotNullPara({"fileId","projectId","fileTypeId"})
    public void upFile() {
        User user = AuthUtils.getLoginUser();
        FileProject model = new FileProject();
        model.setFileID(getParaToLong("fileId"));
        model.setProjectID(getParaToLong("projectId"));
        model.setFileTypeID(getParaToLong("fileTypeId"));
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setCreateUserID(user.getId());
        model.setLastUpdateUserID(user.getId());
        if(!fileProjectService.save(model)){
            renderJson(RestResult.buildError("上传失败"));
            throw new BusinessException("上传失败");
        }
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
            Long projectId = projectService.saveProject(project);
            JSONObject json = new JSONObject();
            json.put("projectId", projectId);
            if (projectId != -1L) {
                renderJson(json);
            } else {
                renderJson(RestResult.buildError("项目资料上传失败"));
                throw new BusinessException("项目资料上传失败");
            }
        } else if (saveOrUpdate == 0) {
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
        Project project = projectService.findById(id);
        LeaderGroup leaderGroup = leaderGroupService.findByProjectID(id);
        if (leaderGroup == null) {
            leaderGroup = new LeaderGroup();
        }
        List<FileProject> fileProject = fileProjectService.findAllByProjectID(id);
        if (fileProject == null) {
            fileProject = new ArrayList<>();
        }
        render("projectMessage.html");

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
     * 通往项目管理界面-已评估
     */
    public void review() {
        render("review.html");

    }

    /**
     * 项目管理界面-评估完成-表格渲染
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
     * 选择邀请的服务机构
     */
    @NotNullPara({"id"})
    public void inviteChoose() {
        Long id = getParaToLong("id");
        setAttr("id", id).render("invite.html");
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


}
