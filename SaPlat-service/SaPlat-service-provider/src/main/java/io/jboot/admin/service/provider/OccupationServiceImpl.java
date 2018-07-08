package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.OccupationService;
import io.jboot.admin.service.entity.model.Occupation;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class OccupationServiceImpl extends JbootServiceBase<Occupation> implements OccupationService {

}