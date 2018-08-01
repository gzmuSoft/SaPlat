package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ApplyInviteService;
import io.jboot.admin.service.entity.model.ApplyInvite;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ApplyInviteServiceImpl extends JbootServiceBase<ApplyInvite> implements ApplyInviteService {

}