package io.jboot.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
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
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import java.util.*;


@RequestMapping("/app/projectUndertake")
public class ProjectUndertakeController extends BaseController {

    @JbootrpcService
    private ProjectService projectService;
    @JbootrpcService
    private ProjectAssTypeService projectAssTypeService;
    @JbootrpcService
    private ProjectUndertakeService projectUndertakeService;
    @JbootrpcService
    private FacAgencyService facAgencyService;
    @JbootrpcService
    private OrganizationService organizationService;
    @JbootrpcService
    private StructPersonLinkService structPersonLinkService;
    @JbootrpcService
    private PersonService personService;
    @JbootrpcService
    private OrgStructureService orgStructureService;
    @JbootrpcService
    private LeaderGroupService leaderGroupService;
    @JbootrpcService
    private ExpertGroupService expertGroupService;
    @JbootrpcService
    private EvaSchemeService evaSchemeService;
    @JbootrpcService
    private ImpTeamService impTeamService;
    @JbootrpcService
    private ScheduledPlanService scheduledPlanService;
    @JbootrpcService
    private FileFormService fileFormService;
    @JbootrpcService
    private FilesService filesService;
    @JbootrpcService
    private UserService userService;
    @JbootrpcService
    private AuthProjectService authProjectService;
    @JbootrpcService
    private RoleService roleService;
    @JbootrpcService
    private ManagementService managementService;
    @JbootrpcService
    private NotificationService notificationService;

//    /**
//     * 去重
//     */
//    static String sub(String str) {
//        List list = new ArrayList();
//        StringBuffer sb = new StringBuffer(str);
//        int j = 0;
//        for (int i = 0; i < str.length(); i++) {
//            if (list.contains(str.charAt(i))) {
//                sb.deleteCharAt(i - j);
//                j++;
//            } else {
//                list.add(str.charAt(i));
//            }
//        }
//        return sb.toString();
//    }

    /**
     * 跳转榜单页面
     */
    public void toProjectList() {
        BaseStatus projectTypeStatus = new BaseStatus() {
        };
        ProjectAssType model = new ProjectAssType();
        model.setIsEnable(true);
        List<ProjectAssType> PaTypeList = projectAssTypeService.findAll(model);
        if (PaTypeList != null) {
            for (ProjectAssType item : PaTypeList) {
                projectTypeStatus.add(item.getId().toString(), item.getName());
            }
        }
        setAttr("PaTypeNameList", projectTypeStatus);
        render("projectList.html");
    }

    /**
     * 榜单显示已发布的项目
     */
    public void projectList() {
        User user = AuthUtils.getLoginUser();
        FacAgency facAgency = facAgencyService.findByOrgId(user.getUserID());//找到当前用户所属组织机构对应的服务机构信息
        if (facAgency != null) {
            int pageNumber = getParaToInt("pageNumber", 1);
            int pageSize = getParaToInt("pageSize", 30);
            Project project = new Project();
            if (StrKit.notBlank(getPara("name"))) {
                project.setName(getPara("name"));
            }
            if (StrKit.notBlank(getPara("projectType"))) {
                project.setPaTypeID(Long.parseLong(getPara("projectType")));
            }
            if (StrKit.notBlank(getPara("maxAmount"))) {
                project.setMaxAmount(Double.parseDouble(getPara("maxAmount")));
            }
            if (StrKit.notBlank(getPara("minAmount"))) {
                project.setMinAmount(Double.parseDouble(getPara("minAmount")));
            }
            project.setStatus(ProjectStatus.IS_VERIFY);
            project.setIsPublic(true);
            //获取不是当前用户发布的满足指定条件的项目榜单
            Page<Project> page = projectService.findPageByIsPublic(user.getId(), project, pageNumber, pageSize);
            if (page != null && page.getList().size() > 0) {
                page.getList().forEach(p -> {
                    //查找当前用户是否与当前项目有承接关系
                    ProjectUndertake projectUndertake = projectUndertakeService.findByProjectIdAndFacAgencyID(p.getId(), facAgency.getId());
                    if (projectUndertake != null) {
                        Integer status = projectUndertake.getStatus();
                        if (status == 0) {
                            p.setRemark("待确认");
                        } else if (status == 1) {
                            p.setRemark("已拒绝");
                        } else if (status == 2) {
                            p.setRemark("已同意");
                        } else if (status == 3) {
                            p.setRemark("已被承接");
                        }
                    } else {
                        p.setRemark("未承接");
                    }
                });
            }
            renderJson(new DataTable<Project>(page));
        } else {
            renderJson(new DataTable<Project>(null));
        }
    }

    /**
     * 查看项目详细信息
     */
    @NotNullPara({"id"})
    public void see() {
        Long id = getParaToLong("id");
        AuthProject apModel = authProjectService.findByProjectId(id);//获取项目的立项审核信息
        Project pModel = projectService.findById(id); //获取项目信息
        pModel.setTypeName(projectAssTypeService.findById(pModel.getPaTypeID()).getName());
        Organization organization = organizationService.findById(userService.findById(pModel.getUserId()).getUserID()); //获取组织信息
        String strRoleName = roleService.findById(apModel.getRoleId()).getName();
        setAttr("organization", organization)
                .setAttr("model", pModel)
                .setAttr("roleName", strRoleName)
                .render("see.html");

    }

    /**
     * 介入项目
     */
    @NotNullPara({"id"})
    @RequiresRoles(value = {"组织机构", "服务机构"}, logical = Logical.AND)
    public void agency() {
        Long id = getParaToLong("id");
        User user = AuthUtils.getLoginUser();
        Organization organization = organizationService.findById(user.getUserID());//找到组织机构信息
        FacAgency facAgency = null;
        if (organization != null) {
            facAgency = facAgencyService.findByOrgId(organization.getId());//找到组织机构对应的服务机构信息
        }

        //获取项目id为id，创建用户编号为当前用户编号的项目承接信息
        ProjectUndertake projectUndertake = projectUndertakeService.findByProjectIdAndFacAgencyID(id, facAgency.getId());
        //获取项目信息
        Project project = projectService.findById(id);
        if (projectUndertake != null) {
            if (projectUndertake.getApplyOrInvite()) {
                if (projectUndertake.getStatus() != Integer.parseInt(ProjectUndertakeStatus.REFUSE)) {
                    throw new BusinessException("您已经被邀请了，无需再申请！");
                }
            } else {
                if (projectUndertake.getStatus() != Integer.parseInt(ProjectUndertakeStatus.REFUSE)) {
                    throw new BusinessException("您已经申请过了，请不要重复申请！");
                }
            }
        } else {
            projectUndertake = new ProjectUndertake();
            projectUndertake.setCreateUserID(user.getId());
            projectUndertake.setCreateTime(new Date());
            projectUndertake.setProjectID(id);
            if (facAgency != null) {
                projectUndertake.setFacAgencyID(facAgency.getId());
            }
        }
        projectUndertake.setName(project.getName());
        projectUndertake.setDeadTime(project.getEndPublicTime());
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
        notification.setContent("您好，\"" + organization.getName() + "\"希望承接您的项目委评，请及时处理！");
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
     * 参数 flag ：
     * true    作为请求发起方
     * false   作为请求接受方
     */
    public void projectUndertakeList() {
        User user = AuthUtils.getLoginUser();
        Boolean applyOrInvite = getParaToBoolean("applyOrInvite");
        Boolean flag = getParaToBoolean("flag");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Page<ProjectUndertake> page = null;
        /*
        //注：邀请介入时，CreateUserID为创建项目的用户ID；申请介入时，CreateUserID为申请介入项目评估的服务机构对应用户ID
        if(applyOrInvite && flag) {//applyOrInvite: true, flag: true：查看邀请第三方介入的状态(已通过验证)
            // 此时，createUserID为创建项目的用户ID，可以通过createUserID来获取列表
            page = projectUndertakeService.findPage(projectUndertake, pageNumber, pageSize);
        }
        else if (applyOrInvite && !flag) {//applyOrInvite: true, flag: false：查看邀请您介入的项目(已通过验证)
            projectUndertake.setSort(0); // 此时createUserID为创建项目的用户ID，需要通过facAgencyID来获取列表
            FacAgency facAgency = facAgencyService.findByOrgId(user.getUserID());//找到当前用户所属组织机构对应的服务机构信息
            if (facAgency != null) {
                projectUndertake.setFacAgencyID(facAgency.getId());
            }
            page = projectUndertakeService.findPage(projectUndertake, pageNumber, pageSize);
        }
        else if(!applyOrInvite && flag){//applyOrInvite: false, flag: true：查看申请介入项目的状态(已通过验证)
            FacAgency facAgency = facAgencyService.findByOrgId(user.getUserID());//找到当前用户所属组织机构对应的服务机构信息
            if (facAgency != null) {
                projectUndertake.setFacAgencyID(facAgency.getId());
            }
            page = projectUndertakeService.findPage(projectUndertake, pageNumber, pageSize);
        }
        else if(!applyOrInvite && !flag){//applyOrInvite: false, flag: false：查看申请介入您项目的请求(已通过验证)
            //此时，CreateUserID为申请介入项目评估的服务机构对应用户ID，需要通过user.getUserID()获得其拥有的project表再获取申请介入当前用户所拥有项目的承接关联列表
            page = projectUndertakeService.findPageOfApplyIn(user.getId(), pageNumber, pageSize);
        }
        */

        if (!applyOrInvite && !flag) {//applyOrInvite: false, flag: false：查看申请介入您项目的请求(已通过验证)
            //此时，CreateUserID为申请介入项目评估的服务机构对应用户ID，需要通过user.getUserID()获得其拥有的project表再获取申请介入当前用户所拥有项目的承接关联列表
            page = projectUndertakeService.findPageOfApplyIn(user.getId(), pageNumber, pageSize);
        } else {
            ProjectUndertake projectUndertake = new ProjectUndertake();

            projectUndertake.setApplyOrInvite(applyOrInvite);
            projectUndertake.setIsEnable(true);
            if (applyOrInvite && flag) {
                //applyOrInvite: true, flag: true：查看邀请第三方介入的状态(已通过验证)，无需做特殊处理
                projectUndertake.setCreateUserID(user.getId());
            } else {
                //applyOrInvite: true, flag: false：查看邀请您介入的项目(已通过验证)
                //找到当前用户所属组织机构对应的服务机构信息
                FacAgency facAgency = facAgencyService.findByOrgId(user.getUserID());
                if (facAgency != null) {
                    projectUndertake.setFacAgencyID(facAgency.getId());
                }

                if (!applyOrInvite && flag) {
                    //applyOrInvite: false, flag: true：查看申请介入项目的状态，需额外设置用户id(已通过验证)
                    projectUndertake.setCreateUserID(user.getId());
                }
            }

            page = projectUndertakeService.findPage(projectUndertake, pageNumber, pageSize);
        }

        renderJson(new DataTable<ProjectUndertake>(page));
    }

    /**
     * 处理邀请/申请请求，
     * 参数 invite：
     * 1   拒绝
     * 2   同意
     * 参数 flag：
     * true    作为请求发起方
     * false   作为请求接受方
     * id：承接的 id
     * reply: 拒绝时回显
     */
    public void invite() {
        Integer invite = getParaToInt("invite");
        Boolean flag = getParaToBoolean("flag");
        Long id = getParaToLong("id");
        if (flag == null || invite == null || id == null || (!invite.equals(Integer.valueOf(ProjectUndertakeStatus.REFUSE))
                && !invite.equals(Integer.valueOf(ProjectUndertakeStatus.ACCEPT)))) {
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

        if (flag && invite.equals(Integer.valueOf(ProjectUndertakeStatus.REFUSE))) {
            reply = getPara("reply");
            notification.setName("邀请介入拒绝通知");
            notification.setContent(user.getName() + "已拒绝您的邀请！");
            projectUndertake.setStatus(Integer.valueOf(ProjectUndertakeStatus.REFUSE));
        } else if (!flag && invite.equals(Integer.valueOf(ProjectUndertakeStatus.REFUSE))) {
            reply = getPara("reply");
            notification.setName("申请介入拒绝通知");
            notification.setContent(user.getName() + "已拒绝您的申请！");
            projectUndertake.setStatus(Integer.valueOf(ProjectUndertakeStatus.REFUSE));
        }

        if (flag && invite.equals(Integer.valueOf(ProjectUndertakeStatus.ACCEPT))) {
            notification.setName("邀请介入同意通知");
            notification.setContent(user.getName() + "已接受您的邀请！");
            projectUndertake.setStatus(Integer.valueOf(ProjectUndertakeStatus.ACCEPT));
            Project project = projectService.findById(projectUndertake.getProjectID());
            project.setStatus(ProjectStatus.REVIEW);
            if (!projectService.update(project)) {
                throw new BusinessException("请求错误");
            }
        } else if (!flag && invite.equals(Integer.valueOf(ProjectUndertakeStatus.ACCEPT))) {
            notification.setName("申请介入同意通知");
            notification.setContent(user.getName() + "已接受您的申请！");
            projectUndertake.setStatus(Integer.valueOf(ProjectUndertakeStatus.ACCEPT));
            Project project = projectService.findById(projectUndertake.getProjectID());
            project.setStatus(ProjectStatus.REVIEW);
            if (!projectService.update(project)) {
                throw new BusinessException("请求错误");
            }
        }

        projectUndertake.setReply(reply);
        projectUndertake.setLastUpdateUserID(user.getId());
        projectUndertake.setLastAccessTime(new Date());

        Long projectID = projectUndertake.getProjectID();
        Long receiverID;
        if (flag) {
            receiverID = projectService.findById(projectID).getUserId();
        } else {
            receiverID = projectUndertake.getFacAgencyID();
        }

        notification.setSource("/app/projectUndertake/invite");
        notification.setRecModule("");
        notification.setReceiverID(Math.toIntExact(receiverID));
        notification.setCreateUserID(user.getId());
        notification.setCreateTime(new Date());
        notification.setLastUpdateUserID(user.getId());
        notification.setLastAccessTime(new Date());
        notification.setIsEnable(true);
        notification.setStatus(0);

        if (!projectUndertakeService.saveOrUpdateAndSend(projectUndertake, notification)) {
            renderJson(RestResult.buildError("请求失败，请重新尝试！"));
            throw new BusinessException("请求失败，请重新尝试！");
        }
        List<ProjectUndertake> list = projectUndertakeService.findListByProjectAndStatus(projectID, ProjectUndertakeStatus.WAITING);
        for (ProjectUndertake model : list) {
            model.setStatus(Integer.valueOf(ProjectUndertakeStatus.UNDERTAKE));
            if (!projectUndertakeService.update(model)) {
                throw new BusinessException("更新数据库的其他请求变为已承接状态失败！");
            }
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 稳评方案初始页面
     */
    @NotNullPara("id")
    public void toProjectImpTeam() {
        Long id = getParaToLong("id");
        StringBuilder string = new StringBuilder();
        EvaScheme evaScheme = evaSchemeService.findByProjectID(id);
        if (evaScheme != null) {
            setAttr("status", evaScheme.getStatus());
        }
        Project project = projectService.findById(id);//点击的当前项目
        LeaderGroup leaderGroup = leaderGroupService.findByProjectID(id);
        User user = AuthUtils.getLoginUser();
        Organization org = organizationService.findById(user.getUserID());
//        List<OrgStructure> orgStructures = orgStructureService.findByOrgIdAndType(org.getId(),2);
        List<OrgStructure> orgStructures = orgStructureService.findByOrgId(org.getId());
//        for (StructPersonLink structPersonLink : structPersonLinks) {
//            string.append(structPersonLink.getStructID());
//        }
//        for (int i = 0; i < sub(string.toString()).length(); i++) {
//            orgStructures.add(orgStructureService.findById(Character.getNumericValue(sub(string.toString()).charAt(i))));
//        }
        if (leaderGroup == null) {
            leaderGroup = new LeaderGroup();
        }
        setAttr("leaderGroup", leaderGroup).setAttr("project", project).setAttr("orgStructures", orgStructures).render("projectImpTeam.html");
    }

    /**
     * 下拉框二级联动
     */
    @Before(GET.class)
    public void persons() {
        List<StructPersonLink> structPersonLinks = structPersonLinkService.findByStructId(getParaToLong("orgStructureId"));
        List<ExpertGroup> expertGroups = new ArrayList<>();
        List<Person> persons = new ArrayList<>();
        boolean isGetExpert = getParaToBoolean("flag");
        for (StructPersonLink structPersonLink : structPersonLinks) {
            Person person = personService.findById(structPersonLink.getPersonID());
            if (false == isGetExpert) {
                persons.add(person);
            } else {
                ExpertGroup expertGroupModel = expertGroupService.findByPersonId(person.getId());
                if (expertGroupModel != null) {
                    persons.add(person);
                }
            }
        }
        JSONObject json = new JSONObject();
        json.put("persons", persons);
//        if (getParaToBoolean("flag")) {
//            ExpertGroup expertGroupModel;
//            for (int i = 0; i < persons.size(); i++) {
//                expertGroupModel = expertGroupService.findByPersonId(persons.get(i).getId());
//                if (expertGroupModel != null) {
//                    expertGroups.add(expertGroupModel);
//                }
//            }
//            json.put("expertGroups", expertGroups);
//        }
        renderJson(json);
    }

    /**
     * 稳评方案提交资料
     */
    @Before(POST.class)
    @NotNullPara("id")
    public void ImpTeam() {
        User loginUser = AuthUtils.getLoginUser();
        Long id = getParaToLong("id");

        ImpTeam impTeam = getBean(ImpTeam.class, "impTeam");
        impTeam.setName(impTeam.getName() + "  评估实施小组");
        impTeam.setProjectID(id);
        impTeam.setCreateTime(new Date());
        impTeam.setLastAccessTime(new Date());
        impTeam.setCreateUserID(loginUser.getId());
        impTeam.setLastUpdateUserID(loginUser.getId());

        EvaScheme evaScheme = getBean(EvaScheme.class, "EvaScheme");
        evaScheme.setProjectID(id);
        evaScheme.setCreateTime(new Date());
        evaScheme.setLastAccessTime(new Date());
        evaScheme.setCreateUserID(loginUser.getId());
        evaScheme.setLastUpdateUserID(loginUser.getId());
        evaScheme.setStatus("1");

        Gson gson = new Gson();
        ScheduledPlan scheduledPlan;
        String[] sName = gson.fromJson(getPara("ScheduledPlan.name"), String[].class);
        String[] sStartDate = gson.fromJson(getPara("ScheduledPlan.startDate"), String[].class);
        String[] sEndDate = gson.fromJson(getPara("ScheduledPlan.endDate"), String[].class);
        String[] sContent = gson.fromJson(getPara("ScheduledPlan.content"), String[].class);
        Long fileId1 = getParaToLong("fileId1"), fileId2 = getParaToLong("fileId2"), fileId3 = getParaToLong("fileId3");
        Files files1 = filesService.findById(fileId1), files2 = filesService.findById(fileId2), files3 = filesService.findById(fileId3);
        if(files1!=null){
            files1.setIsEnable(true);
        }
        if(files2!=null){
            files2.setIsEnable(true);
        }
        if(files3!=null){
            files3.setIsEnable(true);
        }
        FileForm fileForm1 = fileFormService.findById(getParaToLong("fileFormId1"));
        FileForm fileForm3 = fileFormService.findById(getParaToLong("fileFormId3"));
        String file2Id = getPara("fileFormId2");
        FileForm fileForm2 = null;
        if (file2Id != null) {
            fileForm2 = fileFormService.findById(file2Id);
        }
        List<ScheduledPlan> scheduledPlans = new ArrayList<>();
        for (int i = 0; i < sName.length; i++) {
            scheduledPlan = new ScheduledPlan();
            scheduledPlan.setName(sName[i]);
            scheduledPlan.setStartDate(java.sql.Date.valueOf(sStartDate[i]));
            scheduledPlan.setEndDate(java.sql.Date.valueOf(sEndDate[i]));
            scheduledPlan.setContent(sContent[i]);
            scheduledPlan.setCreateTime(new Date());
            scheduledPlan.setLastAccessTime(new Date());
            scheduledPlan.setCreateUserID(loginUser.getId());
            scheduledPlan.setLastUpdateUserID(loginUser.getId());
            scheduledPlans.add(scheduledPlan);
        }
        if (getPara("status").equals("3")) {
            impTeam.setId(impTeamService.findByProjectId(id).getId());
            evaScheme.setId(evaSchemeService.findByProjectID(id).getId());
            if (impTeamService.update(impTeam, evaScheme, scheduledPlans, fileForm1, fileForm2, fileForm3, files1, files2, files3)) {
                renderJson(RestResult.buildSuccess("更新成功"));
            } else {
                renderJson(RestResult.buildError("更新失败"));
                throw new BusinessException("更新失败");
            }
        } else if (impTeamService.save(impTeam, evaScheme, scheduledPlans, fileForm1, fileForm2, fileForm3, files1, files2, files3)) {
            renderJson(RestResult.buildSuccess("保存成功"));
        } else {
            renderJson(RestResult.buildError("保存失败"));
            throw new BusinessException("保存失败");
        }
    }

    /**
     * 稳评表资料上传
     */
    @NotNullPara({"fileId", "fieldName"})
    public void upFile() {
        User user = AuthUtils.getLoginUser();
        Files files = new Files();
        files.setId(getParaToLong("fileId"));
        files.setIsEnable(true);

        FileForm fileForm = new FileForm();
        fileForm.setFileID(getParaToLong("fileId"));
        fileForm.setTableName("eva_scheme");
        fileForm.setFieldName(getPara("fieldName"));
        fileForm.setStatus(false);
        fileForm.setCreateTime(new Date());
        fileForm.setLastAccessTime(new Date());
        fileForm.setCreateUserID(user.getId());
        fileForm.setLastUpdateUserID(user.getId());
        FileForm newFileForm = fileFormService.saveAndGet(fileForm, files);
        if (newFileForm == null) {
            renderJson(RestResult.buildError("保存失败"));
            throw new BusinessException("保存失败");
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            //传递消息实体 data
            map.put("fileFormId", newFileForm.getId());
            renderJson(RestResult.buildSuccess(map));
        }
    }


    /**
     * 管理机构对前期资料进行审核页面跳转
     */
    @RequiresRoles("管理机构")
    public void managementReview() {
        render("managementReview.html");
    }

    /**
     * 管理机构对前期资料进行审核创建
     */
    @RequiresRoles("管理机构")
    public void managementReviewTableData() {
        User user = AuthUtils.getLoginUser();
        Management management = managementService.findByOrgId(user.getUserID());
        if (management == null || !management.getIsEnable()) {
            renderJson(new DataTable<Project>(null));
            return;
        }
        Long managementID = management.getId();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setStatus(ProjectStatus.REVIEW);
        project.setManagementID(managementID);
        project.setIsEnable(true);
        Page<Project> page = projectService.findPage(project, pageNumber, pageSize);
        if (page.getList() != null) {
            page.getList().forEach(p -> {
                EvaScheme evaScheme = evaSchemeService.findByProjectID(p.getId());
                if (evaScheme != null) {
                    if ("1".equals(evaScheme.getStatus())) {
                        p.setRemark("待审核");
                        p.setSpell("3");
                    } else if ("2".equals(evaScheme.getStatus())) {
                        p.setRemark("审核通过");
                        p.setSpell("2");
                    } else if ("3".equals(evaScheme.getStatus())) {
                        p.setRemark("审核不通过");
                        p.setSpell("1");
                    } else {
                        p.setRemark("当前项目未上传前期资料");
                        p.setSpell("0");
                    }
                } else {
                    p.setRemark("当前项目未上传前期资料");
                    p.setSpell("0");
                }

            });
        }
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 管理就够对前期资料查看
     */
    @RequiresRoles("管理机构")
    @NotNullPara("id")
    public void managementReviewSee() {
        Long id = getParaToLong("id");
        Project project = projectService.findById(id);
        ImpTeam impTeam = impTeamService.findByProjectId(id);
        if (impTeam == null) {
            throw new BusinessException("当前项目未上传前期资料");
        }
        EvaScheme evaScheme = evaSchemeService.findByProjectID(id);
        if (evaScheme == null) {
            throw new BusinessException("当前项目未上传前期资料");
        }
        ScheduledPlan scheduledPlan = scheduledPlanService.findByEvaSchemeID(evaScheme.getId());
        FileForm fileForm1 = fileFormService.findFirstByTableNameAndRecordIDAndFileName("eva_Scheme", "\uE67C委托书", evaScheme.getId());
        FileForm fileForm2 = fileFormService.findFirstByTableNameAndRecordIDAndFileName("eva_Scheme", "\uE67C稳评方案封面", evaScheme.getId());
        FileForm fileForm3 = fileFormService.findFirstByTableNameAndRecordIDAndFileName("eva_Scheme", "\uE67C报备登记表", evaScheme.getId());

        if (fileForm1 != null && fileForm1.getFileID() != null) {
            setAttr("file1src", fileForm1.getFileID());
        }
        if (fileForm2 != null && fileForm2.getFileID() != null) {
            setAttr("file2src", fileForm2.getFileID());
        }
        if (fileForm3 != null && fileForm3.getFileID() != null) {
            setAttr("file3src", fileForm3.getFileID());
        }

        LeaderGroup leaderGroup = leaderGroupService.findByProjectID(project.getId());
        Person leader = personService.findById(impTeam.getLeaderID());
        String[] asLeaderIds = impTeam.getAssLeaderIDs().split(",");
        List<Person> asLeaderGroups = Collections.synchronizedList(new ArrayList<Person>());
        for (String asLeaderId : asLeaderIds) {
            if (!"".equals(asLeaderId.trim())) {
                asLeaderGroups.add(personService.findById(asLeaderId));
            }
        }
        String[] expertIds = impTeam.getExpertGroupIDs().split(",");
        List<Person> expertGroups = Collections.synchronizedList(new ArrayList<Person>());
        for (String expertId : expertIds) {
            if (!"".equals(expertId.trim())) {
                expertGroups.add(personService.findById(expertId));
            }
        }
        String[] invTeamIds = impTeam.getInvTeamIDs().split(",");
        List<Person> invTeams = Collections.synchronizedList(new ArrayList<Person>());
        for (String invTeamId : invTeamIds) {
            if (!"".equals(invTeamId.trim())) {
                invTeams.add(personService.findById(invTeamId));
            }
        }
        String[] repTeamIds = impTeam.getRepTeamIDs().split(",");
        List<Person> repTeams = Collections.synchronizedList(new ArrayList<Person>());
        for (String repTeamId : repTeamIds) {
            if (!"".equals(repTeamId.trim())) {
                repTeams.add(personService.findById(repTeamId));
            }
        }

        List<ScheduledPlan> scheduledPlans = scheduledPlanService.findListByEvaSchemeID(evaScheme.getId());

        setAttr("impTeam", impTeam)
                .setAttr("asLeaderGroups", asLeaderGroups)
                .setAttr("expertGroups", expertGroups)
                .setAttr("invTeams", invTeams)
                .setAttr("leader", leader)
                .setAttr("repTeams", repTeams)
                .setAttr("scheduledPlans", scheduledPlans)
                .setAttr("leaderGroup", leaderGroup)
                .setAttr("project", project)
                .setAttr("evaScheme", evaScheme)
                .setAttr("scheduledPlan", scheduledPlan)
                .render("projectImpTeamSee.html");
    }

    /**
     * 管理前期资料添加
     */
    @RequiresRoles("管理机构")
    @NotNullPara("id")
    public void managementReviewAccept() {
        Long id = getParaToLong("id");
        EvaScheme evaScheme = evaSchemeService.findByProjectID(id);
        evaScheme.setStatus("2");
        Project project = projectService.findById(id);
        if (!evaSchemeService.update(evaScheme)) {
            throw new BusinessException("更新失败");
        }else{
            User user = AuthUtils.getLoginUser();//获得当前登录用户信息
            Notification notification = new Notification();
            notification.setName("评估前期资料审核完成");
            notification.setSource("/app/projectUndertake/managementReviewAccept");
            notification.setContent("您好!您的《"+project.getName()+"》评估前期资料审核已通过!");
            notification.setCreateUserID(user.getId());
            notification.setLastUpdateUserID(user.getId());
            notification.setIsEnable(true);
            notification.setStatus(0);
            notification.setReceiverID(project.getCreateUserID().intValue());

            notificationService.save(notification);
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 管理前期资料拒绝
     */
    @RequiresRoles("管理机构")
    @NotNullPara("id")
    public void managementReviewRefuse() {
        Long id = getParaToLong("id");
        EvaScheme evaScheme = evaSchemeService.findByProjectID(id);
        evaScheme.setStatus("3");
        Project project = projectService.findById(id);
        if (!evaSchemeService.update(evaScheme)) {
            throw new BusinessException("更新失败");
        }else {
            User user = AuthUtils.getLoginUser();//获得当前登录用户信息
            Notification notification = new Notification();
            notification.setName("评估前期资料审核完成");
            notification.setSource("/app/projectUndertake/managementReviewAccept");
            notification.setContent("您好!您的《"+project.getName()+"》评估前期资料审核未通过!");
            notification.setCreateUserID(user.getId());
            notification.setLastUpdateUserID(user.getId());
            notification.setIsEnable(true);
            notification.setStatus(0);
            notification.setReceiverID(Integer.parseInt(project.getCreateUserID().toString()));
            notificationService.save(notification);
        }
        renderJson(RestResult.buildSuccess());
    }
}
