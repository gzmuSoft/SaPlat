package io.jboot.admin.controller.system;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/18.
 */
@RequestMapping("system/organizationrole")
public class OrganizationRoleController extends Controller {
    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private OrganizationService organizationService;

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

    public void managementIndex() {
        render("management.html");

    }


    @NotNullPara({"id", "enable"})
    public void managementEnable() {
        Management management = managementService.findById(getParaToLong("id"));
        if (management == null) {
            throw new BusinessException("找不到该管理机构");
        }
        management.setLastAccessTime(new Date());
        management.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        management.setIsEnable(getParaToBoolean("enable"));
        if (!managementService.update(management)) {
            throw new BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void managementTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        Management management = new Management();
        management.setName(getPara("name"));
        management.setResponsibility(getPara("responsibility"));
        management.setManager(getPara("manager"));
        management.setIsEnable(getParaToBoolean("isEnable"));
        Page<Management> page = managementService.findPage(management, pageNumber, pageSize);
        renderJson(new DataTable<Management>(page));
    }


    public void profgroupIndex() {
        render("profgroup.html");

    }


    @NotNullPara({"id", "enable"})
    public void profgroupEnable() {
        ProfGroup profGroup = profGroupService.findById(getParaToLong("id"));
        if (profGroup == null) {
            throw new BusinessException("找不到该团体");
        }
        profGroup.setLastAccessTime(new Date());
        profGroup.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        profGroup.setIsEnable(getParaToBoolean("enable"));
        if (!profGroupService.update(profGroup)) {
            throw new BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void profgroupTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        ProfGroup profGroup = new ProfGroup();
        profGroup.setName(getPara("name"));
        profGroup.setAdministrator(getPara("administrator"));
        profGroup.setIsEnable(getParaToBoolean("isEnable"));
        Page<ProfGroup> page = profGroupService.findPage(profGroup, pageNumber, pageSize);
        renderJson(new DataTable<ProfGroup>(page));
    }


    public void facagencyIndex() {
        render("facagency.html");

    }


    @NotNullPara({"id", "enable"})
    public void facagencyEnable() {
        FacAgency facAgency = facAgencyService.findById(getParaToLong("id"));
        if (facAgency == null) {
            throw new BusinessException("找不到该团体");
        }
        facAgency.setLastAccessTime(new Date());
        facAgency.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        facAgency.setIsEnable(getParaToBoolean("enable"));
        if (!facAgencyService.update(facAgency)) {
            throw new BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void facagencyTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        FacAgency facAgency = new FacAgency();
        facAgency.setName(getPara("name"));
        facAgency.setCredit(getPara("credit"));
        Date[] dates = new Date[2];
        try {
            dates[0] = getParaToDate("startDate");
            dates[1] = getParaToDate("endDate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Page<FacAgency> page = facAgencyService.findPage(facAgency, dates, pageNumber, pageSize);
        renderJson(new DataTable<FacAgency>(page));
    }


    public void enterpriseIndex() {
        render("enterprise.html");
    }

    @NotNullPara({"id", "enable"})
    public void enterpriseEnable() {
        Enterprise enterprise = enterpriseService.findById(getParaToLong("id"));
        if (enterprise == null) {
            throw new BusinessException("找不到该企业机构");
        }
        enterprise.setLastAccessTime(new Date());
        enterprise.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        enterprise.setIsEnable(getParaToBoolean("enable"));
        if (!enterpriseService.update(enterprise)) {
            throw new BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void enterpriseTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        Enterprise enterprise = new Enterprise();
        enterprise.setName(getPara("name"));
        enterprise.setCredit(getPara("credit"));
        Date[] dates = new Date[2];
        try {
            dates[0] = getParaToDate("startDate");
            dates[1] = getParaToDate("endDate");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Page<Enterprise> page = enterpriseService.findPage(enterprise, dates, pageNumber, pageSize);
        renderJson(new DataTable<Enterprise>(page));
    }

    public void reviewgroupIndex() {
        render("reviewgroup.html");
    }

    @NotNullPara({"id", "enable"})
    public void reviewgroupEnable() {
        ReviewGroup reviewGroup = reviewGroupService.findById(getParaToLong("id"));
        if (reviewGroup == null) {
            throw new BusinessException("找不到该审查团体");
        }
        reviewGroup.setLastAccessTime(new Date());
        reviewGroup.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        reviewGroup.setIsEnable(getParaToBoolean("enable"));
        if (!reviewGroupService.update(reviewGroup)) {
            throw new BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void reviewgroupTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        ReviewGroup reviewGroup = new ReviewGroup();
        reviewGroup.setName(getPara("name"));
        reviewGroup.setAdministrator(getPara("administrator"));
        reviewGroup.setIsEnable(getParaToBoolean("isEnable"));
        Page<ReviewGroup> page = reviewGroupService.findPage(reviewGroup, pageNumber, pageSize);
        renderJson(new DataTable<ReviewGroup>(page));
    }

    /**
     * 修改密码
     */
    @Before(GET.class)
    @NotNullPara("orgID")
    public void changepwd() {
        Organization organization = organizationService.findById(getParaToLong("orgID"));

        User user = userService.findByUserIdAndUserSource(organization.getId(), 1);
        setAttr("user", user).render("changepwd.html");
    }

    /**
     * 修改密码提交
     */
    @Before({POST.class})
    public void postChangepwd() {
        User sysUser = getBean(User.class, "user");

        String pwd = getPara("newPwd");

        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        SimpleHash hash = new SimpleHash("md5", pwd, salt, 2);
        pwd = hash.toHex();
        sysUser.setPwd(pwd);
        sysUser.setSalt(salt);
        sysUser.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        sysUser.setLastAccessTime(new Date());
        sysUser.setRemark("用户修改密码");

        if (!userService.update(sysUser)) {
            throw new BusinessException("修改密码失败");
        }

        renderJson(RestResult.buildSuccess());
    }


    @Before(GET.class)
    @NotNullPara({"orgID","type"})
    public void view() {
        String type=getPara("type");
        Organization organization = organizationService.findById(getParaToLong("orgID"));
        try {
            if ("facAgency".equals(type)) {
                FacAgency facAgency = facAgencyService.findByOrgId(organization.getId());
                FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("facAgency", "法人身份证照片", facAgency.getId());
                setAttr("pictrue", fileForm.getFileID());
                fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("facAgency", "维稳备案文件照片", facAgency.getId());
                setAttr("regDocsFilePath", fileForm.getFileID());
                setAttr("organization", organization).setAttr("facAgency", facAgency).render("fac_agencyView.html");
            } else if ("management".equals(type)) {
                Management management = managementService.findByOrgId(organization.getId());
                setAttr("organization", organization).setAttr("management", management).render("managementView.html");
            } else if ("enterprise".equals(type)) {
                Enterprise enterprise = enterpriseService.findByOrgId(organization.getId());
                FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("enterprise", "法人身份证照片", enterprise.getId());
                setAttr("pictrue", fileForm.getFileID());
                setAttr("organization", organization).setAttr("enterprise", enterprise).render("enterpriseView.html");
            } else if ("reviewGroup".equals(type)) {
                ReviewGroup reviewGroup = reviewGroupService.findByOrgId(organization.getId());
                setAttr("organization", organization).setAttr("reviewGroup", reviewGroup).render("review_groupView.html");
            } else if ("profGroup".equals(type)) {
                ProfGroup profGroup = profGroupService.findByOrgId(organization.getId());
                FileForm fileForm = fileFormService.findFirstByTableNameAndRecordIDAndFileName("profGroup", "管理员身份证照片", profGroup.getId());
                setAttr("identity", fileForm.getFileID());
                setAttr("organization", organization).setAttr("profGroup", profGroup).render("prof_groupView.html");
            } else {
                throw new BusinessException("请求参数非法");
            }
        } catch (NullPointerException nullPoint) {
            throw new BusinessException("用户资料错误!");
        } catch (Exception e) {
            throw new BusinessException("请求参数非法");
        }
    }
}
