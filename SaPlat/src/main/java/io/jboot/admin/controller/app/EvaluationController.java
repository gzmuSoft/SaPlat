package io.jboot.admin.controller.app;

import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.ProjectStatus;
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
    private ProjectFileTypeService projectFileTypeService;

    /**
     * 评估详情
     */
    @NotNullPara({"id"})
    public void evaluationInformation() {
        Long id = getParaToLong("id");
        Project project = projectService.findFirstByColumns(new String[]{"id", "status"},
                new String[]{id.toString(), ProjectStatus.REVIEW});
        EvaScheme evaScheme = evaSchemeService.findByProjectID(id);
        if (project == null) {
            project = new Project();
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

        setAttr("project", project.toJson())
                .setAttr("evaScheme", evaScheme.toJson())
                .render("evaluation.html");
    }
}
