package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ManagementService;
import io.jboot.admin.service.entity.model.Management;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ManagementServiceImpl extends JbootServiceBase<Management> implements ManagementService {

}