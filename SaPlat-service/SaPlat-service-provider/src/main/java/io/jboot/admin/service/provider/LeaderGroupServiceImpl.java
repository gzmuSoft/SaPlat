package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.LeaderGroupService;
import io.jboot.admin.service.entity.model.LeaderGroup;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class LeaderGroupServiceImpl extends JbootServiceBase<LeaderGroup> implements LeaderGroupService {

    @Override
    public LeaderGroup findByProject(Long projectID) {
        return DAO.findFirstByColumn("projectID", projectID);
    }
}