package io.jboot.admin.controller.system;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.*;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/7/13.
 */
@RequestMapping("/system/projectauth")
public class ProjectAuthController extends BaseController {

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private AuthService authService;


    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private AuthProjectService authProjectService;


    @JbootrpcService
    private UserService userService;

    //团队
    @JbootrpcService
    private OrganizationService organizationService;


    //个人
    @JbootrpcService
    private PersonService personService;

    @JbootrpcService
    private ExpertGroupService expertGroupService;

    @JbootrpcService
    private ManagementService managementService;

    @JbootrpcService
    private EnterpriseService enterpriseService;

    @JbootrpcService
    private FacAgencyService facAgencyService;

    @JbootrpcService
    private ProfGroupService profGroupService;

    @JbootrpcService
    private ReviewGroupService reviewGroupService;

    @JbootrpcService
    private FileFormService fileFormService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private ProjectStepService projectStepService;

    @JbootrpcService
    private LeaderGroupService leaderGroupService;

    @JbootrpcService
    private ProjectAssTypeService projectAssTypeService;

    @Before(GET.class)
    public void index() {
        render("main.html");
    }


    @Before(GET.class)
    public void verifyIndex() {
        List<Role> roleList = roleService.findByStatusUsed();
        BaseStatus roleStatus = new RoleStatus();
        for (Role role : roleList) {
            roleStatus.add(role.getId().toString(), role.getName());
        }


        setAttr("roleStatus", roleStatus).render("verifyIndex.html");
    }

    @Before(GET.class)
    public void dataIndex() {
        List<Role> roleList = roleService.findByStatusUsed();
        BaseStatus roleStatus = new BaseStatus() {};
        for (Role role : roleList) {
            if (role.getName().contains("立项")) {
                roleStatus.add(role.getId().toString(), role.getName());
            }
        }
        setAttr("roleStatus", roleStatus).render("dataIndex.html");
    }

    @Before(GET.class)
    @NotNullPara("id")
    public void verifyUpdate() {
        Long id = getParaToLong("id");
        Auth auth = authService.findById(id);
        if (auth == null) {
            throw new BusinessException("没有这个审核");
        }
        if (auth.getStatus().equals(AuthStatus.CANCEL_VERIFY)) {
            throw new BusinessException("用户已取消审核");
        }

        setAttr("auth", auth).render("verifyupdate.html");
    }

    @Before(GET.class)
    @NotNullPara("id")
    public void dataUpdate() {
        Long id = getParaToLong("id");
        AuthProject authProject = authProjectService.findById(id);
        if (authProject == null) {
            throw new BusinessException("没有这个审核");
        }
        setAttr("authProject", authProject).render("dataupdate.html");
    }


    @Before(POST.class)
    public void verifyPostupdate() {
        Auth auth = getBean(Auth.class, "auth");
        Auth model = authService.findById(auth.getId());
        if (model == null) {
            throw new BusinessException("没有这个审核");
        }

        User user = AuthUtils.getLoginUser();
        model.setLastUpdUser(user.getName());
        model.setReply(auth.getReply());
        if(auth.getStatus() == null) {
            model.setStatus("1");
        }
        else{
            model.setStatus("2");
        }
        if (!authService.update(model)) {
            throw new BusinessException("审核失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @Before({POST.class})
    public void dataPostupdate() {
        AuthProject authProject = getBean(AuthProject.class, "authProject");
        AuthProject model = authProjectService.findById(authProject.getId());
        if (model == null) {
            throw new BusinessException("没有这个审核");
        }

        User user = AuthUtils.getLoginUser();
        model.setLastUpdUser(user.getName());
        model.setReply(authProject.getReply());
        if(authProject.getStatus() == null) {
            model.setStatus("1");
        }
        else{
            model.setStatus("2");
        }
        model.setRemark(user.getId().toString());
        if (!authProjectService.update(model)) {
            throw new BusinessException("审核失败");
        }
        renderJson(RestResult.buildSuccess());
    }


    @Before(GET.class)
    @NotNullPara({"pageNumber", "pageSize"})
    public void verifyTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Auth auth = new Auth();
        if (getParaToLong("userId") != null) {
            auth.setUserId(getParaToLong("userId"));
        }
        if (!"".equals(getPara("status"))) {
            auth.setStatus(getPara("status"));
        }
        if (!"".equals(getPara("name"))) {
            auth.setName(getPara("name"));
        }
        auth.setType(TypeStatus.PROJECT_VERIFY);
        Page<Auth> page = authService.findPage(auth, pageNumber, pageSize);
        renderJson(new DataTable<Auth>(page));
    }


    @Before(GET.class)
    @NotNullPara({"pageNumber", "pageSize"})
    public void dataTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        String ctime = getPara("cTime", "ctime");
        AuthProject authProject = new AuthProject();
        int cindex = ctime.indexOf("/");
        if (cindex > 0) {
            authProject.setStartTime(ctime.substring(0, cindex - 1));
            authProject.setEntTime(ctime.substring(cindex + 2));
        }
        if (getParaToLong("userId") != null) {
            authProject.setUserId(getParaToLong("userId"));
        }
        if (!"".equals(getPara("status"))) {
            authProject.setStatus(getPara("status"));
        }
        if (!"".equals(getPara("name"))) {
            authProject.setName(getPara("name"));
        }
        if (!"".equals(getPara("roleId"))) {
            authProject.setName(getPara("roleId"));
        }
        authProject.setType(ProjectTypeStatus.INFORMATION_REVIEW);
        Page<AuthProject> page = authProjectService.findPage(authProject, pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            page.getList().get(i).setProjectName(projectService.findById(page.getList().get(i).getProjectId()).getName());
        }
        renderJson(new DataTable<AuthProject>(page));
    }


    @Before(GET.class)
    @NotNullPara("id")
    public void verifyView() {
        Long id = getParaToLong("id");
        Auth auth = authService.findById(id);
        if (auth == null) {
            throw new BusinessException("没有这个审核");
        }
        User user = userService.findById(auth.getUserId());
        Person person = personService.findById(user.getUserID());
        Organization organization = organizationService.findById(user.getUserID());

        if (!auth.getType().equals(TypeStatus.PROJECT_VERIFY)) {
            throw new BusinessException("请求参数非法");
        }

        Role role = roleService.findById(auth.getRoleId());
        try {
            if ("expertGroup".equals(role.getRemark())) {
                ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
                setAttr("person", person).setAttr("expertGroup", expertGroup).render("expertGroup.html");
            } else if ("facAgency".equals(role.getRemark())) {
                FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
                FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("facAgency", "法人身份证照片", facAgency.getId());
                setAttr("pictrue", fileForm.getFileID());
                fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("facAgency", "维稳备案文件照片", facAgency.getId());
                setAttr("regDocsFilePath", fileForm.getFileID());
                setAttr("organization", organization).setAttr("facAgency", facAgency).render("fac_agency.html");
            } else if ("management".equals(role.getRemark())) {
                Management management = managementService.findByOrgId(organization.getId());
                setAttr("organization", organization).setAttr("management", management).render("management.html");
            } else if ("enterprise".equals(role.getRemark())) {
                Enterprise enterprise = enterpriseService.findByOrgId(organization.getId());
                FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("enterprise", "法人身份证照片", enterprise.getId());
                setAttr("pictrue", fileForm.getFileID());
                setAttr("organization", organization).setAttr("enterprise", enterprise).render("enterprise.html");
            } else if ("reviewGroup".equals(role.getRemark())) {
                ReviewGroup reviewGroup = reviewGroupService.findByOrgId(organization.getId());
                setAttr("organization", organization).setAttr("reviewGroup", reviewGroup).render("review_group.html");
            } else if ("profGroup".equals(role.getRemark())) {
                ProfGroup profGroup = profGroupService.findByOrgId(organization.getId());
                FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("profGroup", "管理员身份证照片", profGroup.getId());
                setAttr("identity", fileForm.getFileID());
                setAttr("organization", organization).setAttr("profGroup", profGroup).render("prof_group.html");
            } else {
                throw new BusinessException("请求参数非法");
            }
        } catch (NullPointerException nullPoint) {
            throw new BusinessException("用户资料错误!");
        } catch (Exception e) {
            throw new BusinessException("请求参数非法");
        }
    }

    @Before(GET.class)
    @NotNullPara("id")
    public void dataView() {
        Long id = getParaToLong("id");
        AuthProject apModel = authProjectService.findByProjectId(id);//获取项目的立项审核信息
        Project pModel = projectService.findById(id); //获取项目信息
        pModel.setTypeName(projectAssTypeService.findById(pModel.getPaTypeID()).getName());
        Organization organization = organizationService.findById(userService.findById(pModel.getUserId()).getUserID()); //获取组织信息
        String strRoleName = roleService.findById(apModel.getRoleId()).getName();
        Management management = managementService.findById(pModel.getManagementID());
        setAttr("organization",organization)
                .setAttr("management", management)
                .setAttr("model", pModel)
                .setAttr("roleName", strRoleName)
                .render("project.html");
    }

    /**
     * 立项中-文件列表表格渲染
     */
    @NotNullPara({"id", "paTypeID"})
    public void fileTable() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long id = getParaToLong("id");
        ProjectAssType projectAssType = projectAssTypeService.findById(getParaToLong("paTypeID"));
        if (projectAssType != null) {
            ProjectFileType parentProjectFileType = projectFileTypeService.findByName(projectAssType.getName());
            ProjectFileType childProjectFileType = new ProjectFileType();
            childProjectFileType.setParentID(parentProjectFileType.getId());
            Page<ProjectFileType> page = projectFileTypeService.findPage(childProjectFileType, pageNumber, pageSize);
            for (int i = 0; i < page.getList().size(); i++) {
                FileProject fileProject = fileProjectService.findByProjectIDAndFileTypeID(id, page.getList().get(i).getId());
                if (fileProject != null) {
                    page.getList().get(i).setFileID(fileProject.getFileID());
                    page.getList().get(i).setIsUpLoad(true);
                } else {
                    page.getList().get(i).setIsUpLoad(false);
                }
            }
            renderJson(new DataTable<ProjectFileType>(page));
        } else {
            renderJson(new DataTable<ProjectFileType>(null));
        }
    }
}
