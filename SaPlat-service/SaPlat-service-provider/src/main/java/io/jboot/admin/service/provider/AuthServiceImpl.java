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
    public Auth findByUserIDAndRole(Object id, long role) {
        return DAO.findFirstByColumns(Columns.create("userId", id).eq("roleId", role));
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

        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "status desc,lastUpdTime desc");
    }


    @Override
    public boolean update(Auth model) {

        Role role = roleService.findById(model.getRoleId());
        Notification notification = new Notification();
        notification.setName("认证结果通知 ");
        notification.setSource("/app/auth/invite");
        if (model.getStatus().equals(AuthStatus.IS_VERIFY)) {
            notification.setContent("您好,您申请的 " + role.getName() + " 审核通过");
        } else {
            notification.setContent("您好,您申请的 " + role.getName() + " 审核不通过");
        }
        notification.setReceiverID(Math.toIntExact(model.getUserId()));
        notification.setCreateUserID(userService.findByName(model.getLastUpdUser()).getId());
        notification.setCreateTime(new Date());
        notification.setLastUpdateUserID(userService.findByName(model.getLastUpdUser()).getId());
        notification.setLastAccessTime(new Date());
        notification.setIsEnable(true);
        notification.setStatus(0);

        User user = userService.findById(model.getUserId());
        ExpertGroup expertGroup[] = new ExpertGroup[1];
        FacAgency facAgency[] =  new FacAgency[1];
        Management management[] =  new Management[1];
        Enterprise enterprise[] =  new Enterprise[1];
        ReviewGroup reviewGroup[] =  new ReviewGroup[1];
        ProfGroup profGroup[] =  new ProfGroup[1];
        if ("专家团体".equals(role.getName())) {
            expertGroup[0] = expertGroupService.findByPersonId(user.getUserID());
            expertGroup[0].setIsEnable(true);
        } else if ("服务机构".equals(role.getName())) {
            facAgency[0] = facAgencyService.findByOrgId(user.getUserID());
            facAgency[0].setIsEnable(true);
        } else if ("管理机构".equals(role.getName())) {
            management[0] = managementService.findByOrgId(user.getUserID());
            management[0].setIsEnable(true);
        } else if ("企业机构".equals(role.getName())) {
            enterprise[0] = enterpriseService.findByOrgId(user.getUserID());
            enterprise[0].setIsEnable(true);
        } else if ("审查团体".equals(role.getName())) {
            reviewGroup[0] = reviewGroupService.findByOrgId(user.getUserID());
            reviewGroup[0].setIsEnable(true);
        } else if ("专业团体".equals(role.getName())) {
            profGroup[0] = profGroupService.findByOrgId(user.getUserID());
            profGroup[0].setIsEnable(true);
        }

        List<UserRole> userRoleList = userRoleService.findListByUserId(model.getUserId());

        return Db.tx(() -> {
            model.setLastUpdTime(new Date());
            if (!model.update()) {
                return false;
            }

            if (!notification.save()) {
                return false;
            }
            if ((!model.getStatus().equals(AuthStatus.IS_VERIFY)) && (!role.getName().contains("立项"))) {
                return true;
            }

            if ("专家团体".equals(role.getName())) {
                if (!expertGroupService.update(expertGroup[0])) {
                    return false;
                }
            } else if ("服务机构".equals(role.getName())) {
                if (!facAgencyService.update(facAgency[0])) {
                    return false;
                }
            } else if ("管理机构".equals(role.getName())) {
                if (!managementService.update(management[0])) {
                    return false;
                }
            } else if ("企业机构".equals(role.getName())) {
                if (!enterpriseService.update(enterprise[0])) {
                    return false;
                }
            } else if ("审查团体".equals(role.getName())) {
                if (!reviewGroupService.update(reviewGroup[0])) {
                    return false;
                }
            } else if ("专业团体".equals(role.getName())) {
                if (!profGroupService.update(profGroup[0])) {
                    return false;
                }
            }

            for (UserRole roleItem : userRoleList) {
                if (model.getUserId().equals(roleItem.getUserID()) && model.getRoleId().equals(roleItem.getRoleID())) {
                    if (!model.getStatus().equals(AuthStatus.IS_VERIFY)) {
                        userRoleService.delete(roleItem);
                    }
                    return true;
                }
            }

            List<UserRole> oldUserRoleList = new ArrayList<UserRole>();
            UserRole userRole = new UserRole();
            userRole.setRoleID(model.getRoleId());
            userRole.setUserID(model.getUserId());
            oldUserRoleList.add(userRole);
            int[] rets = userRoleService.batchSave(oldUserRoleList);
            for (int ret : rets) {
                if (ret < 1) {
                    return false;
                }
            }
            return true;
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