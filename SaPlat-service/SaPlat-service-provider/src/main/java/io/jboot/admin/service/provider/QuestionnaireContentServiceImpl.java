package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.QuestionnaireContentService;
import io.jboot.admin.service.entity.model.QuestionnaireContent;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class QuestionnaireContentServiceImpl extends JbootServiceBase<QuestionnaireContent> implements QuestionnaireContentService {

}