package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.admin.service.api.ManagementService;
import io.jboot.admin.service.entity.model.Management;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ManagementServiceImpl extends JbootServiceBase<Management> implements ManagementService {
    @Inject
    @Override
    public Management findByUser(User user) {
        return DAO.findFirstByColumn("name", user.getName());
    }

    @Override
    public boolean update(Management management, User user) {
        return Db.tx(() -> management.update() && user.update());
    }
}