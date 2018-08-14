package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.admin.service.api.EvaSchemeService;
import io.jboot.admin.service.api.FileFormService;
import io.jboot.admin.service.entity.model.FileForm;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class FileFormServiceImpl extends JbootServiceBase<FileForm> implements FileFormService {

    @Override
    public FileForm saveAndGet(FileForm model, Files files) {
        if (!model.save() && !files.update()) {
            return null;
        }
        return model;
    }

    @Override
    public FileForm findFirstByTableNameAndRecordIDAndFileName(String tableName, String fieldName, Long recordID) {
        Columns columns = Columns.create();
        columns.like("tableName", "%" + tableName.trim() + "%");
        columns.like("fieldName", "%" + fieldName.trim() + "%");
        columns.eq("recordID", recordID);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public FileForm saveOrUpdateAndGet(FileForm model, Files files) {
        if (model != null) {
            if (files == null) {
                if (model.saveOrUpdate()) {
                    return model;
                } else {
                    return null;
                }
            }
            if (Db.tx(() -> model.saveOrUpdate() && files.update())) {
                return model;
            }
        }
        return null;
    }


}