package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class FilesServiceImpl extends JbootServiceBase<Files> implements FilesService {

    @Override
    public List<Files> findByPath(String... path) {
        List<Files> files = Collections.synchronizedList(new ArrayList<Files>());
        for (String p : path) {
            Files file = DAO.findFirstByColumn("path", p);
            if (file != null){
                files.add(file);
            }
        }
        return files;
    }
    @Override
    public Files saveAndGet(Files model) {
        if(!model.save()){
            return null;
        }
        return model;
    }
}