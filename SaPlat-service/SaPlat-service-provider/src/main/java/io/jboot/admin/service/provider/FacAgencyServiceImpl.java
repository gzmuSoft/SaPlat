package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.FacAgencyService;
import io.jboot.admin.service.entity.model.FacAgency;
import io.jboot.admin.service.entity.model.Management;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class FacAgencyServiceImpl extends JbootServiceBase<FacAgency> implements FacAgencyService {
    @Override
    public FacAgency findByOrgID(Long orgID) {
        return DAO.findFirstByColumn("orgID", orgID);
    }
}