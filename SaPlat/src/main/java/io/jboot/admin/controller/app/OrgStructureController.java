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
import io.jboot.admin.service.entity.status.system.ApplyInviteStatus;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * -----------------------------
 *
 * @author rainsLM
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

    @JbootrpcService
    private PersonService personService;

    /**
     * index
     */
    public void index() {
        User loginUser = AuthUtils.getLoginUser();
        //获取已经认证通过的角色信息
        List<Auth> auth = authService.findListByUserIdAndStatusAndType(loginUser.getId(), AuthStatus.IS_VERIFY, TypeStatus.ORGANIZATION);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < auth.size(); i++) {
            //获取已经认证的具体类型
            list.add(roleService.findById(auth.get(i).getRoleId()).getName());
        }
        if (auth != null && auth.size() > 0) {
            setAttr("nameList", list).render("prove.html");
            //已经存在认证机构状态
        } else {
            //还没有认证任何架构
            render("noProve.html");
        }
    }

    /**
     * 管理架构主界面
     */
    @NotNullPara({"orgType"})
    public void management() {
        Long uid = AuthUtils.getLoginUser().getId();
        String orgType = getPara("orgType");
        setAttr("orgType", orgType)
                .setAttr("uid", uid)
                .render("main.html");
    }

    /**
     * 添加架构页面
     */
    @NotNullPara({"orgType"})
    public void addStructure() {
        Organization organization = organizationService.findById(AuthUtils.getLoginUser().getUserID());
        String orgType = getPara("orgType");
        Long parentID = (getParaToLong("parentID") != null) ? getParaToLong("parentID") : 0;
        String parentStructName = null;
        if(parentID == 0){
            parentStructName = "根架构";
        }else{
           OrgStructure org =  orgStructureService.findById(parentID);
            parentStructName = org.getName();
        }
        if (organization != null) {
            setAttr("org", organization)
                    .setAttr("parentStructName",parentStructName)
                    .setAttr("orgType", orgType)
                    .setAttr("parentID", parentID)
                    .render("addStructure.html");
        }
    }

    /**
     * 邀请架构人员页面
     */
    @NotNullPara({"id", "orgType"})
    public void addPerson() {
        //获取架构id
        Long id = getParaToLong("id");
        //获取架构
        OrgStructure org = orgStructureService.findById(id);
        String orgType = getPara("orgType");
        setAttr("id", id)
                .setAttr("orgStructName",org.getName())
                .setAttr("orgType", orgType)
                .render("addPerson.html");
    }

    /**
     * 更新架构信息
     */
    @NotNullPara({"id"})
    public void updateStructure() {
        Long id = getParaToLong("id");
        OrgStructure orgStructure = orgStructureService.findById(id);
        setAttr("orgstruct", orgStructure).render("updateStructure.html");
    }

    /**
     * 获取提交的架构更新信息
     */

    public void postUpdateStructure() {
        OrgStructure orgStructure = getBean(OrgStructure.class, "orgStructure");
        OrgStructure byId = orgStructureService.findById(orgStructure.getId());
        if (byId == null) {
            throw new BusinessException("架构不存在");
        }
        if (!orgStructureService.update(orgStructure)) {
            throw new BusinessException("架构信息更新失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 添加架构信息
     */
    public void postAddStructure() {
        OrgStructure orgStructure = getBean(OrgStructure.class, "orgstructure");
        //获取用户uid
        orgStructure.setCreateUserID(AuthUtils.getLoginUser().getId());
        if (!orgStructureService.save(orgStructure)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 组织邀请个人群体加入架构
     */
    @NotNullPara("orgType")
    public void postAddPerson() {
        int orgType = getParaToInt("orgType");
        ApplyInvite applyInvite = getBean(ApplyInvite.class, "applyInvite");
        Date nowTime = new Date();
        Calendar threeDaysLater = Calendar.getInstance();
        //获取三天以后的日期作为申请的失效日期
        threeDaysLater.setTime(nowTime);
        threeDaysLater.add(Calendar.DATE, 3);

        applyInvite.setDeadTime(threeDaysLater.getTime());
        applyInvite.setCreateUserID(AuthUtils.getLoginUser().getId());
        applyInvite.setCreateTime(new Date());
        applyInvite.setApplyOrInvite(1);
        applyInvite.setModule(0);
        applyInvite.setStatus(ApplyInviteStatus.WAITE);
        applyInvite.setUserSource(orgType);
        applyInvite.setBelongToID(AuthUtils.getLoginUser().getId());
        User user = userService.findByName(applyInvite.getName());
        OrgStructure orgStructure = orgStructureService.findById(applyInvite.getStructID());
        if (orgStructure == null) {
            throw new BusinessException("你不能邀请用户加入一个不存在的架构");
        }
        applyInvite.setName(orgStructure.getName());
        if (user == null) {
            throw new BusinessException("用户不存在无法邀请加入组织架构！");
            //生成邀请注册页面
        }
        if (!isPerson(user.getUserID())) {
            throw new BusinessException("该用户不是个人群体账户！");
        }
        if(isJoined(orgStructure.getId(),user.getId())){
            throw new BusinessException("该用户已经被邀请！");
        }
        if (isStructPerson(orgStructure.getId(), user.getUserID())) {
            throw new BusinessException("该用户已经加入了架构！");
        }
        applyInvite.setUserID(user.getId());
        Notification notification = sendMessage("邀请加入架构通知", "你好！" + AuthUtils.getLoginUser().getName() + "组织架构（" + applyInvite.getName() + "）的管理员邀请你加入该架构，请前往组织架构 -> 通知消息 处理！", "/app/OrgStructure/showMessage", user.getId(), AuthUtils.getLoginUser());
        if (!applyInviteService.saveOrUpdateAndSend(applyInvite, notification)) {
            throw new BusinessException("邀请请求发送失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 组织架构main表格数据
     */
    @NotNullPara("orgType")
    public void tableData() {
        int orgType = getParaToInt("orgType");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long uid = AuthUtils.getLoginUser().getId();
        String structName = getPara("name","");
        Page<Record> page = orgStructureService.findMainList(pageNumber,pageSize,uid,orgType,structName);
        renderJson(new DataTable<Record>(page));
    }

    /**
     * 查看架构人员
     */
    @NotNullPara({"structureID"})
    public void showPerson() {
        Long sid = getParaToLong("structureID");
        int orgType = getParaToInt("orgType");
        setAttr("sid", sid)
                .setAttr("orgType",orgType)
                .render("showPerson.html");
    }

    /**
     * 架构人员列表
     */
    @NotNullPara({"structureID"})
    public void StructurePersonList() {
        int pageNumber = getParaToInt("pageNumber",1);
        int pageSize = getParaToInt("pageSize",30);
        Long sid = getParaToLong("structureID");
        Page<Record> page = structPersonLinkService.findPersonListByStructId(pageNumber,pageSize,sid);
        renderJson(new DataTable<Record>(page));
//        Map<String, Object> structPersonLinkList = structPersonLinkService.findByStructIdAndUsername(sid);
//        renderJson(structPersonLinkList);
    }

    /**
     * 个人群体-我的加入的架构
     */
    public void myStructure() {
        render("myStructure.html");
    }

    /**
     * 个人群体-申请加入架构
     */
    public void joinStructure() {
        render("joinStructure.html");
    }

    /**
     * 个人群体 - 同意加入架构
     */
    @NotNullPara("id")
    public void acceptJoinStruct() {
        Long id = getParaToLong("id");
        Long UserId = AuthUtils.getLoginUser().getUserID();
        ApplyInvite applyInvite = applyInviteService.findById(id);
        if (applyInvite == null) {
            throw new BusinessException("邀请信息不存在");
        }
        if (!isPerson(AuthUtils.getLoginUser().getUserID())) {
            throw new BusinessException("该用户不是个人群体账户！");
        }
        if (isStructPerson(applyInvite.getStructID(), UserId)) {
            throw new BusinessException("你已经加入了架构，无需重复加入架构");
        }
        applyInvite.setStatus(ApplyInviteStatus.AGREE);
        StructPersonLink structPersonLink = new StructPersonLink();
        structPersonLink.setCreateTime(new Date());
        structPersonLink.setStructID(applyInvite.getStructID());
        structPersonLink.setCreateUserID(applyInvite.getCreateUserID());
        structPersonLink.setPersonID(AuthUtils.getLoginUser().getUserID());
        structPersonLink.setIsEnable(true);
        Notification notification = sendMessage("邀请用户加入架构通知", "你好！你邀请的用户" + AuthUtils.getLoginUser().getName() + "已经成功加入组织架构！", "/app/OrgStructure/applyManage", applyInvite.getBelongToID(), AuthUtils.getLoginUser());
        if (!applyInviteService.saveAndUpdateAndSend(applyInvite, notification, structPersonLink)) {
            renderJson(RestResult.buildError("同意申请处理失败！"));
            throw new BusinessException("同意申请处理失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 个人群体 - 拒绝加入架构
     */
    @NotNullPara("id")
    public void rejectJoinStruct() {
        Long ApplyId = getParaToLong("id");
        String reply = getPara("reply", null);
        ApplyInvite applyInvite = applyInviteService.findById(ApplyId);
        if (applyInvite == null) {
            throw new BusinessException("邀请信息不存在");
        }
        applyInvite.setReply(reply);
        applyInvite.setStatus(ApplyInviteStatus.REFUSE);
        Notification notification = sendMessage("拒绝加入架构通知", "你好！" + AuthUtils.getLoginUser().getName() + "拒绝了你的邀请（加入组织架构" + applyInvite.getName() + "），拒绝原因:" + reply + "。", "/app/OrgStructure/applyManage", applyInvite.getBelongToID(), AuthUtils.getLoginUser());
        if (!applyInviteService.saveOrUpdateAndSend(applyInvite, notification)) {
            renderJson(RestResult.buildError("拒绝用户申请失败！"));
            throw new BusinessException("拒绝用户申请失败！");
        }
        renderJson(RestResult.buildSuccess());

    }

    /**
     * 查询已经个人群体加入的架构列表接口
     */
    public void myStructureListApi() {
        Long uid = AuthUtils.getLoginUser().getUserID();
        int pageNumber = getParaToInt("pageNubmer",1);
        int pageSize = getParaToInt("pageSize",30);
//        Map<String, Object> list = structPersonLinkService.findStructureListByPersonID(uid);
        Page<Record> page = structPersonLinkService.findStructListPageByPersonID(pageNumber,pageSize,uid);
        renderJson(new DataTable(page));
    }

    /**
     * 用户主动申请加入架构
     */
    @NotNullPara({"structID"})
    public void joinStructureApi() {
        Long structID = getParaToLong("structID");
        Long uid = AuthUtils.getLoginUser().getId();
        Long UserId = AuthUtils.getLoginUser().getUserID();
        Date nowTime = new Date();
        Calendar threeDaysLater = Calendar.getInstance();
        //获取三天以后的日期作为申请的失效日期
        threeDaysLater.setTime(nowTime);
        threeDaysLater.add(Calendar.DATE, 3);
        OrgStructure orgStructure = orgStructureService.findById(structID);
        if (orgStructure == null) {
            throw new BusinessException("你准备加入的架构不存在！");
        }
        if (!isPerson(AuthUtils.getLoginUser().getUserID())) {
            throw new BusinessException("该用户不是个人群体账户！");
        }
        if (isStructPerson(orgStructure.getId(), UserId)) {
            throw new BusinessException("你已经成功加入过此架构，无法重复加入!");
        }
        //判断用户在一定时间内不得重复申请、

        if (isJoined(orgStructure.getId(), uid)) {
            throw new BusinessException("你已经申请加入过此架构，仍在处理中请耐心等待！");
        }
        ApplyInvite applyInvite = new ApplyInvite();
        applyInvite.setBelongToID(orgStructure.getCreateUserID());
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
        applyInvite.setStatus(ApplyInviteStatus.WAITE);
        Notification notification = sendMessage("申请加入架构通知", "你好！" + AuthUtils.getLoginUser().getName() + "申请加入组织架构（" + applyInvite.getName() + "），请前往组织架构->申请管理中心处理！", "/app/OrgStructure/showMessage", applyInvite.getBelongToID(), AuthUtils.getLoginUser());
        if (!applyInviteService.saveOrUpdateAndSend(applyInvite, notification)) {
            throw new BusinessException("申请请求发送失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 查找架构
     */
    public void searchStructureApi() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long sid = getParaToLong("sid");
        String name = getPara("name");
        OrgStructure orgStructure = new OrgStructure();
        orgStructure.setName(name);
        orgStructure.setId(sid);
        Page<Record> page = orgStructureService.searchStructure(orgStructure, pageNumber, pageSize);
        renderJson(new DataTable<Record>(page));
    }

    /**
     * 查看架构邀请信息页面
     */
    public void showMessage() {
        render("showMessage.html");
    }

    /**
     * 查看架构邀请信息接口
     */
    public void showMessageApi() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long uid = AuthUtils.getLoginUser().getId();
        String name = getPara("name");
        ApplyInvite applyInvite = new ApplyInvite();
        applyInvite.setUserID(uid);
        applyInvite.setName(name);
        Page<ApplyInvite> list = applyInviteService.findListByUserIdOrUserName(pageNumber, pageSize, applyInvite);
        renderJson(new DataTable<ApplyInvite>(list));
    }

    /**
     * 管理机构 - 管理申请加入架构页面
     */
    public void applyManage() {
        render("applyManage.html");
    }

    /**
     * 管理组织 - 查看用户主动申请加入架构列表
     */
    public void showApplyApi() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        String name = getPara("name");
        Long pid = getParaToLong("uid");
        ApplyInvite applyInvite = new ApplyInvite();
        applyInvite.setUserID(pid);
        applyInvite.setName(name);
        applyInvite.setBelongToID(AuthUtils.getLoginUser().getId());
        Page<ApplyInvite> list = applyInviteService.findApplyByUserIdOrName(pageNumber, pageSize, applyInvite);
        renderJson(new DataTable<ApplyInvite>(list));
    }

    /**
     * 组织 - 同意用户申请加入架构
     */
    @NotNullPara("id")
    public void acceptApply() {
        Long id = getParaToLong("id");
        ApplyInvite applyInvite = applyInviteService.findById(id);
        if (applyInvite == null) {
            throw new BusinessException("申请信息不存在");
        }
        User user = userService.findById(applyInvite.getUserID());
        if (!isPerson(user.getUserID())) {
            throw new BusinessException("该用户不是个人群体账户！");
        }
        if (isStructPerson(applyInvite.getStructID(), user.getUserID())) {
            throw new BusinessException("该用户已经加入了架构，无需重复加入");
        }

        applyInvite.setStatus(ApplyInviteStatus.AGREE);
        StructPersonLink structPersonLink = new StructPersonLink();
        structPersonLink.setCreateTime(new Date());
        structPersonLink.setStructID(applyInvite.getStructID());
        structPersonLink.setCreateUserID(applyInvite.getCreateUserID());
        structPersonLink.setPersonID(user.getUserID());
        structPersonLink.setIsEnable(true);
        Notification notification = sendMessage("成功加入架构通知", "你好！你申请加入的组织架构（" + applyInvite.getName() + "）已经加入成功！", "/app/OrgStructure/showMessage", applyInvite.getUserID(), AuthUtils.getLoginUser());
        if (!applyInviteService.saveAndUpdateAndSend(applyInvite, notification, structPersonLink)) {
            renderJson(RestResult.buildError("同意申请处理失败！"));
            throw new BusinessException("同意申请处理失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 组织 - 拒绝用户申请加入架构
     */
    @NotNullPara("id")
    public void rejectApply() {
        Long id = getParaToLong("id");
        String reply = getPara("reply", null);
        ApplyInvite applyInvite = applyInviteService.findById(id);
        if (applyInvite == null) {
            throw new BusinessException("邀请信息不存在");
        }
        applyInvite.setReply(reply);
        applyInvite.setStatus(ApplyInviteStatus.REFUSE);
        Notification notification = sendMessage("被拒绝加入架构通知", "你好！" + AuthUtils.getLoginUser().getName() + "你申请加入组织架构（" + applyInvite.getName() + "）被组织管理员拒绝，被拒绝原因:" + reply + "！", "/app/OrgStructure/showMessage", applyInvite.getUserID(), AuthUtils.getLoginUser());
        if (!applyInviteService.saveOrUpdateAndSend(applyInvite, notification)) {
            renderJson(RestResult.buildError("拒绝用户申请失败！"));
            throw new BusinessException("拒绝用户申请失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 组织管理 - 禁用架构成员
     */
    @NotNullPara("id")
    public void personUse(){
        Long id = getParaToLong("id");
        StructPersonLink structPersonLink = structPersonLinkService.findById(id);
        if(structPersonLink == null){
            throw new BusinessException("架构用户不存在无法启用！");
        }
        structPersonLink.setIsEnable(true);
        structPersonLink.setRemark("启用的架构人员");
        if(!structPersonLinkService.update(structPersonLink)){
            throw new BusinessException("启用失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 组织管理 - 启用架构成员
     */
    @NotNullPara("id")
    public void personUnuse(){
        Long id = getParaToLong("id");
        StructPersonLink structPersonLink = structPersonLinkService.findById(id);
        if(structPersonLink == null){
            throw new BusinessException("架构用户不存在无法禁用！");
        }
        structPersonLink.setIsEnable(false);
        structPersonLink.setRemark("启用架构人员");
        if(!structPersonLinkService.update(structPersonLink)){
            throw new BusinessException("禁用失败！");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 私有方法 用于查询用户在是否已经是架构中的成员 避免架构人员关联表中数据存在重复
     * true存在 false 不存在
     */
    private boolean isStructPerson(Long structID, Long userID) {
        List<StructPersonLink> structPersonLink = structPersonLinkService.findByStructIdAndUserID(structID, userID);
        if (structPersonLink.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 私有方法 查询是否是个人群体
     *
     * @param UserID
     * @return
     */
    private boolean isPerson(Long UserID) {
        Person person = personService.findById(UserID);
        if (person != null) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * 私有方法 用于发送站内通知信息
     */
    private Notification sendMessage(String title, String content, String source, Long ReceiverID, User user) {
        Notification notification = new Notification();
        notification.setName(title);
        notification.setSource(source);
        notification.setContent(content);
        notification.setRecModule("");
        notification.setReceiverID(Math.toIntExact(ReceiverID));
        notification.setCreateTime(new Date());
        notification.setCreateUserID(user.getId());
        notification.setLastAccessTime(new Date());
        notification.setLastUpdateUserID(user.getId());
        notification.setStatus(0);
        notification.setIsEnable(true);
        return notification;
    }

    /**
     * 私有方法 - 用于判断是否有待处理中的申请
     *
     * @param structID
     * @param UserID
     * @return
     */
    private boolean isJoined(Long structID, Long UserID) {
        ApplyInvite applyInvite = applyInviteService.findByStructIDAndUserID(structID, UserID);
        if (applyInvite == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 批量邀请页面
     */
    public void batchInvite(){
        int orgType = getParaToInt("orgType");
        int structID = getParaToInt("structID");
        setAttr("orgType",orgType)
                .setAttr("structID",structID)
                .render("batchInvite.html");
    }

    /**
     * 处理批量邀请数据提交
     */
    public void batchPostAddPerson(){
        //获取人员列表
        String personLists = getPara("invitePersons");
        //获取类型
        int orgType = getParaToInt("orgType");
        //获取要加入的架构id
        Long structID = getParaToLong("structID");
        //System.out.println(personLists);
        String[] persons = personLists.split("\r\n");
        String username = null;
        OrgStructure orgStructure = orgStructureService.findById(structID);
        int success = 0;
        int failed = 0;
        for(String person : persons){
            //获取账户名称
            username = person.trim();
            //判断用户名是否可用
            if(username != null){
                ApplyInvite applyInvite = new ApplyInvite();
                Date nowTime = new Date();
                Calendar threeDaysLater = Calendar.getInstance();
                //获取三天以后的日期作为申请的失效日期
                threeDaysLater.setTime(nowTime);
                threeDaysLater.add(Calendar.DATE, 3);

                applyInvite.setStructID(structID);
                applyInvite.setDeadTime(threeDaysLater.getTime());
                applyInvite.setCreateUserID(AuthUtils.getLoginUser().getId());
                applyInvite.setCreateTime(new Date());
                applyInvite.setApplyOrInvite(1);
                applyInvite.setModule(0);
                applyInvite.setStatus(ApplyInviteStatus.WAITE);
                applyInvite.setUserSource(orgType);
                applyInvite.setBelongToID(AuthUtils.getLoginUser().getId());
                User user = userService.findByName(username);

                if(orgStructure != null && user != null && isPerson(user.getUserID()) == true && isStructPerson(orgStructure.getId(), user.getUserID()) == false && isJoined(orgStructure.getId(),user.getId()) == false){
                    applyInvite.setName(orgStructure.getName());
                    applyInvite.setUserID(user.getId());
                    Notification notification = sendMessage("邀请加入架构通知", "你好！" + AuthUtils.getLoginUser().getName() + "组织架构（" + applyInvite.getName() + "）的管理员邀请你加入该架构，请前往组织架构 -> 通知消息 处理！", "/app/OrgStructure/showMessage", user.getId(), AuthUtils.getLoginUser());
                    if (applyInviteService.saveOrUpdateAndSend(applyInvite, notification)) {
                        success++;
                    }else{
                        failed++;
                    }
                }else{
                    failed++;
                }
            }else{
                failed++;
                continue;
            }
        }
        Map<String,String> map = new ConcurrentHashMap<String,String>();
        map.put("code","0");
        map.put("data","成功" + success + "人,失败" + failed + "人！");
        map.put("msg","成功" + success + "人,失败" + failed + "人！");
        renderJson(map);
    }

}