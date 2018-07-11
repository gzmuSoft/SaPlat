package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ProjectServiceImpl extends JbootServiceBase<Project> implements ProjectService {

}