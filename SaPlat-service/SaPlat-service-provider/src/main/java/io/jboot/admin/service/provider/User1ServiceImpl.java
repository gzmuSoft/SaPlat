package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.User1Service;
import io.jboot.admin.service.entity.model.User1;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class User1ServiceImpl extends JbootServiceBase<User1> implements User1Service {

}