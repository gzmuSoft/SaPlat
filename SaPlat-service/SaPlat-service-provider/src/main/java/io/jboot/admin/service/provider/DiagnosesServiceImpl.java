package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.DiagnosesService;
import io.jboot.admin.service.entity.model.Diagnoses;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class DiagnosesServiceImpl extends JbootServiceBase<Diagnoses> implements DiagnosesService {

}