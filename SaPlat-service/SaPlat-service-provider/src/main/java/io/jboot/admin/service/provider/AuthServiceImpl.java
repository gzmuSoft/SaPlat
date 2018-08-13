package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AuthService;
import io.jboot.admin.service.api.RoleService;
import io.jboot.admin.service.api.UserRoleService;
import io.jboot.admin.service.api.UserService;
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

        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "-status");
    }


    @Override
    public boolean update(Auth model) {
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                model.setLastUpdTime(new Date());
                if (!model.update()) {
                    return false;
                }

                Role role1 = roleService.findById(model.getRoleId());
                Notification notification = new Notification();
                notification.setName("认证结果通知 ");
                notification.setSource("/app/project/invite");
                if (model.getStatus().equals(AuthStatus.IS_VERIFY)) {
                    notification.setContent("您好,您认证的" + role1.getName() + "认证成功");
                } else {
                    notification.setContent("您好,您认证的" + role1.getName() + "认证失败");
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
                List<UserRole> userRoleList = userRoleService.findListByUserId(model.getUserId());
                for (UserRole role : userRoleList) {
                    if (model.getUserId().equals(role.getUserID()) && model.getRoleId().equals(role.getRoleID())) {
                        if (!model.getStatus().equals(AuthStatus.IS_VERIFY)) {
                            userRoleService.delete(role);
                        }
                        return true;
                    }
                }
                userRoleList = new ArrayList<UserRole>();
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
                return true;
            }
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