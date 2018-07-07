package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.RoleRes1Service;
import io.jboot.admin.service.entity.model.RoleRes1;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class RoleRes1ServiceImpl extends JbootServiceBase<RoleRes1> implements RoleRes1Service {

}