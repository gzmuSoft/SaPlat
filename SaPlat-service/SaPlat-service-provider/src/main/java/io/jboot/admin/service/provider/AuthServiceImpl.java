package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AuthService;
import io.jboot.admin.service.api.UserRoleService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.service.entity.model.UserRole;
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
                List<UserRole> userRoleList = userRoleService.findByUserId(model.getUserId());
                for (UserRole role : userRoleList) {
                    if (model.getUserId().equals(role.getUserId()) && model.getRoleId().equals(role.getRoleId())) {
                        if (!model.getStatus().equals(AuthStatus.IS_VERIFY)) {
                            userRoleService.delete(role);
                        }
                        return true;
                    }
                }
                userRoleList = new ArrayList<UserRole>();
                UserRole userRole = new UserRole();
                userRole.setRoleId(model.getRoleId());
                userRole.setUserId(model.getUserId());
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
}