package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.NationService;
import io.jboot.admin.service.entity.model.Nation;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class NationServiceImpl extends JbootServiceBase<Nation> implements NationService {

}