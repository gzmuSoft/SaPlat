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
import java.util.Calendar;
import java.util.Date;

@Bean
@Singleton
@JbootrpcService
public class EnterpriseServiceImpl extends JbootServiceBase<Enterprise> implements EnterpriseService {
    @Inject
    private NotificationService notificationService;

    @Inject
    private UserRoleService userRoleService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private OrganizationService organizationService;


    @Override
    public Page<Enterprise> findPage(Enterprise model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<Enterprise> findPage(Enterprise model, Date[] dates, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (StrKit.notBlank(model.getCredit())){
            columns.like("credit", "%" + model.getCredit() + "%");
        }
        if (StrKit.notNull(dates[0])){
            columns.ge("start", dates[0]);
        }
        if (StrKit.notNull(dates[1])){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dates[1]);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dates[1] = calendar.getTime();
            columns.le("start", dates[1]);
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public Enterprise findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public Enterprise findByOrgId(Long orgId) {
        return DAO.findFirstByColumn("orgID", orgId);
    }

    @Override
    public Enterprise findByCreateUserID(Object createUserID) {
        return DAO.findFirstByColumn("createUserID", createUserID);
    }

    @Override
    public boolean saveOrUpdate(Enterprise model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean saveOrUpdate(Enterprise model, Auth auth, Notification notification) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate() && notificationService.saveOrUpdate(notification));
    }

    @Override
    public boolean useOrunuse(Enterprise enterprise){
        return Db.tx(() -> {
            Organization organization=organizationService.findById(enterprise.getOrgID());
            User user = userService.findByUserIdAndUserSource(organization.getId(), 1);
            Role role = roleService.findByName("企业机构");
            UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), role.getId());
            if(userRole==null){
                return false;
            }
            userRole.setIsEnable(enterprise.getIsEnable());
            Role _role = roleService.findByName("企业机构立项");
            UserRole _userRole = userRoleService.findByUserIdAndRoleId(user.getId(), _role.getId());
            if(_userRole!=null){
                _userRole.setIsEnable(enterprise.getIsEnable());
                if(!userRoleService.update(_userRole)){
                    return false;
                }
            }
            return userRoleService.update(userRole) && enterprise.update();
        });
    }
}