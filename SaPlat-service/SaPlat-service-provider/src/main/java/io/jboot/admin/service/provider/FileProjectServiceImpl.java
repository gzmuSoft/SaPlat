package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class FileProjectServiceImpl extends JbootServiceBase<FileProject> implements FileProjectService {

}