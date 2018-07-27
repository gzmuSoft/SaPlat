package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class FileProjectServiceImpl extends JbootServiceBase<FileProject> implements FileProjectService {
    @Override
    public FileProject findByProjectID(Long id){
        Columns columns = Columns.create();
        columns.eq("projectID",id);
        return DAO.findFirstByColumns(columns);
    }
}