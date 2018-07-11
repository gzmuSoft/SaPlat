package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.Role1Service;
import io.jboot.admin.service.entity.model.Role1;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class Role1ServiceImpl extends JbootServiceBase<Role1> implements Role1Service {

}