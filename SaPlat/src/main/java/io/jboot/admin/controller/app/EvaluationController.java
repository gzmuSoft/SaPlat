package io.jboot.admin.controller.app;

import com.jfinal.kit.StrKit;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.ProjectStatus;
import io.jboot.admin.service.entity.status.system.ProjectUndertakeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

/**
 * -----------------------------
 * 评估阶段
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 21:24 2018/8/1
 */
@RequestMapping("/app/evaluation")
public class EvaluationController extends BaseController {

    @JbootrpcService
    private UserRoleService userRoleService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private EvaSchemeService evaSchemeService;

    @JbootrpcService
    private SiteSurveyExpertAdviceService siteSurveyExpertAdviceService;

    @JbootrpcService
    private DiagnosesService diagnosesService;

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private ProjectUndertakeService projectUndertakeService;

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @JbootrpcService
    private OrganizationService organizationService;

    @JbootrpcService
    private FacAgencyService facAgencyService;

    /**
     * 评估详情
     */
    @NotNullPara({"id"})
    public void evaluationInformation() {
        Long id = getParaToLong("id");
        Project project = projectService.findFirstByColumns(new String[]{"id", "status"},
                new String[]{id.toString(), ProjectStatus.REVIEW});
        if (project == null) {
            throw new BusinessException("当前项目不符合要求！");
        }
        EvaScheme evaScheme = evaSchemeService.findByProjectID(id);

        User u = AuthUtils.getLoginUser();
        // 如果为委评
        if ("委评".equals(project.getAssessmentMode())) {
            // 当前项目是否是由他自己创建
            if (!project.getUserId().equals(u.getId())) {
                // 通过项目 id 和状态查询承接表
                ProjectUndertake projectUndertake = projectUndertakeService.findByProjectIdAndStatus(project.getId(), ProjectUndertakeStatus.ACCEPT);
                // 如果查不到
                if (projectUndertake == null) {
                    throw new BusinessException("当前项目无人承接！");
                }
                // 查找角色
                Role role = roleService.findByName("服务机构");
                UserRole userRole = userRoleService.findByUserIdAndRoleId(u.getId(), role.getId());
                // 验证角色
                if (userRole == null) {
                    throw new BusinessException("请先认证服务机构！");
                }
                User user = AuthUtils.getLoginUser();
                Organization organization = organizationService.findById(user.getUserID());
                FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
                if (facAgency == null) {
                    throw new BusinessException("找不到对应服务机构的身份信息！");
                }
//                if (projectUndertake.getFacAgencyID().equals(projectUndertake.getCreateUserID())) {
//                    throw new BusinessException("当前项目是自评项目！");
//                }
                if (projectUndertake.getApplyOrInvite()) {
                    // 当他为邀请的时候，通过服务机构 id 和查出的 服务机构 id 进行验证
                    if (!facAgency.getId().equals(projectUndertake.getFacAgencyID())) {
                        throw new BusinessException("当前用户与项目承接人身份不对应！");
                    }
                } else {
                    // 当他为申请的时候，通过用户 id 和查出的 创建用户 id 进行验证
                    if (!projectUndertake.getCreateUserID().equals(user.getId())) {
                        throw new BusinessException("当前用户与项目承接人身份不对应！");
                    }
                }
                setAttr("isSelf", "false");
            } else {
                setAttr("isSelf", "true");
            }
            setAttr("method", "false");
        } else if (!project.getUserId().equals(u.getId())) {
            throw new BusinessException("这个不是你的项目哦~！");
        } else if ("自评".equals(project.getAssessmentMode())) {
            // 如果为自评
            setAttr("method", "true");
        }

        if (evaScheme == null) {
            evaScheme = new EvaScheme();
        } else {
            List<SiteSurveyExpertAdvice> model = siteSurveyExpertAdviceService.findListByProjectId(id);
            if (model == null || model.size() == 0) {
                setAttr("siteSurveyExpertAdvice", "false");
            } else {
                setAttr("siteSurveyExpertAdvice", "true");
            }
            List<Diagnoses> diagnoses = diagnosesService.findListByProjectId(id);
            if (diagnoses == null || diagnoses.size() == 0) {
                setAttr("diagnoses", "false");
            } else {
                setAttr("diagnoses", "true");
            }
        }
        List<ProjectFileType> list = projectFileTypeService.findListByParentId(projectFileTypeService.findByName("评估文件").getId());
        for (ProjectFileType p : list) {
            List<FileProject> fileProjects = fileProjectService.findListByFileTypeIDAndProjectID(p.getId(), id);
            if (fileProjects != null && fileProjects.size() > 0) {
                setAttr(p.getUrl(), "true");
            } else {
                setAttr(p.getUrl(), "false");
            }
        }

        setAttr("project", project)
                .setAttr("evaSchemeStatus", evaScheme.getStatus())
                .render("evaluation.html");
    }

    /**
     * 评估详情
     */
    @NotNullPara({"id"})
    public void evaluationInformation_mgr() {
        Long id = getParaToLong("id");
        String entry = getPara("entry");
        Project project = null;
        if (StrKit.notBlank(entry) && StrKit.equals("mgr", entry)) {
            project = projectService.findFirstByColumns(new String[]{"id"},
                    new String[]{id.toString()});
            if (project == null) {
                throw new BusinessException("数据异常");
            }
        } else {
            project = projectService.findFirstByColumns(new String[]{"id", "status"},
                    new String[]{id.toString(), ProjectStatus.REVIEW});
            if (project == null) {
                throw new BusinessException("当前项目不符合要求！");
            }
        }
        setAttr("isSelf", "true");
        setAttr("method", "false");
        EvaScheme evaScheme = evaSchemeService.findByProjectID(id);

        if (evaScheme == null) {
            evaScheme = new EvaScheme();
        } else {
            List<SiteSurveyExpertAdvice> model = siteSurveyExpertAdviceService.findListByProjectId(id);
            if (model == null || model.size() == 0) {
                setAttr("siteSurveyExpertAdvice", "false");
            } else {
                setAttr("siteSurveyExpertAdvice", "true");
            }
            List<Diagnoses> diagnoses = diagnosesService.findListByProjectId(id);
            if (diagnoses == null || diagnoses.size() == 0) {
                setAttr("diagnoses", "false");
            } else {
                setAttr("diagnoses", "true");
            }
        }
        List<ProjectFileType> list = projectFileTypeService.findListByParentId(projectFileTypeService.findByName("评估文件").getId());
        for (ProjectFileType p : list) {
            List<FileProject> fileProjects = fileProjectService.findListByFileTypeIDAndProjectID(p.getId(), id);
            if (fileProjects != null && fileProjects.size() > 0) {
                setAttr(p.getUrl(), "true");
            } else {
                setAttr(p.getUrl(), "false");
            }
        }

        setAttr("project", project)
                .setAttr("evaSchemeStatus", evaScheme.getStatus())
                .setAttr("entry", "mgr_agency")
                .render("evaluation.html");
    }
}
