package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ScheduledPlanService;
import io.jboot.admin.service.entity.model.ScheduledPlan;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ScheduledPlanServiceImpl extends JbootServiceBase<ScheduledPlan> implements ScheduledPlanService {

}