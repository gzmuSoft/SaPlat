package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.FacAgencyService;
import io.jboot.admin.service.entity.model.FacAgency;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class FacAgencyServiceImpl extends JbootServiceBase<FacAgency> implements FacAgencyService {

}