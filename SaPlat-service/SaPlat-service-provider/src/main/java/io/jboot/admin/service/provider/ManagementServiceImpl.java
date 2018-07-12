package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.ManagementService;
import io.jboot.admin.service.entity.model.Management;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ManagementServiceImpl extends JbootServiceBase<Management> implements ManagementService {

    @Override
    public Management findByOrgID(Long orgID) {
        return DAO.findFirstByColumn("orgID", orgID);
    }


}