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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
            if (facAgency == null){
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
        Files files = filesService.findById(fileProject.getFileID());
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
            if(projectUndertake.getApplyOrInvite()){
                FacAgency facAgency=facAgencyService.findById(projectUndertake.getFacAgencyID());
                organization = organizationService.findById(facAgency.getOrgID());
                user=userService.findByUserIdAndUserSource(organization.getId(),1);
            }
            else{
                user=userService.findById(projectUndertake.getCreateUserID());
                organization=organizationService.findById(user.getUserID());
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
            invTeamMap.put(invTeamID, person.getName());
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
        diagnoses.setStaffArrangements(StringUtils.join(idList, ","));
        String[] surveyWayList = getParaValues("surveyWays");
        diagnoses.setSurveyWay(StringUtils.join(surveyWayList, ","));
        diagnoses.setCreateUserID(AuthUtils.getLoginUser().getId());
        diagnoses.setLastUpdateUserID(AuthUtils.getLoginUser().getId());

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
        Long projectId = getParaToLong("id");
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setProjectID(projectId);
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
    @NotNullPara("projectId")
    public void personOrOrganization() {
        Project project = projectService.findById(getPara("projectId"));
        int contentsLength = 0;
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setType(getParaToInt("type"));
        setAttr("flag", getPara("flag", "false"));
        //判断跳转的页面是哪一个
        if (questionnaire.getType() == 0) {
            //加载民族
            Nation nmodel = new Nation();
            nmodel.setIsEnable(true);
            List<Nation> nations = nationService.findAll(nmodel);
            BaseStatus nationStatus = new BaseStatus() {
            };
            for (Nation nation : nations) {
                nationStatus.add(nation.getId().toString(), nation.getName());
            }
            //加载学历
            Educational emodel = new Educational();
            emodel.setIsEnable(true);
            List<Educational> educationals = educationalService.findAll(emodel);
            BaseStatus educationalStatus = new BaseStatus() {
            };
            for (Educational educational : educationals) {
                educationalStatus.add(educational.getId().toString(), educational.getName());
            }
            //加载职业
            Occupation omodel = new Occupation();
            omodel.setIsEnable(true);
            List<Occupation> occupations = occupationService.findAll(omodel);
            BaseStatus occupationStatus = new BaseStatus() {
            };
            for (Occupation item : occupations) {
                occupationStatus.add(item.getId().toString(), item.getName());
            }
            setAttr("contentsLength", contentsLength).
                    setAttr("questionnaire", questionnaire).
                    setAttr("project", project).
                    setAttr("occupationStatus", occupationStatus).
                    setAttr("educationalStatus", educationalStatus).
                    setAttr("nationStatus", nationStatus).
                    render("personMain.html");
        } else {
            setAttr("contentsLength", contentsLength).
                    setAttr("questionnaire", questionnaire).
                    setAttr("project", project).
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
        if (questionnaire == null) {
            setAttr("projectId", project.getId()).
                    render("personOrOrganization.html");
        } else {
            //加载内容Ids
            Long[] contentIds = questionnaireContentLinkService.findContentIdByQuestionnaireId(questionnaire.getId());
            QuestionnaireContent[] questionnaireContents = new QuestionnaireContent[contentIds.length];
            //内容条数
            int contentsLength = contentIds.length;
            //加载内容s
            for (int i = 0; i < contentIds.length; i++) {
                questionnaireContents[i] = questionnaireContentService.findById(contentIds[i]);
            }
            //判断跳转的页面是哪一个
            if (questionnaire.getType() == 0) {
                //加载民族
                Nation nmodel = new Nation();
                nmodel.setIsEnable(true);
                List<Nation> nations = nationService.findAll(nmodel);
                BaseStatus nationStatus = new BaseStatus() {
                };
                Nation nationName = null;
                for (Nation nation : nations) {
                    if (questionnaire.getNationID().equals(nation.getId())) {
                        nationName = nation;
                    }
                    nationStatus.add(nation.getId().toString(), nation.getName());
                }
                //加载学历
                Educational emodel = new Educational();
                emodel.setIsEnable(true);
                List<Educational> educationals = educationalService.findAll(emodel);
                BaseStatus educationalStatus = new BaseStatus() {
                };
                Educational educationalName = null;
                for (Educational educational : educationals) {
                    if (questionnaire.getDegreeOfEducationID().equals(educational.getId())) {
                        educationalName = educational;
                    }
                    educationalStatus.add(educational.getId().toString(), educational.getName());
                }
                //加载职业
                Occupation omodel = new Occupation();
                omodel.setIsEnable(true);
                List<Occupation> occupations = occupationService.findAll(omodel);
                BaseStatus occupationStatus = new BaseStatus() {
                };
                Occupation occupationName = null;
                for (Occupation item : occupations) {
                    if (questionnaire.getOccupationID().equals(item.getId())) {
                        occupationName = item;
                    }
                    occupationStatus.add(item.getId().toString(), item.getName());
                }
                setAttr("questionnaireContents", questionnaireContents).
                        setAttr("contentsLength", contentsLength).
                        setAttr("questionnaire", questionnaire).
                        setAttr("project", project).
                        setAttr("occupationStatus", occupationStatus).
                        setAttr("occupation", occupationName).
                        setAttr("educationalStatus", educationalStatus).
                        setAttr("educational", educationalName).
                        setAttr("nationStatus", nationStatus).
                        setAttr("nation", nationName).
                        render("personMain.html");
            } else {
                setAttr("questionnaireContents", questionnaireContents).
                        setAttr("contentsLength", contentsLength).
                        setAttr("questionnaire", questionnaire).
                        setAttr("project", project).
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
        Project project = getBean(Project.class, "project");
        project.setLastAccessTime(new Date());
        project.setLastUpdateUserID(loginUser.getId());
        //调查问卷
        Questionnaire questionnaire = getBean(Questionnaire.class, "questionnaire");
        //因bug手动获取
        questionnaire.setIDCard(getPara("questionnaire.IDCard"));
        //修改则有问卷id 创建则没有
        questionnaire.setLastAccessTime(new Date());
        if (questionnaire.getCreateTime() == null) {
            questionnaire.setCreateTime(new Date());
        }
        if (questionnaire.getProjectID() == null) {
            questionnaire.setProjectID(project.getId());
        }
        if (questionnaire.getCreateUserID() == null) {
            questionnaire.setCreateUserID(loginUser.getId());
        }
        //调查内容
        String[] questionnaireContentNames = getParaValues("name");
        String[] questionnaireContents = getParaValues("content");
        List<QuestionnaireContent> contents = new ArrayList<QuestionnaireContent>();
        int i = 0;
        for (String content : questionnaireContents) {
            QuestionnaireContent questionnaireContent = new QuestionnaireContent();
            questionnaireContent.setName(questionnaireContentNames[i]);
            questionnaireContent.setContent(content);
            questionnaireContent.setCreateTime(new Date());
            questionnaireContent.setLastAccessTime(new Date());
            questionnaireContent.setCreateUserID(loginUser.getId());
            contents.add(questionnaireContent);
            i++;
        }
        if (questionnaire.getId() != null) {
            if (!questionnaireService.updateQuestionnaire(questionnaire, contents, project)) {
                throw new BusinessException("修改失败!");
            }
        } else {
            if (!questionnaireService.saveQuestionnaire(questionnaire, contents, project)) {
                throw new BusinessException("保存失败!");
            }
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 删除调查问卷以及内容
     */
    @NotNullPara("id")
    public void questionnaireDelete() {
        Questionnaire questionnaire = questionnaireService.findById(getParaToLong("id"));
        Long[] contentIds, linkIds;
        if (questionnaire != null) {
            contentIds = questionnaireContentLinkService.findContentIdByQuestionnaireId(questionnaire.getId());
            linkIds = new Long[contentIds.length];
            for (int i = 0; i < contentIds.length; i++) {
                linkIds[i] = questionnaireContentLinkService.findIdByContentId(contentIds[i]);
            }
            if (!questionnaireService.deleteQuestionnaire(questionnaire.getId(), contentIds, linkIds)) {
                throw new BusinessException("删除失败!");
            }
        } else {
            throw new BusinessException("删除的数据不存在!");
        }
        renderJson(RestResult.buildSuccess());
    }


    /**
     * 临时-项目风险因素影响程度及概率
     */
    @NotNullPara("projectID")
    public void toInitialRiskExpertise() {
        Long projectId = getParaToLong("projectID");
        // 获取到当前行的id
        Long id = getParaToLong("id");
        // 设置必要的标识
        setAttr("flag", getPara("flag", "false"))
                .setAttr("project", projectService.findById(projectId))
                .setAttr("projectId", projectId);
        // 如果为空，就是添加，否则为查看
        if (id == null) {
            setAttr("expertGroups", expertGroupService.findAll())
                    .render("initialRiskExpertise.html");
        } else {
            InitialRiskExpertise initialRiskExpertise = initialRiskExpertiseService.findById(id);
            ExpertGroup expertGroup = expertGroupService.findById(initialRiskExpertise.getExpertID());
            setAttr("initialRiskExpertise", initialRiskExpertise)
                    .setAttr("expertGroup", expertGroup)
                    .render("initialRiskExpertiseSee.html");
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
     * 项目风险因素影响程度及概率数据提交
     */
    @Before(POST.class)
    @NotNullPara({"projectId", "expertId"})
    public void initialRiskExpertise() {
        User user = AuthUtils.getLoginUser();
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
            model.setIncidenceProbability((float) getParaToLong("incidenceProbability"));
            model.setRiskProbability((float) getParaToLong("riskProbability"));
            model.setRiskLevel((float) getParaToLong("incidenceProbability") * (float) getParaToLong("riskProbability"));
        }
        model.setRiskFactor(getPara("riskFactor"));
        model.setCreateUserID(user.getId());
        model.setCreateTime(new Date());
        model.setLastAccessTime(new Date());
        model.setLastUpdateUserID(user.getId());
        model.setStatus(3);
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
    public void toInitialRiskExpertiseDelete() {
        Long id = getParaToLong("id");
        InitialRiskExpertise model = initialRiskExpertiseService.findById(id);
        if (model == null || initialRiskExpertiseService.delete(model)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

}
