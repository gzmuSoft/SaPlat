package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.*;


/**
 * -----------------------------
 *
 * @author rainsLM
 *
 * @date 14:40 2018-07-24
 */
@RequestMapping("/app/OrgStructure")
public class OrgStructureController extends BaseController {

    @JbootrpcService
    private OrgStructureService orgStructureService;

    @JbootrpcService
    private StructPersonLinkService structPersonLinkService;

    @JbootrpcService
    private AuthService authService;

    @JbootrpcService
    private OrganizationService organizationService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private ApplyInviteService applyInviteService;

    @JbootrpcService
    private RoleService roleService;

    /**
     * index
     */
    public void index(){
        User loginUser = AuthUtils.getLoginUser();
        //获取已经认证通过的角色信息
        List<Auth> auth = authService.findListByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.IS_VERIFY, TypeStatus.ORGANIZATION);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < auth.size(); i++) {
            //获取已经认证的具体类型
            list.add(roleService.findById(auth.get(i).getRoleId()).getName());
        }
        if(auth != null && auth.size() >0)
        {
            setAttr("nameList",list).render("prove.html");
            //已经存在认证机构状态
        }else{
            //还没有认证任何架构
            render("noProve.html");
        }
    }

    /**
     * 管理架构主界面
     */
    @NotNullPara({"orgName"})
    public void management(){
        Long uid = AuthUtils.getLoginUser().getUserID();
        String orgName = getPara("orgName");
        setAttr("orgName",orgName)
                .setAttr("uid",uid)
                .render("main.html");
    }

    /**
     * 添加架构页面
     */
    @NotNullPara({"orgName"})
    public void addStructure(){
        Organization organization = organizationService.findById(AuthUtils.getLoginUser().getUserID());
        String orgName = getPara("orgName");
        System.out.println(AuthUtils.getLoginUser().getUserSource());
        Long parentID = (getParaToLong("parentID") != null)?getParaToLong("parentID"):0;
        if(organization != null){
            setAttr("org",organization)
                    .setAttr("orgName",orgName)
                    .setAttr("parentID",parentID)
                    .render("addStructure.html");
        }


    }
    /**
     * 添加架构人员页面
     */
    @NotNullPara({"id"})
    public void addPerson(){
        //获取架构id
        Long id = getParaToLong("id");
        setAttr("id",id).render("addPerson.html");
    }
    /**
     * 更新架构信息
     */
    @NotNullPara({"id"})
    public void updateStructure(){
        Long id = getParaToLong("id");
        OrgStructure orgStructure = orgStructureService.findById(id);
        setAttr("orgstruct",orgStructure).render("updateStructure.html");
    }

    /**
     * 获取提交的架构更新信息
     */

    public void postUpdateStructure(){
        OrgStructure orgStructure = getBean(OrgStructure.class,"orgStructure");
        OrgStructure byId = orgStructureService.findById(orgStructure.getId());
        if(byId == null){
            throw new BusinessException("架构不存在");
        }
        if(!orgStructureService.update(orgStructure)){
            throw new BusinessException("架构信息更新失败");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 添加架构信息
     */
    public void postAddStructure(){
        //组织类型OrgType（0：管理机构、1：企业机构、2：服务机构、3：审查团体、4：专业团体）
        //List<String> list = Arrays.asList("管理机构","企业机构","服务机构","审查团体","专业团体");
        OrgStructure orgStructure = getBean(OrgStructure.class, "orgstructure");
        //获取用户uid
        orgStructure.setCreateUserID(AuthUtils.getLoginUser().getUserID());
        if (!orgStructureService.save(orgStructure)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 添加架构人员
     *
     */
    public void postAddPerson(){
        ApplyInvite applyInvite = getBean(ApplyInvite.class,"applyInvite");
        applyInvite.setCreateUserID(AuthUtils.getLoginUser().getUserID());
        applyInvite.setCreateTime(new Date());
        applyInvite.setApplyOrInvite(1);
        //等待做
        applyInvite.setUserSource(0);
        Long uid = applyInvite.getUserID();
        User user = userService.findById(uid);
        if(user == null){
            throw new BusinessException("用户不存在无法邀请加入组织架构！");
            //生成邀请注册页面
        }
        if(!applyInviteService.save(applyInvite)){
            throw new BusinessException("邀请信息已经发送，等待用户同意");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 表格数据
     */
    @NotNullPara("orgName")
    public void tableData(){
        List<String> list = Arrays.asList("管理机构","企业机构","服务机构","审查团体","专业团体");
        String orgName = getPara("orgName");
        int orgType = list.indexOf(orgName);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long uid = AuthUtils.getLoginUser().getUserID();
        OrgStructure orgStructure = new OrgStructure();
        orgStructure.setName(getPara("name"));

        Page<OrgStructure> page = orgStructureService.findPage(orgStructure,pageNumber,pageSize,uid,orgType);
        renderJson(new DataTable<OrgStructure>(page));
    }
    /**
     * 查看架构人员
     */
    @NotNullPara({"structureID"})
    public void showPerson(){
        Long sid = getParaToLong("structureID");
//        Map<String,Object> structPersonLink = structPersonLinkService.findByStructIdAndUsername(sid);
//        renderJson(structPersonLink);
        setAttr("sid",sid).
            render("showPerson.html");
    }

    /**
     * 架构人员列表
     */
    @NotNullPara({"structureID"})
    public void StructurePersonList(){
        Long sid = getParaToLong("structureID");
        Map<String,Object> structPersonLinkList = structPersonLinkService.findByStructIdAndUsername(sid);
        renderJson(structPersonLinkList);
    }
}
