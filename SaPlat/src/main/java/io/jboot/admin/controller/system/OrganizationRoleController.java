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
public class OrganizationRoleController extends Controller{
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

    public void managementIndex(){
        render("management.html");

    }


    @NotNullPara({"id","enable"})
    public void managementEnable(){
        Management management=managementService.findById(getParaToLong("id"));
        if(management==null){
            throw new BusinessException("找不到改团体");
        }
        management.setLastAccessTime(new Date());
        management.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        management.setIsEnable(getParaToBoolean("enable"));
        if(!managementService.update(management)){
            throw new  BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void managementTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        Management management=new Management();
        management.setName(getPara("name"));

        Page<Management> page = managementService.findPage(management, pageNumber, pageSize);
        renderJson(new DataTable<Management>(page));
    }



    public void profgroupIndex(){
        render("profgroup.html");

    }


    @NotNullPara({"id","enable"})
    public void profgroupEnable(){
        ProfGroup profGroup=profGroupService.findById(getParaToLong("id"));
        if(profGroup==null){
            throw new BusinessException("找不到该团体");
        }
        profGroup.setLastAccessTime(new Date());
        profGroup.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        profGroup.setIsEnable(getParaToBoolean("enable"));
        if(!profGroupService.update(profGroup)){
            throw new  BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void profgroupTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        ProfGroup profGroup=new ProfGroup();
        profGroup.setName(getPara("name"));
        Page<ProfGroup> page = profGroupService.findPage(profGroup, pageNumber, pageSize);
        renderJson(new DataTable<ProfGroup>(page));
    }



    public void facagencyIndex(){
        render("facagency.html");

    }


    @NotNullPara({"id","enable"})
    public void facagencyEnable(){
        FacAgency facAgency=facAgencyService.findById(getParaToLong("id"));
        if(facAgency==null){
            throw new BusinessException("找不到该团体");
        }
        facAgency.setLastAccessTime(new Date());
        facAgency.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        facAgency.setIsEnable(getParaToBoolean("enable"));
        if(!facAgencyService.update(facAgency)){
            throw new  BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void facagencyTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        FacAgency facAgency=new FacAgency();
        facAgency.setName(getPara("name"));
        Page<FacAgency> page = facAgencyService.findPage(facAgency, pageNumber, pageSize);
        renderJson(new DataTable<FacAgency>(page));
    }


    public void enterpriseIndex(){
        render("enterprise.html");

    }


    @NotNullPara({"id","enable"})
    public void enterpriseEnable(){
        Enterprise enterprise=enterpriseService.findById(getParaToLong("id"));
        if(enterprise==null){
            throw new BusinessException("找不到该团体");
        }
        enterprise.setLastAccessTime(new Date());
        enterprise.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        enterprise.setIsEnable(getParaToBoolean("enable"));
        if(!enterpriseService.update(enterprise)){
            throw new  BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void enterpriseTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        Enterprise enterprise=new Enterprise();
        enterprise.setName(getPara("name"));
        Page<Enterprise> page = enterpriseService.findPage(enterprise, pageNumber, pageSize);
        renderJson(new DataTable<Enterprise>(page));
    }



    public void reviewgroupIndex(){
        render("reviewgroup.html");

    }


    @NotNullPara({"id","enable"})
    public void reviewgroupEnable(){
        ReviewGroup reviewGroup=reviewGroupService.findById(getParaToLong("id"));
        if(reviewGroup==null){
            throw new BusinessException("找不到该团体");
        }
        reviewGroup.setLastAccessTime(new Date());
        reviewGroup.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        reviewGroup.setIsEnable(getParaToBoolean("enable"));
        if(!reviewGroupService.update(reviewGroup)){
            throw new  BusinessException("操作失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * res表格数据
     */
    public void reviewgroupTableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        ReviewGroup reviewGroup=new ReviewGroup();
        reviewGroup.setName(getPara("name"));
        Page<ReviewGroup> page = reviewGroupService.findPage(reviewGroup, pageNumber, pageSize);
        renderJson(new DataTable<ReviewGroup>(page));
    }


    /**
     * 修改密码
     */
    @Before(GET.class)
    @NotNullPara("orgID")
    public void changepwd() {
        Organization organization=organizationService.findById(getParaToLong("orgID"));

        User user=userService.findByUserIdAndUserSource(organization.getId(),1);
        setAttr("user", user).render("changepwd.html");
    }

    /**
     * 修改密码提交
     */
    @Before( {POST.class} )
    public void postChangepwd() {
        User sysUser = getBean(User.class, "user");

        String pwd = getPara("newPwd");


        String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
        SimpleHash hash = new SimpleHash("md5", pwd, salt2, 2);
        pwd = hash.toHex();
        sysUser.setPwd(pwd);
        sysUser.setSalt2(salt2);
        sysUser.setLastUpdAcct(AuthUtils.getLoginUser().getName());
        sysUser.setLastUpdTime(new Date());
        sysUser.setNote("用户修改密码");

        if (!userService.update(sysUser)) {
            throw new BusinessException("修改密码失败");
        }

        renderJson(RestResult.buildSuccess());
    }
}
