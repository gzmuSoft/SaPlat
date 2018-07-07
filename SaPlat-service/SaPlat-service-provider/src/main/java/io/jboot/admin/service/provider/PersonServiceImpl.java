package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class PersonServiceImpl extends JbootServiceBase<Person> implements PersonService {

}