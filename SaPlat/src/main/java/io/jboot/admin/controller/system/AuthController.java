package io.jboot.admin.controller.system;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.RoleStatus;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

@RequestMapping("/system/auth")
public class AuthController extends BaseController{
    @JbootrpcService
    private AuthService authService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private OrganizationService organizationService;

    @JbootrpcService
    private ProfGroupService profGroupService;

//    @JbootrpcService
//    private ExpertGroupService expertGroupService;


    public void index(){
        List<Role>  roleList =roleService.findByStatusUsed();
        BaseStatus roleStatus = new RoleStatus();
        for (Role role : roleList) {
            roleStatus.add(role.getId().toString(),role.getName());
        }
        BaseStatus typeStatus = new RoleStatus();
        typeStatus.add("0","个人审核");
        typeStatus.add("1","团体审核");
        setAttr("typeStatus",typeStatus).setAttr("roleStatus",roleStatus).render("main.html");
    }
//    public void add(){
//        render("add.html");
//    }

    public void delete(){
        Long id = getParaToLong("id");
        if (!authService.deleteById(id)){
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara("id")
    public void view(){
        Long id =getParaToLong("id");
        Auth auth=authService.findById(id);
        User user=userService.findById(auth.getUserId());
        Organization organization = organizationService.findById(user.getUserID());
        ProfGroup profGroup=profGroupService.findById(organization.getId());
        setAttr("organization",organization).setAttr("prof_group",profGroup).render("prof_group.html");
    }

    @NotNullPara({"id"})
    public void update(){
        Long id = getParaToLong("id");
        Auth auth = authService.findById(id);
        setAttr("auth",auth).render("update.html");
    }



    public void postupdate(){
        Auth auth=getBean(Auth.class, "auth");
        Auth authid=authService.findById(auth.getId());
        if(authid== null){
            throw new BusinessException("没有这个审核");
        }
        if(!authService.update(auth)){
            throw new BusinessException("审核失败");
        }
        renderJson(RestResult.buildSuccess());
    }


    @NotNullPara({"pageNumber","pageSize"})
    public void tableData(){
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Auth auth = new Auth();
        auth.setUserId(getParaToLong("userId"));
        auth.setStatus(getPara("status"));
        auth.setType(getPara("type"));
        Page<Auth> page=authService.findPage(auth,pageNumber,pageSize);
        renderJson(new DataTable<Auth>(page));
    }
}




