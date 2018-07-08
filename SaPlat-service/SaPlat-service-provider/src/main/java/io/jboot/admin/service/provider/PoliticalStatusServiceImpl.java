package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PoliticalStatusService;
import io.jboot.admin.service.entity.model.PoliticalStatus;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class PoliticalStatusServiceImpl extends JbootServiceBase<PoliticalStatus> implements PoliticalStatusService {

}