package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.json.JFinalJson;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ResultCode;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.ProjectStatus;
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

    /**
     * 资料编辑页面
     */
    @NotNullPara("id")
    public void edit() {
        Long id = getParaToLong("id");
        setAttr("projectId", id);
        render("edit.html");
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

    public void list() {
        String url = getPara("url");
        Long projectId = getParaToLong("id");
        setAttr("url", url)
                .setAttr("projectId", projectId)
                .render("tableView.html");
    }

    public void expertAdviceDataTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long projectId = getParaToLong("id");
        SiteSurveyExpertAdvice model = new SiteSurveyExpertAdvice();
        model.setProjectID(projectId);
        Page<SiteSurveyExpertAdvice> page = siteSurveyExpertAdviceService.findPage(model, pageNumber, pageSize);
        page.getList().forEach(p -> p.setRemark("专家：" + expertGroupService.findById(p.getExpertID()).getName()));
//        if (page.getList().size() > 0) {
//            page.getList().forEach(p -> {
//            });
//        }
        renderJson(new DataTable<SiteSurveyExpertAdvice>(page));
    }

    /**
     * 现场踏勘专家意见
     */
    @NotNullPara("id")
    public void expertAdvice() {
        Long id = getParaToLong("id");
        Long expertID = getParaToLong("expertID");
        Project project = projectService.findById(id);
        User user = AuthUtils.getLoginUser();
        List<ExpertGroup> expertGroups = expertGroupService.findAll();
        String assessmentMode = project.getAssessmentMode();
        String name = null;
        if ("自评".equals(assessmentMode)) {
            Organization organization = organizationService.findById(userService.findById(project.getUserId()).getUserID());
            name = organization.getName();
        } else {
            ProjectUndertake projectUndertake = projectUndertakeService.findByProjectId(id);
            //项目为承接时
            if (!ProjectUndertakeStatus.UNDERTAKE.equals(projectUndertake.getStatus().toString())) {
                renderJson(RestResult.buildError("項目还不能填写资料"));
                throw new BusinessException("項目还不能填写资料");
            }
            FacAgency facAgency = facAgencyService.findById(projectUndertake.getFacAgencyID());
            name = facAgency.getName();
        }
        SiteSurveyExpertAdvice model = new SiteSurveyExpertAdvice();
        ExpertGroup expertGroup = null;
        if (expertID != null) {
            model = siteSurveyExpertAdviceService.findByColumns(new String[]{"projectID", "expertID"}, new String[]{id.toString(), expertID.toString()});
            expertGroup = expertGroupService.findById(expertID);
        }
        String date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        setAttr("expertGroups", expertGroups)
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
        Long id = getParaToLong("projectID");
        Long expertGroupId = getParaToLong("expertGroupId");
        String riskFactor = getPara("riskFactor");
        String resolving = getPara("resolving");
        String content = getPara("content");

        User user = AuthUtils.getLoginUser();
        SiteSurveyExpertAdvice model = new SiteSurveyExpertAdvice();
        model.setName("现场踏勘专家意见");
        model.setProjectID(id);
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
        if (!siteSurveyExpertAdviceService.save(model)) {
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
}
