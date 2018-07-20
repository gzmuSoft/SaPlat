package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.FacAgencyService;
import io.jboot.admin.service.api.OrganizationService;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.api.ProjectUndertakeService;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.ProjectUndertakeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import java.util.Date;


@RequestMapping("/app/projectUndertake")
public class ProjectUndertakeController extends BaseController {

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private ProjectUndertakeService projectUndertakeService;

    @JbootrpcService
    private FacAgencyService facAgencyService;

    @JbootrpcService
    private OrganizationService organizationService;

    /**
     * 跳转榜单页面
     */
    public void toProjectList() {
        render("projectList.html");
    }

    /**
     * 榜单显示已发布的项目
     */
    public void projectList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setIsPublic(true);
        Page<Project> page = projectService.findPageByIsPublic(project, pageNumber, pageSize);
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 查看项目详细信息
     */
    @NotNullPara({"id"})
    public void see() {
        User user = AuthUtils.getLoginUser();
        Long id = getParaToLong("id");
        Project model = projectService.findById(id);
        setAttr("model", model).render("see.html");
    }

    /**
     * 介入项目
     */
    @NotNullPara({"id"})
    @RequiresRoles(value = {"组织机构", "服务机构"}, logical = Logical.AND)
    public void agency() {
        Long id = getParaToLong("id");
        User user = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(user.getUserID());
        FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());

        ProjectUndertake projectUndertake = projectUndertakeService.findByProjectIdAndFacAgencyId(id, facAgency.getId());
        Project project = projectService.findById(id);
        if (projectUndertake == null) {
            projectUndertake = new ProjectUndertake();
            projectUndertake.setCreateUserID(user.getId());
            projectUndertake.setCreateTime(new Date());
            projectUndertake.setProjectID(id);
            projectUndertake.setFacAgencyID(facAgency.getId());
        } else if (projectUndertake.getStatus() == 0) {
            renderJson(RestResult.buildError("您已经申请过了，请不要重复申请！"));
            throw new BusinessException("您已经申请过了，请不要重复申请！");
        }
        projectUndertake.setName(project.getName());
        projectUndertake.setDeadTime(project.getEndTime());
        projectUndertake.setApplyOrInvite(false);
        projectUndertake.setStatus(0);
        projectUndertake.setReply(null);
        projectUndertake.setLastAccessTime(new Date());
        projectUndertake.setLastUpdateUserID(user.getId());
        projectUndertake.setRemark(null);
        projectUndertake.setIsEnable(true);

        Notification notification = new Notification();
        notification.setName("项目承接通知");
        notification.setSource("/app/projectAndServiceOrg");
        notification.setContent("您好，" + organization.getName() + "希望承接您的项目委评，请及时处理！");
        //TODO 待处理接受模块路径
        notification.setRecModule("");
        notification.setReceiverID(Math.toIntExact(project.getUserId()));
        notification.setCreateTime(new Date());
        notification.setCreateUserID(user.getId());
        notification.setLastAccessTime(new Date());
        notification.setLastUpdateUserID(user.getId());
        notification.setStatus(Integer.valueOf(ProjectUndertakeStatus.WAITING));
        notification.setIsEnable(true);

        if (!projectUndertakeService.saveOrUpdateAndSend(projectUndertake, notification)) {
            renderJson(RestResult.buildError("申请介入失败，请重新尝试。"));
            throw new BusinessException("申请介入失败，请重新尝试。");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 加载承接详情页面
     */
    public void projectUndertakeIndex() {
        render("projectUndertake.html");
    }

    /**
     * 承接详情页面数据加载，
     * 参数 applyOrInvite ：
     * true    主动申请
     * false   被邀请
     */
    public void projectUndertakeList() {
        Boolean applyOrInvite = getParaToBoolean("applyOrInvite");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        ProjectUndertake projectUndertake = new ProjectUndertake();
        projectUndertake.setApplyOrInvite(applyOrInvite);
        projectUndertake.setIsEnable(true);
        Page<ProjectUndertake> page = projectUndertakeService.findPage(projectUndertake, pageNumber, pageSize);
        renderJson(new DataTable<ProjectUndertake>(page));
    }

    /**
     * 处理邀请请求，
     * 参数 invite：
     * 1   拒绝
     * 2   同意
     * id：承接的 id
     * reply: 拒绝时回显
     */
    public void invite() {
        Integer invite = getParaToInt("invite");
        Long id = getParaToLong("id");
        if (invite == null || id == null|| (!invite.equals(Integer.valueOf(ProjectUndertakeStatus.REFUSE))
                && !invite.equals(Integer.valueOf(ProjectUndertakeStatus.ACCEPT)) )) {
            renderJson(RestResult.buildError("请求参数错误"));
            throw new BusinessException("请求参数错误");
        }
        ProjectUndertake projectUndertake = projectUndertakeService.findById(id);
        if (projectUndertake == null || !projectUndertake.getIsEnable()) {
            renderJson(RestResult.buildError("请求参数错误"));
            throw new BusinessException("请求参数错误");
        }
        User user = AuthUtils.getLoginUser();
        String reply = null;
        Notification notification = new Notification();
        if (invite.equals(Integer.valueOf(ProjectUndertakeStatus.REFUSE))) {
            reply = getPara("reply");
            notification.setName("邀请拒绝通知");
            notification.setContent(user.getName() + "已拒绝您的邀请！");
            projectUndertake.setStatus(Integer.valueOf(ProjectUndertakeStatus.REFUSE));
        }
        if (invite.equals(Integer.valueOf(ProjectUndertakeStatus.ACCEPT))) {
            notification.setName("邀请接受通知");
            notification.setContent(user.getName() + "已接受您的邀请！");
            projectUndertake.setStatus(Integer.valueOf(ProjectUndertakeStatus.ACCEPT));
        }
        projectUndertake.setReply(reply);
        projectUndertake.setLastUpdateUserID(user.getId());
        projectUndertake.setLastAccessTime(new Date());

        Long projectID = projectUndertake.getProjectID();
        Long receiverID = projectService.findById(projectID).getUserId();
        notification.setSource("/app/projectUndertake/invite");
        //TODO 等待书写接受模块
        notification.setRecModule("");
        notification.setReceiverID(Math.toIntExact(receiverID));
        notification.setCreateUserID(user.getId());
        notification.setCreateTime(new Date());
        notification.setLastUpdateUserID(user.getId());
        notification.setLastAccessTime(new Date());
        notification.setIsEnable(true);
        notification.setStatus(0);

        if (!projectUndertakeService.saveOrUpdateAndSend(projectUndertake,notification)){
            renderJson(RestResult.buildError("请求失败，请重新尝试！"));
            throw new BusinessException("请求失败，请重新尝试！");
        }
        renderJson(RestResult.buildSuccess());
    }
}
