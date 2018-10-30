package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ReviewGroupServiceImpl extends JbootServiceBase<ReviewGroup> implements ReviewGroupService {
    @Inject
    private NotificationService notificationService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private UserRoleService userRoleService;

    @Override
    public ReviewGroup findByOrgId(Long orgId) {
        return DAO.findFirstByColumn("orgID", orgId);
    }

    @Override
    public ReviewGroup findByCreateUserID(Object createUserID) {
        return DAO.findFirstByColumn("createUserID", createUserID);
    }

    @Override
    public boolean saveOrUpdate(ReviewGroup model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean useOrUnuse(ReviewGroup reviewGroup){
        return Db.tx(() -> {
            Organization organization=organizationService.findById(reviewGroup.getOrgID());
            User user = userService.findByUserIdAndUserSource(organization.getId(), 1);
            Role role = roleService.findByName("审查团体");
            UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), role.getId());
            if(userRole==null){
                return false;
            }
            userRole.setIsEnable(reviewGroup.getIsEnable());
            Role _role = roleService.findByName("审查团体立项");
            UserRole _userRole = userRoleService.findByUserIdAndRoleId(user.getId(), _role.getId());
            if(_userRole!=null){
                _userRole.setIsEnable(reviewGroup.getIsEnable());
                if(!userRoleService.update(_userRole)){
                    return false;
                }
            }
            return userRoleService.update(userRole) && reviewGroup.update();
        });
    }
    @Override
    public boolean saveOrUpdate(ReviewGroup model, Auth auth, Notification notification) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate() && notificationService.saveOrUpdate(notification));
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public ReviewGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }


    @Override
    public Page<ReviewGroup> findPage(ReviewGroup reviewGroup, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (reviewGroup.getIsEnable() != null) {
            columns.eq("isEnable", reviewGroup.getIsEnable());
        }
        if (StrKit.notBlank(reviewGroup.getName())) {
            columns.like("name", "%" + reviewGroup.getName() + "%");
        }
        if (StrKit.notBlank(reviewGroup.getAdministrator())){
            columns.like("administrator", "%" + reviewGroup.getAdministrator() + "%");
        }
        if (StrKit.notNull(reviewGroup.getIsEnable())){
            columns.eq("isEnable", reviewGroup.getIsEnable());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }
}