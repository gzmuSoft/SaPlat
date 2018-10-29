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
public class FacAgencyServiceImpl extends JbootServiceBase<FacAgency> implements FacAgencyService {
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
    public FacAgency findByOrgId(Long orgID) {
        return DAO.findFirstByColumn("orgID", orgID);
    }

    @Override
    public FacAgency findByCreateUserID(Object createUserID) {
        return DAO.findFirstByColumn("createUserID", createUserID);
    }

    @Override
    public Page<FacAgency> findPage(FacAgency model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<FacAgency> findPage(FacAgency model, Date[] dates, int pageNumber, int pageSize) {
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
    public Page<FacAgency> findPageExcludeByOrgID(long orgID,int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        columns.ne("orgID", orgID);
        columns.eq("isEnable", 1);
        return DAO.paginateByColumns(pageNumber, pageSize,columns.getList(), "id desc");
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public FacAgency findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public boolean saveOrUpdate(FacAgency model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean saveOrUpdate(FacAgency model, Auth auth, Notification notification) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate() && notificationService.saveOrUpdate(notification));
    }

    @Override
    public boolean useOrUnuse(FacAgency facAgency) {
        return Db.tx(() -> {
            Organization organization=organizationService.findById(facAgency.getOrgID());
            User user = userService.findByUserIdAndUserSource(organization.getId(), 1);
            Role role = roleService.findByName("服务机构");
            UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), role.getId());
            if(userRole==null){
                return false;
            }
            userRole.setIsEnable(facAgency.getIsEnable());
            return userRoleService.update(userRole) && facAgency.update();
        });
    }

}