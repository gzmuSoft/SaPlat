package io.jboot.admin.controller.app;

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
    private ProjectTypeService projectTypeService;

    @JbootrpcService
    private AuthProjectService authProjectService;

    /**
     * 项目立项基本资料初始化至信息管理界面
     */
    public void index() {
        User loginUser = AuthUtils.getLoginUser();
        List<Auth> authList = authService.findListByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.IS_VERIFY, TypeStatus.PROJECT_VERIFY);
        List<ProjectType> typeList = projectTypeService.findAll();
        List<String> roleNameList = new ArrayList<>();
        for (int i = 0; i < authList.size(); i++) {
            roleNameList.add(roleService.findById(authList.get(i).getRoleId()).getName());
        }
        setAttr("roleNameList", roleNameList).setAttr("typeNameList", typeList).render("projectInformation.html");
    }

    /**
     * 自评提交立项资料并进入已审核状态
     */
    @Before(GET.class)
    public void selfAssessment() {
        User loginUser = AuthUtils.getLoginUser();
        Project project = getBean(Project.class, "project");
        project.setCreateTime(new Date());
        project.setLastAccessTime(new Date());
        project.setDrawings("#/");
        project.setIsEnable(true);
        project.setStatus(ProjectStatus.IS_VERIFY);
        project.setUserId(loginUser.getId());
        project.setCreateUserID(loginUser.getId());
        project.setLastUpdateUserID(loginUser.getId());

        LeaderGroup leaderGroup = getBean(LeaderGroup.class, "leaderGroup");
        leaderGroup.setCreateTime(new Date());
        leaderGroup.setLastAccessTime(new Date());
        leaderGroup.setCreateUserID(loginUser.getId());
        leaderGroup.setLastUpdateUserID(loginUser.getId());

        AuthProject authProject = new AuthProject();
        authProject.setRoleId(roleService.findByName(project.getRoleName()).getId());
        authProject.setUserId(loginUser.getId());
        authProject.setLastUpdTime(new Date());
        authProject.setType(TypeStatus.PROJECT_VERIFY);
        authProject.setStatus(AuthStatus.IS_VERIFY);
        authProject.setName(loginUser.getName());
        authProject.setLastUpdUser(loginUser.getName());
        if (projectService.saveOrUpdate(project, authProject, leaderGroup)) {
            renderJson(RestResult.buildSuccess("立项成功"));
            render("verfed.html");
        } else {
            renderJson(RestResult.buildError("立项失败"));
            throw new BusinessException("立项失败");
        }
    }

    /**
     * 委评提交立项资料并进入待审核状态
     */
    @Before(GET.class)
    public void othersAssessment() {
        User loginUser = AuthUtils.getLoginUser();
        Project project = getBean(Project.class, "project");
        project.setCreateTime(new Date());
        project.setLastAccessTime(new Date());
        project.setDrawings("#/");
        project.setIsEnable(true);
        project.setStatus(ProjectStatus.VERIFIING);
        project.setUserId(loginUser.getId());
        project.setCreateUserID(loginUser.getId());
        project.setLastUpdateUserID(loginUser.getId());

        LeaderGroup leaderGroup = getBean(LeaderGroup.class, "leaderGroup");
        leaderGroup.setCreateTime(new Date());
        leaderGroup.setLastAccessTime(new Date());
        leaderGroup.setCreateUserID(loginUser.getId());
        leaderGroup.setLastUpdateUserID(loginUser.getId());

        AuthProject authProject = new AuthProject();
        authProject.setRoleId(roleService.findByName(project.getRoleName()).getId());
        authProject.setUserId(loginUser.getId());
        authProject.setLastUpdTime(new Date());
        authProject.setType(TypeStatus.PROJECT_VERIFY);
        authProject.setStatus(AuthStatus.VERIFYING);
        authProject.setName(loginUser.getName());
        authProject.setLastUpdUser(loginUser.getName());
        if (projectService.saveOrUpdate(project, authProject, leaderGroup)) {
            renderJson(RestResult.buildSuccess("提交立项资料成功，请等待审核"));
            render("verfiing.html");
        } else {
            renderJson(RestResult.buildError("立项失败"));
            throw new BusinessException("立项失败");
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
        model.setTypeName(projectTypeService.findById(model.getTypeID()).getName());
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
     * 通往项目管理界面-已审核
     */
    public void toVerfed() {
        render("verfed.html");
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
        renderJson(new DataTable<Project>(page));
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
    public void toAssing() {
        render("assing.html");
    }

    /**
     * 项目管理界面-评估中-表格渲染
     */
    public void assing() {
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
    public void toAssed() {
        render("assed.html");

    }

    /**
     * 项目管理界面-评估完成-表格渲染
     */
    public void assed() {
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
}
