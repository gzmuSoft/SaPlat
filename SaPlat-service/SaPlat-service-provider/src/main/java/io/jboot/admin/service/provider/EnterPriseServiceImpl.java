package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.EnterPriseService;
import io.jboot.admin.service.entity.model.EnterPrise;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class EnterPriseServiceImpl extends JbootServiceBase<EnterPrise> implements EnterPriseService {

}