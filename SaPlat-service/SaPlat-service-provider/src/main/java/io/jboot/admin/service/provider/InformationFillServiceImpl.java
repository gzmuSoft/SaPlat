package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.InformationFillService;
import io.jboot.admin.service.entity.model.InformationFill;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class InformationFillServiceImpl extends JbootServiceBase<InformationFill> implements InformationFillService {

}