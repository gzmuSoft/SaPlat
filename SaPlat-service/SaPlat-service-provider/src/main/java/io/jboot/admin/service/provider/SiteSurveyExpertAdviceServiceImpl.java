package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.SiteSurveyExpertAdviceService;
import io.jboot.admin.service.entity.model.SiteSurveyExpertAdvice;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class SiteSurveyExpertAdviceServiceImpl extends JbootServiceBase<SiteSurveyExpertAdvice> implements SiteSurveyExpertAdviceService {

}