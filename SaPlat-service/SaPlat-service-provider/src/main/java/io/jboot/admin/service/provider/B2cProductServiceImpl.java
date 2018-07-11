package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.B2cProductService;
import io.jboot.admin.service.entity.model.B2cProduct;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class B2cProductServiceImpl extends JbootServiceBase<B2cProduct> implements B2cProductService {

}