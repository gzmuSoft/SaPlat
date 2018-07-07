package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.EnterpriseService;
import io.jboot.admin.service.entity.model.Enterprise;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class EnterpriseServiceImpl extends JbootServiceBase<Enterprise> implements EnterpriseService {

}