package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.AuthService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;


@Bean
@Singleton
@JbootrpcService
public class AuthServiceImpl extends JbootServiceBase<Auth> implements AuthService {

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
        columns.eq("userId",user.getId());
        columns.eq("type",typeStatus);
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
        return null;
    }

    @Override
    public List<Auth> findByUserIdAndStatusToList(Long userId, String status) {
        Columns columns = new Columns();
        columns.eq("userId", userId);
        columns.eq("status", status);
        return DAO.findListByColumns(columns);
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