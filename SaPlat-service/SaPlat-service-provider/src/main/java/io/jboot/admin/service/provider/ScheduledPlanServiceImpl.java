package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ScheduledPlanService;
import io.jboot.admin.service.entity.model.ScheduledPlan;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ScheduledPlanServiceImpl extends JbootServiceBase<ScheduledPlan> implements ScheduledPlanService {

    @Override
    public ScheduledPlan findByEvaSchemeID(Long evaSchemeID) {
        return DAO.findFirstByColumn(Column.create("evaSchemeID",evaSchemeID));
    }

    @Override
    public List<ScheduledPlan> findListByEvaSchemeID(Long evaschemeID) {
        return DAO.findListByColumn(Column.create("evaSchemeID",evaschemeID));
    }
}