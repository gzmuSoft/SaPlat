package io.jboot.admin.controller.app;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/25.
 */
@RequestMapping("/app/diagnoses")
public class DiagnosesController extends BaseController {
    @JbootrpcService
    private DiagnosesService diagnosesService;

    @JbootrpcService
    private ProjectUndertakeService projectUndertakeService;


    @JbootrpcService
    private ImpTeamService impTeamService;

    @JbootrpcService
    private OrganizationService organizationService;

    @JbootrpcService
    private FacAgencyService facAgencyService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private FilesService filesService;


    @JbootrpcService
    private PersonService personService;

    @Before(GET.class)
//    @NotNullPara("projectId")
    public void index() {
//        Project project=projectService.findById(getParaToLong("projectId"));
        Project project = projectService.findById(28);
        if (project == null) {
            throw new BusinessException("没有此项目");
        }
        setAttr("project", project);
//        FileProject fileProject =fileProjectService.findByProjectID(project.getId());
//        Files files=filesService.findById(fileProject.getFileID());
        Files files = filesService.findById(30);
        setAttr("files", files);
        setAttr("pdfURL", "upload/2018-07-26/2f0d703e-c0d9-4b4f-a158-53a9204bea42.pdf");

        ImpTeam impTeam = impTeamService.findByUserIDAndProjectID(AuthUtils.getLoginUser().getId(), project.getId());
        String invTeamIDs = impTeam.getInvTeamIDs();
        System.out.println(invTeamIDs);
        List<String> invTeamIDList = java.util.Arrays.asList(invTeamIDs.split(","));
        Map<String, String> invTeamMap = new HashMap<String, String>();
        for (String invTeamID : invTeamIDList) {
            Person person = personService.findById(invTeamID);
            invTeamMap.put(invTeamID, person.getName());
        }
        setAttr("invTeamMap", invTeamMap);
        Organization organization = organizationService.findById(AuthUtils.getLoginUser().getUserID());
        FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
        setAttr("facagency", facAgency);
        render("main.html");
    }

    @Before(POST.class)
    @NotNullPara({"staffArrangements", "surveyWays"})
    public void save() {
        Diagnoses diagnoses = getBean(Diagnoses.class, "diagnoses");
        String[] idList = getParaValues("staffArrangements");
        diagnoses.setStaffArrangements(StringUtils.join(idList, ","));
        String[] surveyWayList = getParaValues("surveyWays");
        diagnoses.setSurveyWay(StringUtils.join(surveyWayList, ","));
        diagnoses.setCreateUserID(AuthUtils.getLoginUser().getId());
        diagnoses.setLastUpdateUserID(AuthUtils.getLoginUser().getId());

        if (!diagnosesService.save(diagnoses)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

}
