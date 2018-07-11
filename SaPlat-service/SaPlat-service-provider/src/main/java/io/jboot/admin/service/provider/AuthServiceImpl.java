package io.jboot.admin.service.provider;

import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.AuthService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class AuthServiceImpl extends JbootServiceBase<Auth> implements AuthService {

    @Override
    public Auth findByUser(User user) {
        return DAO.findFirstByColumn("userId", user.getId());
    }
}