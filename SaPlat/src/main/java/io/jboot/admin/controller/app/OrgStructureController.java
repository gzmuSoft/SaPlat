package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
    @NotNullPara({"orgType"})
    public void management(){
        Long uid = AuthUtils.getLoginUser().getId();
        String orgType = getPara("orgType");
        setAttr("orgType",orgType)
                .setAttr("uid",uid)
                .render("main.html");
    }

    /**
     * 添加架构页面
     */
    @NotNullPara({"orgType"})
    public void addStructure(){
        Organization organization = organizationService.findById(AuthUtils.getLoginUser().getUserID());
        String orgType = getPara("orgType");
        Long parentID = (getParaToLong("parentID") != null)?getParaToLong("parentID"):0;
        if(organization != null){
            setAttr("org",organization)
                    .setAttr("orgType",orgType)
                    .setAttr("parentID",parentID)
                    .render("addStructure.html");
        }
    }
    /**
     * 邀请架构人员页面
     */
    @NotNullPara({"id","orgType"})
    public void addPerson(){
        //获取架构id
        Long id = getParaToLong("id");
        String orgType = getPara("orgType");
        setAttr("id",id)
                .setAttr("orgType",orgType)
                .render("addPerson.html");
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
        OrgStructure orgStructure = getBean(OrgStructure.class, "orgstructure");
        //获取用户uid
        orgStructure.setCreateUserID(AuthUtils.getLoginUser().getId());
        if (!orgStructureService.save(orgStructure)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 邀请架构人员加入架构
     *
     */
    @NotNullPara("orgType")
    public void postAddPerson(){
        int orgType = getParaToInt("orgType");
        ApplyInvite applyInvite = getBean(ApplyInvite.class,"applyInvite");
        applyInvite.setCreateUserID(AuthUtils.getLoginUser().getId());
        applyInvite.setCreateTime(new Date());
        applyInvite.setApplyOrInvite(1);
        applyInvite.setUserSource(orgType);
        Long uid = applyInvite.getId();
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
     * 组织架构main表格数据
     */
    @NotNullPara("orgType")
    public void tableData(){
        //List<String> list = Arrays.asList("管理机构","企业机构","服务机构","审查团体","专业团体");
        int orgType = getParaToInt("orgType");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long uid = AuthUtils.getLoginUser().getId();
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
        setAttr("sid",sid)
            .render("showPerson.html");
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

    /**
     * 个人群体-我的加入的架构
     */
    public void myStructure(){
        render("myStructure.html");
    }
    /**
     * 个人群体-申请加入架构
     */
    public void joinStructure(){
        render("joinStructure.html");
    }

    /**
     * 查询已经个人群体加入的架构列表接口
     */
    public void myStructureListApi(){
        Long uid = AuthUtils.getLoginUser().getId();
        Map<String,Object> list = structPersonLinkService.findStructureListByPersonID(uid);
        renderJson(list);
    }
    /**
     * 用户主动申请加入架构
     */
    @NotNullPara({"structID"})
    public void joinStructureApi(){
        Long structID = getParaToLong("structID");
        Long uid = AuthUtils.getLoginUser().getId();
        Date nowTime = new Date();
        Calendar threeDaysLater = Calendar.getInstance();
        //获取三天以后的日期作为申请的失效日期
        threeDaysLater.setTime(nowTime);
        threeDaysLater.add(Calendar.DATE, 3);
        OrgStructure orgStructure = orgStructureService.findById(structID);
        ApplyInvite applyInvite = new ApplyInvite();
        applyInvite.setUserID(uid);
        applyInvite.setCreateTime(new Date());
        applyInvite.setUserSource(orgStructure.getOrgType());
        applyInvite.setApplyOrInvite(0);
        //申请或邀请发起模块标识（0：组织架构、1：项目评审）
        applyInvite.setModule(0);
        applyInvite.setName(orgStructure.getName());
        applyInvite.setCreateUserID(uid);
        applyInvite.setDeadTime(threeDaysLater.getTime());
        applyInvite.setStructID(orgStructure.getId());
        //设置状态标识：0待确认，1已拒绝，2已同意
        applyInvite.setStatus(0);
        if(!applyInviteService.save(applyInvite)){
           throw new BusinessException("申请请求发送失败！");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 查找架构
     */
    public void searchStructureApi(){
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long sid = getParaToLong("sid");
        String name = getPara("name");
        OrgStructure orgStructure = new OrgStructure();
        orgStructure.setName(name);
        orgStructure.setId(sid);
        Page<OrgStructure> page = orgStructureService.searchStructure(orgStructure,pageNumber,pageSize);
        renderJson(new DataTable<OrgStructure>(page));
    }
}
