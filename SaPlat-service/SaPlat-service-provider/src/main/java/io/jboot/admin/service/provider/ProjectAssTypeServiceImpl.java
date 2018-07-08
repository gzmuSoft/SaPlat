package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ProjectAssTypeService;
import io.jboot.admin.service.entity.model.ProjectAssType;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ProjectAssTypeServiceImpl extends JbootServiceBase<ProjectAssType> implements ProjectAssTypeService {

}