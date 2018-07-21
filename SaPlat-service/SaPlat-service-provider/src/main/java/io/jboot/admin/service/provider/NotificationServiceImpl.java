package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class NotificationServiceImpl extends JbootServiceBase<Notification> implements NotificationService {

}