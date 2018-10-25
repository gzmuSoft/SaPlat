package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ProjectExpertgroupService;
import io.jboot.admin.service.entity.model.ProjectExpertgroup;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ProjectExpertgroupServiceImpl extends JbootServiceBase<ProjectExpertgroup> implements ProjectExpertgroupService {

}