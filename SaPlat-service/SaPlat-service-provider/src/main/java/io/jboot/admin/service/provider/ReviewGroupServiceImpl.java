package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ReviewGroupService;
import io.jboot.admin.service.entity.model.ReviewGroup;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ReviewGroupServiceImpl extends JbootServiceBase<ReviewGroup> implements ReviewGroupService {

}