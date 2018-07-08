package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.RiskLevelService;
import io.jboot.admin.service.entity.model.RiskLevel;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class RiskLevelServiceImpl extends JbootServiceBase<RiskLevel> implements RiskLevelService {

}