package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.OrganizationService;
import io.jboot.admin.service.api.RoleService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.Organization;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.validator.app.OrganizationValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * -----------------------------
 *
 * @author LiuChuanjin
 * @version 2.0
 * -----------------------------
 * @date 23:44 2018/7/6
 */
@RequestMapping("/app/organization")
public class OrganizationController extends BaseController{
    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private OrganizationService organizationService ;

    @JbootrpcService
    private RoleService roleService;

    private HttpServletRequest request;
    private HttpServletResponse response;

    public void register(){
        render("register.html");
    }

    @Before({POST.class, OrganizationValidator.class})
    public void postRegister(){
        Organization organization = getBean(Organization.class, "organization");
        if (userService.hasUser(organization.getName())){
            renderJson(RestResult.buildError("用户名已存在"));
            throw new BusinessException("用户名已存在");
        }
        organization.setCreateTime(new Date());
        organization.setLastAccessTime(new Date());
        organization.setIsEnable(1);
        User user = getBean(User.class,"user");
        Long[] roles = new Long[]{roleService.findByName("组织机构").getId()};

        user.setName(organization.getName());
        user.setPhone(organization.getContact());
        if(!organizationService.saveOrganization(organization,user,roles)){
            renderJson(RestResult.buildError("用户保存失败"));
            throw new BusinessException("用户保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
