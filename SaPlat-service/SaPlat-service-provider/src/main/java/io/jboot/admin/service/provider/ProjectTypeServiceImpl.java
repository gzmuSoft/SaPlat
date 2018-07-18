package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.ProjectTypeService;
import io.jboot.admin.service.entity.model.ProjectType;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ProjectTypeServiceImpl extends JbootServiceBase<ProjectType> implements ProjectTypeService {

}