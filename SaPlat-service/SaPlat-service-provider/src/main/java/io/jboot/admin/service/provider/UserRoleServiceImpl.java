package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.UserRoleService;
import io.jboot.admin.service.entity.model.UserRole;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class UserRoleServiceImpl extends JbootServiceBase<UserRole> implements UserRoleService {

    @Override
    public int deleteByUserId(Long userId) {
        return Db.update("delete from sys_user_role where user_id = ?", userId);
    }

    /*
    这个bug在这里五年了
    我是它的第42个维护者
     */
    @Override
    public int[] batchSave(List<UserRole> list) {
        return Db.batchSave(list, list.size());
    }

    @Override
    public UserRole findByUserIdAndRoleId(Long userId, Long roleId) {
        Columns columns = Columns.create();
        columns.eq("userID", userId);
        columns.eq("roleID", roleId);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public List<UserRole> findListByUserId(Long userId) {
        return DAO.findListByColumn("userID", userId);
    }

    @Override
    public List<UserRole> findAll() {
        return DAO.findAll();
    }
}