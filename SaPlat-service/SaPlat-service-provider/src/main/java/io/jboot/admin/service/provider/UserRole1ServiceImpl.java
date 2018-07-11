package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.UserRole1Service;
import io.jboot.admin.service.entity.model.UserRole1;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class UserRole1ServiceImpl extends JbootServiceBase<UserRole1> implements UserRole1Service {

}