package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.Res1Service;
import io.jboot.admin.service.entity.model.Res1;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class Res1ServiceImpl extends JbootServiceBase<Res1> implements Res1Service {

}