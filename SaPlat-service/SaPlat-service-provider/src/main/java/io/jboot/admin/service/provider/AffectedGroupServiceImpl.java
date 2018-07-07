package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.AffectedGroupService;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class AffectedGroupServiceImpl extends JbootServiceBase<AffectedGroup> implements AffectedGroupService {

}