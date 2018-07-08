package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.CountryService;
import io.jboot.admin.service.entity.model.Country;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class CountryServiceImpl extends JbootServiceBase<Country> implements CountryService {

}