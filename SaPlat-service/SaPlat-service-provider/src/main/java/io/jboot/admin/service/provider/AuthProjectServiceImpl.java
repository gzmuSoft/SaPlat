package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.AuthProjectService;
import io.jboot.admin.service.entity.model.AuthProject;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class AuthProjectServiceImpl extends JbootServiceBase<AuthProject> implements AuthProjectService {
    @Override
    public AuthProject findByProjectId(Object projectId) {
        return DAO.findFirstByColumn("projectId", projectId);
    }
}