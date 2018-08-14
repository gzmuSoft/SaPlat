package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Bean
@Singleton
@JbootrpcService
public class AuthServiceImpl extends JbootServiceBase<Auth> implements AuthService {

    @JbootrpcService
    private UserRoleService userRoleService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private ExpertGroupService expertGroupService;

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


    @Override
    public Auth findByUser(User user) {
        return DAO.findFirstByColumn("userId", user.getId());
    }

    @Override
    public Auth findByUserAndRole(User user, long role) {
        return DAO.findFirstByColumns(Columns.create("userId", user.getId()).eq("roleId", role));
    }

    @Override
    public List<Auth> findByUserAndType(User user, String typeStatus) {
        Columns columns = Columns.create();
        columns.eq("userId", user.getId());
        columns.eq("type", typeStatus);
        return DAO.findListByColumns(columns);
    }

    @Override
    public Auth findByUserIdAndStatus(Long userId, String status) {
        Columns columns = new Columns();
        columns.eq("userId", userId);
        columns.eq("status", status);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public List<Auth> findListByUserIdAndStatusAndType(Long userId, String status, String type) {
        Columns columns = new Columns();
        columns.eq("userId", userId);
        columns.eq("status", status);
        columns.eq("type", type);
        return DAO.findListByColumns(columns);
    }

    @Override
    public List<Auth> findByUserIdAndStatusToList(Long userId, String status) {
        Columns columns = new Columns();
        columns.eq("userId", userId);
        columns.eq("status", status);
        return DAO.findListByColumns(columns);
    }


    @Override
    public Page<Auth> findPage(Auth auth, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (auth.getStatus() != null) {
            columns.like("status", "%" + auth.getStatus() + "%");
        }
        if (auth.getUserId() != null) {
            columns.eq("userId", auth.getUserId());
        }
        if (auth.getName() != null) {
            columns.like("name", "%" + auth.getName() + "%");
        }
        if (auth.getType() != null) {
            columns.like("type", "%" + auth.getType() + "%");
        } else {
            columns.lt("type", "2");
        }

        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "-lastUpdTime");
    }


    @Override
    public boolean update(Auth model) {
        return Db.tx(() -> {

            model.setLastUpdTime(new Date());
            if (!model.update()) {
                return false;
            }

            Role role1 = roleService.findById(model.getRoleId());
            Notification notification = new Notification();
            notification.setName("认证结果通知 ");
            notification.setSource("/app/project/invite");
            if (model.getStatus().equals(AuthStatus.IS_VERIFY)) {
                notification.setContent("您好,您申请的 " + role1.getName() + " 审核通过");
            } else {
                notification.setContent("您好,您申请的 " + role1.getName() + " 审核不通过");
            }
            notification.setReceiverID(Math.toIntExact(model.getUserId()));
            notification.setCreateUserID(userService.findByName(model.getLastUpdUser()).getId());
            notification.setCreateTime(new Date());
            notification.setLastUpdateUserID(userService.findByName(model.getLastUpdUser()).getId());
            notification.setLastAccessTime(new Date());
            notification.setIsEnable(true);
            notification.setStatus(0);
            if (!notification.save()) {
                return false;
            }
            if (!model.getStatus().equals(AuthStatus.IS_VERIFY)) {
                return true;
            }
            User user = userService.findById(model.getUserId());
            if ("专家团体".equals(role1.getName())) {
                ExpertGroup expertGroup = expertGroupService.findByPersonId(user.getUserID());
                expertGroup.setIsEnable(true);
                if (!expertGroupService.update(expertGroup)) {
                    return false;
                }
            } else if ("服务机构".equals(role1.getName())) {
                FacAgency facAgency = facAgencyService.findByOrgId(user.getUserID());
                facAgency.setIsEnable(true);
                if (!facAgencyService.update(facAgency)) {
                    return false;
                }
            } else if ("管理机构".equals(role1.getName())) {
                Management management = managementService.findByOrgId(user.getUserID());
                management.setIsEnable(true);
                if (!managementService.update(management)) {
                    return false;
                }
            } else if ("企业机构".equals(role1.getName())) {
                Enterprise enterprise = enterpriseService.findByOrgId(user.getUserID());
                enterprise.setIsEnable(true);
                if (!enterpriseService.update(enterprise)) {
                    return false;
                }
            } else if ("审查团体".equals(role1.getName())) {
                ReviewGroup reviewGroup = reviewGroupService.findByOrgId(user.getUserID());
                reviewGroup.setIsEnable(true);
                if (!reviewGroupService.update(reviewGroup)) {
                    return false;
                }
            } else if ("专业团体".equals(role1.getName())) {
                ProfGroup profGroup = profGroupService.findByOrgId(user.getUserID());
                profGroup.setIsEnable(true);
                if (!profGroupService.update(profGroup)) {
                    return false;
                }
            }
            List<UserRole> userRoleList = new ArrayList<UserRole>();
            UserRole userRole = new UserRole();
            userRole.setRoleID(model.getRoleId());
            userRole.setUserID(model.getUserId());
            userRoleList.add(userRole);
            int[] rets = userRoleService.batchSave(userRoleList);
            for (int ret : rets) {
                if (ret < 1) {
                    return false;
                }
            }
            user.setStatus("3");
            return userService.update(user);
        });
    }

    @Override
    public Auth findByUserIdAndStatusAndType(Long userId, String status, String type) {
        Columns columns = new Columns();
        columns.eq("userId", userId);
        columns.eq("status", status);
        columns.eq("type", type);
        return DAO.findFirstByColumns(columns);
    }
}