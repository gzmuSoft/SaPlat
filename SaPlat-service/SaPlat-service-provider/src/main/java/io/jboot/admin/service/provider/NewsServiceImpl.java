package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.NewsService;
import io.jboot.admin.service.entity.model.News;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class NewsServiceImpl extends JbootServiceBase<News> implements NewsService {

}