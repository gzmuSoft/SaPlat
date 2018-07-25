package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.ProAssReviewService;
import io.jboot.admin.service.entity.model.ProAssReview;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ProAssReviewServiceImpl extends JbootServiceBase<ProAssReview> implements ProAssReviewService {

}