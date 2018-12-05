package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.QuestionnaireFilesService;
import io.jboot.admin.service.entity.model.QuestionnaireFiles;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class QuestionnaireFilesServiceImpl extends JbootServiceBase<QuestionnaireFiles> implements QuestionnaireFilesService {

}