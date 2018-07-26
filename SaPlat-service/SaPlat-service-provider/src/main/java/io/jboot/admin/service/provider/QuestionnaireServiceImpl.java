package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.QuestionnaireService;
import io.jboot.admin.service.entity.model.Questionnaire;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class QuestionnaireServiceImpl extends JbootServiceBase<Questionnaire> implements QuestionnaireService {

}