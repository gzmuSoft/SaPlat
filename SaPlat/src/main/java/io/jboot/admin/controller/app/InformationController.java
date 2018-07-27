package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.json.JFinalJson;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ResultCode;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.ProjectUndertakeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.RequiresRoles;

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
    private InformationFillService informationFillService;

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
    /**
     * 项目信息页面
     */
    public void index(){
        render("projects.html");
    }

    /**
     * 项目信息查看，查看当前已经加入的项目
     */

    public void tableData(){
        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
        List<ImpTeam> impTeams = impTeamService.findByExpertGroup(expertGroup);
        List<Object> ids = Collections.synchronizedList(new ArrayList<Object>());
        impTeams.forEach(impTeam -> ids.add(impTeam.getProjectID()));
        List<Project> projects = projectService.findByIds(ids);
        Map<String,Object> res = new ConcurrentHashMap<>();
        res.put("code", ResultCode.SUCCESS);
        res.put("data", projects);
        res.put("msg","请求成功");
        renderJson(res);
    }

    /**
     * 资料编辑页面
     */
    @NotNullPara("id")
    public void edit(){
        Long id = getParaToLong("id");
        setAttr("projectId",id);
        render("edit.html");
    }


    /**
     * 资料填写，加载当前可以填写的项目
     */
    public void editData(){
        Long id = getParaToLong("id");
        Project project = projectService.findById(id);
        if (project == null){
            renderJson(RestResult.buildError("项目不存在"));
            throw new BusinessException("项目不存在");
        }
        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
        ImpTeam impTeam = impTeamService.findByProjectId(project.getId());
        if (!impTeam.getExpertGroupIDs().contains(String.valueOf(expertGroup.getId()))){
            renderJson(RestResult.buildError("关系不对应"));
            throw new BusinessException("关系不对应");
        }
//        List<InformationFill> informationFills = informationFillService.findByRoleId(roleService.findByName("专家团体").getId());
        List<InformationFill> informationFills = informationFillService.findAll();
        Map<String,Object> res = new ConcurrentHashMap<>();
        res.put("list",informationFills);
        res.put("code",ResultCode.SUCCESS);
        res.put("projectId",project.getId());
        renderJson(res);

//        List<ImpTeam> impTeams = impTeamService.findByExpertGroup(expertGroup);
//        List<Object> ids = Collections.synchronizedList(new ArrayList<Object>());
//        impTeams.forEach(impTeam -> ids.add(impTeam.getProjectID()));
//        List<Project> projects = projectService.findByIds(ids);
//        setAttr("projects",projects);

    }


    /**
     * 现场踏勘专家意见
     */
    @RequiresRoles("专家团体")
    @NotNullPara("id")
    public void expertAdvice(){
        Long id = getParaToLong("id");
        Project project = projectService.findById(id);
        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());

        SiteSurveyExpertAdvice model = siteSurveyExpertAdviceService.findByColumn("expertID", expertGroup.getId().toString(), Column.LOGIC_EQUALS);
        if (model == null){
            model = new SiteSurveyExpertAdvice();
        }
        String assessmentMode = project.getAssessmentMode();
        String name = null;
        if ("自评".equals(assessmentMode)){
            Organization organization = organizationService.findById(userService.findById(project.getUserId()).getUserID());
            name = organization.getName();
        } else {
            ProjectUndertake projectUndertake = projectUndertakeService.findByProjectId(id);
            //项目为承接时
            if (!ProjectUndertakeStatus.UNDERTAKE.equals(projectUndertake.getStatus().toString())){
                renderJson(RestResult.buildError("項目还不能填写资料"));
                throw new BusinessException("項目还不能填写资料");
            }
            FacAgency facAgency = facAgencyService.findById(projectUndertake.getFacAgencyID());
            name = facAgency.getName();
        }
        AffectedGroup affectedGroup = affectedGroupService.findById(expertGroup.getAffectedGroupID());
        Occupation occupation = occupationService.findById(affectedGroup.getOccupationID());
        Post post = postService.findById(affectedGroup.getPersonID());
        String date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        setAttr("expertGroup",expertGroup)
                .setAttr("phone",user.getPhone())
                .setAttr("projectID",project.getId())
                .setAttr("occupation",occupation.getName())
                .setAttr("name",name)
                .setAttr("post",post)
                .setAttr("date",date)
                .setAttr("model",model)
                .setAttr("otherComments",model.getOtherComments())
                .setAttr("resolving",model.getResolving())
                .setAttr("riskFactor",model.getRiskFactor())
                .render("expertAdvice.html");
    }

    /**
     * 现场踏勘专家意见提交
     */
    @Before(POST.class)
    @NotNullPara("projectID")
    public void expertAdvicePost(){
        Long id = getParaToLong();
        String riskFactor = getPara("riskFactor");
        String resolving = getPara("resolving");
        String content = getPara("content");

        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
        SiteSurveyExpertAdvice model = siteSurveyExpertAdviceService.findByColumns(new String[]{"projectID", "expertID"}, new Object[]{id, expertGroup.getId()});
        if (model != null){
            renderJson(RestResult.buildError("您已经填写过此资料"));
            throw new BusinessException("您已经填写过此资料");
        }
        model = new SiteSurveyExpertAdvice();
        model.setProjectID(id);
        model.setExpertID(expertGroup.getId());
        model.setCreateTime(new Date());
        model.setIsEnable(true);
        model.setLastAccessTime(new Date());
        model.setLastUpdateUserID(user.getId());
        model.setCreateUserID(user.getId());
        model.setRiskFactor(riskFactor);
        model.setResolving(resolving);
        model.setOtherComments(content);
        model.setSort(1);
        if (!siteSurveyExpertAdviceService.save(model)){
            renderJson(RestResult.buildError("啊哦，保存失败，请重新尝试！"));
            throw new BusinessException("啊哦，保存失败，请重新尝试！");
        }
        renderJson(RestResult.buildSuccess());
    }
}
