package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.EducationalService;
import io.jboot.admin.service.entity.model.Educational;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class EducationalServiceImpl extends JbootServiceBase<Educational> implements EducationalService {

}