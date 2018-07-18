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
import java.util.Date;

@Bean
@Singleton
@JbootrpcService
public class OrganizationServiceImpl extends JbootServiceBase<Organization> implements OrganizationService {
    @Inject
    private UserService userService;

    @Override
    public boolean save(Organization organization) {
        organization.setCreateTime(new Date());
        organization.setLastAccessTime(new Date());
        organization.setIsEnable(true);
        organization.setCertificate("#/");
        return Db.tx(organization::save);
    }

    @Override
    public boolean saveOrganization(Organization model, User user, Long[] roles) {
        return Db.tx(() -> {
            if (!save(model)){
                return false;
            }
            user.setUserID(findByName(model.getName()).getId());
            return userService.saveUser(user,roles);
        });
    }

    @Override
    public boolean hasUser(String name) {
        return findByName(name) != null;
    }

    @Override
    public Organization findByName(String name) {
        return DAO.findFirstByColumn("name",name);
    }

    @Override
    public boolean update(Organization organization, User user) {
        return Db.tx(() -> organization.update() && user.update());
    }

}