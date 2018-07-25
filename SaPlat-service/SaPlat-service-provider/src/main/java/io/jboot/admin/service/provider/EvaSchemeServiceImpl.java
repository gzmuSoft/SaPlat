package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.EvaSchemeService;
import io.jboot.admin.service.entity.model.EvaScheme;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class EvaSchemeServiceImpl extends JbootServiceBase<EvaScheme> implements EvaSchemeService {

}