package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.admin.service.api.OrganizationService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.Organization;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class OrganizationServiceImpl extends JbootServiceBase<Organization> implements OrganizationService {

    @Inject
    private UserService userService;

    @Override
    public boolean saveOrganization(Organization model, User user, Long[] roles) {
        return Db.tx(() -> userService.saveUser(user, roles) && save(model));
    }
}