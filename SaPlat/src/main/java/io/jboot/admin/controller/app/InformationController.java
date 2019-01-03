package io.jboot.admin.controller.app;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ResultCode;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.ProjectUndertakeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 16:35 2018/7/25
 */
@RequestMapping("/app/information")
public class InformationController extends BaseController {

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private PersonService personService;

    @JbootrpcService
    private ExpertGroupService expertGroupService;

    @JbootrpcService
    private ImpTeamService impTeamService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private OrganizationService organizationService;

    @JbootrpcService
    private FacAgencyService facAgencyService;

    @JbootrpcService
    private ProjectUndertakeService projectUndertakeService;

    @JbootrpcService
    private AffectedGroupService affectedGroupService;

    @JbootrpcService
    private OccupationService occupationService;

    @JbootrpcService
    private PostService postService;

    @JbootrpcService
    private SiteSurveyExpertAdviceService siteSurveyExpertAdviceService;

    @JbootrpcService
    private UserRoleService userRoleService;

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private FilesService filesService;

    @JbootrpcService
    private DiagnosesService diagnosesService;

    @JbootrpcService
    private NationService nationService;

    @JbootrpcService
    private EducationalService educationalService;

    @JbootrpcService
    private QuestionnaireService questionnaireService;

    @JbootrpcService
    private QuestionnaireContentService questionnaireContentService;

    @JbootrpcService
    private QuestionnaireContentLinkService questionnaireContentLinkService;

    @JbootrpcService
    private InitialRiskExpertiseService initialRiskExpertiseService;
    @JbootrpcService
    private ManagementService managementService;

    /**
     * 文件上传页面
     */
    @NotNullPara({"id", "projectId", "questionnaireId"})
    public void fileUploading() {
        Long id = getParaToLong("id");
        String questionnaireId = getPara("questionnaireId");
        ProjectFileType model = projectFileTypeService.findById(id);
        setAttr("projectId", getParaToLong("projectId"))
                .setAttr("model", model)
                .setAttr("questionnaireId", questionnaireId)
                .render("fileUploading.html");
    }

    /**
     * 资料编辑页面
     */
    @NotNullPara("id")
    public void edit() {
        setAttr("projectId", getParaToLong("id"))
                .setAttr("percent", getParaToLong("percent"))
                .setAttr("flag", getPara("flag", "false"))
                .render("edit.html");
    }


    /**
     * edit 下的欢迎页面
     */
    public void welcome() {
        List<ProjectFileType> list = projectFileTypeService.findListByParentId(projectFileTypeService.findByName("评估").getId());
        ProjectFileType projectFileType = new ProjectFileType();
        projectFileType.setName("稳评方案");
        list.add(0, projectFileType);
        setAttr("projectId", getParaToLong("id"))
                .setAttr("list", list)
                .setAttr("flag", getPara("flag", "false"))
                .render("welcome.html");
    }


    /**
     * 个人资料填写，加载当前可以填写的项目
     */
    @NotNullPara("id")
    public void editData() {
        Long id = getParaToLong("id");
        Project project = projectService.findById(id);
        if (project == null) {
            renderJson(RestResult.buildError("项目不存在"));
            throw new BusinessException("项目不存在");
        }
//        User user = AuthUtils.getLoginUser();
//        Person person = personService.findByUser(user);
//        ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
//        ImpTeam impTeam = impTeamService.findByProjectId(project.getId());
//        if (!impTeam.getExpertGroupIDs().contains(String.valueOf(expertGroup.getId()))){
//            renderJson(RestResult.buildError("关系不对应"));
//            throw new BusinessException("关系不对应");
//        }
//        List<InformationFill> informationFills = informationFillService.findByRoleId(roleService.findByName("专家团体").getId());
        Long parentId = projectFileTypeService.findByName("评估").getId();
        List<ProjectFileType> projectFileTypeList = projectFileTypeService.findListByParentId(parentId);
        Map<String, Object> res = new ConcurrentHashMap<>();
        res.put("list", projectFileTypeList);
        res.put("code", ResultCode.SUCCESS);
        res.put("projectId", project.getId());
        renderJson(res);

    }

    /**
     * 通用表格页面渲染
     */
    public void list() {
        String url = getPara("url");
        Long projectId = getParaToLong("id");
        String flag = getPara("flag");
        setAttr("url", url)
                .setAttr("projectId", projectId)
                .setAttr("flag", flag)
                .render("tableView.html");
    }

    /**
     * 调查问卷
     */
    @NotNullPara("projectID")
    public void questionnaireList() {
        String url = getPara("url");
        Long projectID = getParaToLong("projectID");
        setAttr("url", url)
        .setAttr("flag", getPara("flag", "false"))
        .setAttr("projectID", projectID)
        .render("questionnaireList.html");
    }

    /**
     * 专家数据
     */
    public void expertAdviceDataTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long projectId = getParaToLong("id");
        SiteSurveyExpertAdvice model = new SiteSurveyExpertAdvice();
        model.setProjectID(projectId);
        Page<SiteSurveyExpertAdvice> page = siteSurveyExpertAdviceService.findPage(model, pageNumber, pageSize);
        page.getList().forEach(p -> p.setRemark("专家：" + expertGroupService.findById(p.getExpertID()).getName()));

        renderJson(new DataTable<SiteSurveyExpertAdvice>(page));
    }

    /**
     * 现场踏勘专家意见
     */
    @NotNullPara("projectID")
    public void expertAdvice() {
        Long projectID = getParaToLong("projectID");
        Long id = getParaToLong("id");
        Project project = projectService.findById(projectID);
        User user = AuthUtils.getLoginUser();
        List<ExpertGroup> expertGroups = expertGroupService.findAll();
        String assessmentMode = project.getAssessmentMode();
        String name = null;
        if ("自评".equals(assessmentMode)) {
            Organization organization = organizationService.findById(userService.findById(project.getUserId()).getUserID());
            name = organization.getName();
        } else {
            ProjectUndertake projectUndertake = projectUndertakeService.findListByProjectAndStatus(projectID, ProjectUndertakeStatus.ACCEPT).get(0);
            if (projectUndertake == null) {
                renderJson(RestResult.buildError("項目还不能填写资料"));
                throw new BusinessException("項目还不能填写资料");
            }
            Organization organization = organizationService.findById(user.getUserID());
            FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
            if (facAgency == null) {
                facAgency = projectUndertake.getFacAgency();
            }
            name = facAgency.getName();
        }
        SiteSurveyExpertAdvice model = new SiteSurveyExpertAdvice();
        ExpertGroup expertGroup = null;
        if (id != null) {
            model = siteSurveyExpertAdviceService.findById(id);
            expertGroup = expertGroupService.findById(model.getExpertID());
        }
        String date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        String para = getPara("flag", "false");
        setAttr("expertGroups", expertGroups)
                .setAttr("flag", para)
                .setAttr("thisId", id)
                .setAttr("expertGroup", expertGroup)
                .setAttr("phone", user.getPhone())
                .setAttr("projectID", project.getId())
                .setAttr("name", name)
                .setAttr("date", date)
                .setAttr("model", model)
                .setAttr("otherComments", model.getOtherComments())
                .setAttr("resolving", model.getResolving())
                .setAttr("riskFactor", model.getRiskFactor())
                .render("expertAdvice.html");
    }

    /**
     * 现场踏勘专家意见提交
     */
    @Before(POST.class)
    @NotNullPara("projectID")
    public void expertAdvicePost() {
        Long projectID = getParaToLong("projectID");
        Long expertGroupId = getParaToLong("expertGroupId");
        String riskFactor = getPara("riskFactor");
        String resolving = getPara("resolving");
        String content = getPara("content");
        Long id = getParaToLong("id");
        User user = AuthUtils.getLoginUser();
        SiteSurveyExpertAdvice model = new SiteSurveyExpertAdvice();
        model.setName("现场踏勘专家意见");
        model.setProjectID(projectID);
        model.setExpertID(expertGroupId);
        model.setCreateTime(new Date());
        model.setIsEnable(true);
        model.setLastAccessTime(new Date());
        model.setLastUpdateUserID(user.getId());
        model.setCreateUserID(user.getId());
        model.setRiskFactor(riskFactor);
        model.setResolving(resolving);
        model.setOtherComments(content);
        model.setSort(1);
        if (id != null) {
            model.setId(id);
        }
        if (!siteSurveyExpertAdviceService.saveOrUpdate(model)) {
            renderJson(RestResult.buildError("啊哦，保存失败，请重新尝试！"));
            throw new BusinessException("啊哦，保存失败，请重新尝试！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 删除
     */
    @Before(POST.class)
    @NotNullPara("id")
    public void expertAdviceDelete() {
        Long id = getParaToLong("id");
        SiteSurveyExpertAdvice model = siteSurveyExpertAdviceService.findById(id);
        if (model == null) {
            throw new BusinessException("删除失败！");
        }
        if (!siteSurveyExpertAdviceService.delete(model)) {
            throw new BusinessException("删除失败！");
        }
        renderJson(RestResult.buildSuccess());
    }


    /**
     * 调查分析
     */
    @Before(GET.class)
    @NotNullPara("projectID")
    public void diagnoses() {
        Long diagnosesID = getParaToLong("id");
        Diagnoses diagnoses;
        if (diagnosesID == null) {
            diagnoses = new Diagnoses();
        } else {
            diagnoses = diagnosesService.findById(diagnosesID);
        }
        setAttr("diagnoses", diagnoses);
        Project project = projectService.findById(getParaToLong("projectID"));
        if (project == null) {
            throw new BusinessException("没有此项目");
        }
        setAttr("project", project);
        FileProject fileProject = fileProjectService.findByProjectID(project.getId());
        Files files = null;
        if(null != fileProject)
            files = filesService.findById(fileProject.getFileID());
        if(null == files)
            files = new Files();
        setAttr("files", files);
        ProjectUndertake projectUndertake = projectUndertakeService.findByProjectIdAndStatus(project.getId(), ProjectUndertakeStatus.ACCEPT);
        User user = AuthUtils.getLoginUser();
        Organization organization;
        /**
         * 自评的时候
         * projectUndertake的facAgencyID是
         */
        if ("自评".equals(project.getAssessmentMode())) {
            user = userService.findById(projectUndertake.getFacAgencyID());
            organization = organizationService.findById(user.getUserID());
        } else if ("委评".equals(project.getAssessmentMode())) {
            if (projectUndertake.getApplyOrInvite()) {
                FacAgency facAgency = facAgencyService.findById(projectUndertake.getFacAgencyID());
                organization = organizationService.findById(facAgency.getOrgID());
                user = userService.findByUserIdAndUserSource(organization.getId(), 1);
            } else {
                user = userService.findById(projectUndertake.getCreateUserID());
                organization = organizationService.findById(user.getUserID());
            }
        } else {
            throw new BusinessException("请求错误");
        }
        ImpTeam impTeam = impTeamService.findByUserIDAndProjectID(user.getId(), project.getId());
        String invTeamIDs = impTeam.getInvTeamIDs();
        String[] invTeamIDList = invTeamIDs.split(",");
        Map<String, String> invTeamMap = new ConcurrentHashMap<String, String>();
        for (String invTeamID : invTeamIDList) {
            Person person = personService.findById(invTeamID);
            if (person != null){
                invTeamMap.put(invTeamID, person.getName());
            }
        }
        setAttr("invTeamMap", invTeamMap);
        setAttr("organization", organization)
                .setAttr("flag", getPara("flag", "false"));
        render("diagnose.html");
    }

    /**
     * 调查分析数据表格
     */
    public void diagnosesDataTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long projectId = getParaToLong("id");
        Diagnoses diagnoses = new Diagnoses();
        diagnoses.setProject(projectId);
        Page<Diagnoses> page = diagnosesService.findPage(diagnoses, pageNumber, pageSize);
        page.getList().forEach(p -> p.setRemark("调查群体：" + p.getSurveyGroup()));
        renderJson(new DataTable<Diagnoses>(page));
    }

    /**
     * 调查分析保存
     */
    @Before(POST.class)
    @NotNullPara({"staffArrangements", "surveyWays"})
    public void diagnosesSave() {
        Diagnoses diagnoses = getBean(Diagnoses.class, "diagnoses");
        String[] idList = getParaValues("staffArrangements");
        String file = getPara("file");
        if (StringUtils.isNotEmpty(file)) {
            Files files = filesService.findById(file);
            files.setIsEnable(true);
            if (!filesService.update(files)) {
                throw new BusinessException("保存失败");
            }
            diagnoses.setRemark(file);
        }
        diagnoses.setStaffArrangements(StringUtils.join(idList, ","));
        String[] surveyWayList = getParaValues("surveyWays");
        diagnoses.setSurveyWay(StringUtils.join(surveyWayList, ","));
        diagnoses.setCreateUserID(AuthUtils.getLoginUser().getId());
        diagnoses.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        // 没有字段存放“社会稳定风险评估调查分析表”，使用reamrk字段
        if (!diagnosesService.saveOrUpdate(diagnoses)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara("id")
    public void diagnosesDelete() {
        Long id = getParaToLong("id");
        Diagnoses diagnoses = diagnosesService.findById(id);
        if (diagnoses == null) {
            throw new BusinessException("删除失败");
        }
        if (!diagnosesService.delete(diagnoses)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }


    /**
     * 调查问卷数据表格
     */
    public void questionnaireDataTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long projectId = getParaToLong("projectID");
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setProjectID(projectId);
        questionnaire.setIsEnable(true);
        Page<Questionnaire> page = questionnaireService.findPage(questionnaire, pageNumber, pageSize);
        page.getList().forEach(p -> {
            StringBuilder sb = new StringBuilder();
            if (p.getType() == 1) {
                sb.append("调查对象：单位");
            } else {
                sb.append("调查对象：个人");
            }
            p.setRemark(sb.toString());
            p.setName("调查问卷");
        });
        renderJson(new DataTable<Questionnaire>(page));
    }


    /**
     * personOrOrganization
     */
    @NotNullPara("projectID")
    public void personOrOrganization() {
        Project project = projectService.findById(getPara("projectID"));
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setType(getParaToInt("type"));
        setAttr("flag", getPara("flag", "false"));
        setAttr("questionnaire", questionnaire).
                setAttr("project", project).
                setAttr("projectID", project.getId());
        //判断跳转的页面是哪一个
        if (questionnaire.getType() == 0) {
            render("personMain.html");
        } else {
            render("organizationMain.html");
        }
    }

    /**
     * questionnaire (有内容则修改 无内容则创建)
     */
    @NotNullPara("projectID")
    public void questionnaire() {
        User loginUser = AuthUtils.getLoginUser();
        Project project = projectService.findById(getParaToLong("projectID"));
        Questionnaire questionnaire = questionnaireService.findById(getParaToLong("id"));
        setAttr("flag", getPara("flag", "false"));
        //选择问卷为空则是创建
        setAttr("projectID", project.getId())
                .setAttr("project", project);
        if (questionnaire == null) {
            render("personOrOrganization.html");
        } else {
            setAttr("questionnaire", questionnaire);
            if (questionnaire.getType() == 0) {
                render("personMain.html");
            } else {
                render("organizationMain.html");
            }
        }
    }

    /**
     * 修改或者保存调查问卷
     */
    public void questionnaireAdd() {
        User loginUser = AuthUtils.getLoginUser();
        //项目资料
        //Project project = getBean(Project.class, "project");
        String questionnaireId= getPara("questionnaireId");
        Long projectID = getParaToLong("projectID");
        //project.setLastUpdateUserID(loginUser.getId());
        //调查问卷
        Questionnaire questionnaire = getBean(Questionnaire.class, "questionnaire");

        //修改则有问卷id 创建则没有
        if (questionnaire.getProjectID() == null) {
            questionnaire.setProjectID(projectID);
        }
        if (questionnaire.getCreateUserID() == null) {
            questionnaire.setCreateUserID(loginUser.getId());
        }
        String files = getPara("file");
        String[] split = files.split("-");
        ArrayList<Integer> ids = new ArrayList<>();
        for (String aSplit : split) {
            if (StringUtils.isNotEmpty(aSplit) && StringUtils.isNumeric(aSplit)) {
                ids.add(Integer.parseInt(aSplit));
            }
        }
        if (!questionnaireService.saveQuestionnaire(questionnaire, ids, project)) {
            throw new BusinessException("保存失败!");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 删除调查问卷以及内容
     */
    @NotNullPara("id")
    public void questionnaireDelete() {
        Questionnaire questionnaire = questionnaireService.findById(getParaToLong("id"));
        if (questionnaire != null) {
            if (!questionnaireService.deleteQuestionnaire(questionnaire)) {
                throw new BusinessException("删除失败!");
            }
        } else {
            throw new BusinessException("删除的数据不存在!");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 选择添加的风险因素界面
     */
    @NotNullPara("projectID")
    public void chooseRiskExpertise() {
        setAttr("projectId", getPara("projectID")).render("chooseRiskExpertise.html");
    }

    /**
     * 查看风险因素及影响概率
     */
    @NotNullPara({"projectID", "id"})
    public void riskExpertise() {
        String managementName = null;
        String riskExpertise = null;
        String riskProbability = "0%";
        String incidenceExpertise = null;
        String incidenceProbability = "0%";
        ExpertGroup expertGroup = new ExpertGroup();
        InitialRiskExpertise initialRiskExpertise = initialRiskExpertiseService.findById(getParaToLong("id"));
        Project project = projectService.findById(getParaToLong("projectID"));
        if (project != null) {
            Management management = managementService.findById(project.getManagementID());
            if (management != null) {
                managementName = management.getName();
            }
        }
        if (initialRiskExpertise != null) {
            ExpertGroup tmp = expertGroupService.findById(initialRiskExpertise.getExpertID());
            if (tmp != null) {
                expertGroup = tmp;
            }
            int n = initialRiskExpertise.getRiskExpertise();
            switch (n) {
                case 1:
                    riskExpertise = "很低";
                case 2:
                    riskExpertise = "较低";
                case 3:
                    riskExpertise = "中等";
                case 4:
                    riskExpertise = "较高";
                case 5:
                    riskExpertise = "很高";
            }
            n = initialRiskExpertise.getIncidenceExpertise();
            switch (n) {
                case 1:
                    incidenceExpertise = "可忽略";
                case 2:
                    incidenceExpertise = "较小";
                case 3:
                    incidenceExpertise = "中等";
                case 4:
                    incidenceExpertise = "较大";
                case 5:
                    incidenceExpertise = "严重";
            }
            riskProbability = initialRiskExpertise.getRiskProbability() + "%";
            incidenceProbability = initialRiskExpertise.getIncidenceProbability() + "%";
        }
        setAttr("project", project)
                .setAttr("managementName", managementName)
                .setAttr("expertGroup", expertGroup)
                .setAttr("initialRiskExpertise", initialRiskExpertise)
                .setAttr("riskProbability", riskProbability)
                .setAttr("riskExpertise", riskExpertise)
                .setAttr("incidenceProbability", incidenceProbability)
                .setAttr("incidenceExpertise", incidenceExpertise)
                .render("riskExpertise.html");
    }

    /**
     * 项目风险因素影响程度及概率
     * type
     * 0： 初始风险
     * 1： 採取措施后的风险
     */
    @NotNullPara({"projectID", "type"})
    public void toRiskExpertise() {
        Long projectId = getParaToLong("projectID");
        int type = getParaToInt("type");
        // 获取到当前行的id
        Long id = getParaToLong("id");
        // 设置必要的标识
        setAttr("flag", getPara("flag", "false"))
                .setAttr("project", projectService.findById(projectId))
                .setAttr("projectId", projectId);
        // 如果为空，就是添加，否则为查看
        if (id == null) {//根据当前登录用户所属组织查询所有专家
            //setAttr("expertGroups", expertGroupService.findAll());
            setAttr("expertGroups", expertGroupService.findAllByOrgId(AuthUtils.getLoginUser().getUserID()));
            if(type == 0){
                render("initialRiskExpertise.html");
            }else if(type == 1){
                render("resultRiskExpertise.html");
            }
        } else {
            InitialRiskExpertise initialRiskExpertise = initialRiskExpertiseService.findById(id);
            ExpertGroup expertGroup = expertGroupService.findById(initialRiskExpertise.getExpertID());
            setAttr("initialRiskExpertise", initialRiskExpertise).setAttr("expertGroup", expertGroup);
            if(type == 0){
                render("initialRiskExpertise.html");
            }else if(type == 1){
                render("resultRiskExpertise.html");
            }
        }
    }

    /**
     * 表格数据
     */
    public void toInitialRiskExpertiseDataTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long projectId = getParaToLong("id");
        InitialRiskExpertise initialRiskExpertise = new InitialRiskExpertise();
        initialRiskExpertise.setProjectID(projectId);
        Page<InitialRiskExpertise> pages = initialRiskExpertiseService.findPage(initialRiskExpertise, pageNumber, pageSize);
        pages.getList().forEach(model -> {
            ExpertGroup expertGroup = expertGroupService.findById(model.getExpertID());
            if (expertGroup != null) {
                model.setRemark("专家名称：" + expertGroup.getName());
            }
        });
        renderJson(new DataTable<InitialRiskExpertise>(pages));
    }

    /**
     * 风险因素影响程度数据表格
     */
    public void chooseRiskExpertiseDataTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long projectId = getParaToLong("id");
        InitialRiskExpertise initialRiskExpertise = new InitialRiskExpertise();
        initialRiskExpertise.setProjectID(projectId);
        initialRiskExpertise.setIsEnable(true);
        Page<InitialRiskExpertise> page = initialRiskExpertiseService.findPage(initialRiskExpertise, pageNumber, pageSize);
        page.getList().forEach(p -> {
            StringBuilder sb = new StringBuilder();
            if (p.getRemark().equals("initial")) {
                sb.append("初始风险程度");
                p.setRemark("初始风险程度");
            } else if (p.getRemark().equals("result")) {
                sb.append("採取措施后的风险程度");
                p.setRemark("採取措施后的风险程度");
            }
            p.setName("风险因素及风险程度");
        });
        renderJson(new DataTable<InitialRiskExpertise>(page));
    }

    /**
     * 项目风险因素影响程度及概率数据提交
     * type
     * 0： 初始风险
     * 1： 採取措施后的风险
     */
    @Before(POST.class)
    @NotNullPara({"projectId", "expertId", "type"})
    public void initialRiskExpertise() {
        User user = AuthUtils.getLoginUser();
        int type = getParaToInt("type");
        InitialRiskExpertise model = new InitialRiskExpertise();
        ExpertGroup expertGroup = expertGroupService.findById(getParaToLong("expertId"));
        if (expertGroup == null) {
            throw new BusinessException("专家团体不存在！");
        }
        model.setProjectID(getParaToLong("projectId"));
        model.setExpertID(expertGroup.getId());
        model.setIncidenceExpertise(getParaToInt("incidenceExpertise"));
        model.setRiskExpertise(getParaToInt("riskExpertise"));
        if (getPara("riskProbability") != null && getPara("incidenceProbability") != null) {
            model.setIncidenceProbability(Float.parseFloat(getPara("incidenceProbability")));
            model.setRiskProbability(Float.parseFloat(getPara("riskProbability")));
            model.setRiskLevel(Float.parseFloat(getPara("incidenceProbability")) * Float.parseFloat(getPara("riskProbability")));
        }
        model.setRiskFactor(getPara("riskFactor"));
        model.setCreateUserID(user.getId());
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setLastUpdateUserID(user.getId());
        model.setStatus(3);
        if (type == 0) {
            model.setRemark("initial");
        } else {
            model.setRemark("result");
        }
        model.setIsEnable(true);
        if (!initialRiskExpertiseService.save(model)) {
            renderJson(RestResult.buildError("保存失败"));
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 项目风险因素影响程度及概率数据删除
     */
    @NotNullPara("id")
    public void chooseRiskExpertiseDelete() {
        Long id = getParaToLong("id");
        InitialRiskExpertise model = initialRiskExpertiseService.findById(id);
        if (model == null || !initialRiskExpertiseService.delete(model)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 6. 风险等级综合评估
     */
    public void toRiskLevel() {
        String riskLevels = projectService.findById(getParaToLong("id")).getRiskLevels();
        if (StringUtils.isEmpty(riskLevels)) {
            riskLevels = "";
        }
        setAttr("projectID", getParaToLong("id"))
                .setAttr("percent", getParaToLong("percent"))
                .setAttr("flag", getPara("flag", "false"))
                .setAttr("riskLevels", riskLevels)
                .render("riskLevel.html");
    }

    @NotNullPara({"projectId","riskLevels"})
    public void updateRiskLevels(){
        Integer projectId = getParaToInt("projectId");
        Project project = projectService.findById(projectId);
        project.setRiskLevels(getPara("riskLevels",project.getRiskLevels()));
        if (!projectService.update(project)){
            throw new BusinessException("更新失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
