package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.FileFormService;
import io.jboot.admin.service.entity.model.FileForm;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class FileFormServiceImpl extends JbootServiceBase<FileForm> implements FileFormService {

}