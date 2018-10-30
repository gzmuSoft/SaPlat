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
public class ProfGroupServiceImpl extends JbootServiceBase<ProfGroup> implements ProfGroupService {
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
    public ProfGroup findByOrgId(Long orgId) {
        return DAO.findFirstByColumn("orgID", orgId);
    }

    @Override
    public ProfGroup findByCreateUserID(Object createUserID) {
        return DAO.findFirstByColumn("createUserID", createUserID);
    }

    @Override
    public boolean useOrUnuse(ProfGroup profGroup) {
        return Db.tx(() -> {
            Organization organization=organizationService.findById(profGroup.getOrgID());
            User user = userService.findByUserIdAndUserSource(organization.getId(), 1);
            Role role = roleService.findByName("专业团体");
            UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), role.getId());
            if(userRole==null){
                return false;
            }
            userRole.setIsEnable(profGroup.getIsEnable());
            return userRoleService.update(userRole) && profGroup.update();
        });
    }


    @Override
    public boolean saveOrUpdate(ProfGroup model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean saveOrUpdate(ProfGroup model, Auth auth, Notification notification) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate() && notificationService.saveOrUpdate(notification));
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public ProfGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public Page<ProfGroup> findPage(ProfGroup profGroup, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (profGroup.getIsEnable() != null) {
            columns.eq("isEnable", profGroup.getIsEnable());
        }
        if (StrKit.notBlank(profGroup.getName())) {
            columns.like("name", "%" + profGroup.getName() + "%");
        }
        if (StrKit.notBlank(profGroup.getAdministrator())){
            columns.like("administrator", "%" + profGroup.getAdministrator() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }
}