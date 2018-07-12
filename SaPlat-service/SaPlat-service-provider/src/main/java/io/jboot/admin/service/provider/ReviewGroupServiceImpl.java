package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.ReviewGroupService;
import io.jboot.admin.service.entity.model.Management;
import io.jboot.admin.service.entity.model.ReviewGroup;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ReviewGroupServiceImpl extends JbootServiceBase<ReviewGroup> implements ReviewGroupService {
    @Override
    public ReviewGroup findByOrgID(Long orgID) {
        return DAO.findFirstByColumn("orgID", orgID);
    }

}