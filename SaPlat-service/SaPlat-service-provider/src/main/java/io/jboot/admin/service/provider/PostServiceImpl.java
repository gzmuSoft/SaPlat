package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PostService;
import io.jboot.admin.service.entity.model.Post;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class PostServiceImpl extends JbootServiceBase<Post> implements PostService {

}