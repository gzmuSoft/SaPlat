package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

/**
 * @author ASUS
 */
@Bean
@Singleton
@JbootrpcService
public class FileProjectServiceImpl extends JbootServiceBase<FileProject> implements FileProjectService {

    @Override
    public List<FileProject> findAllByProjectID(Long id) {
        Columns columns = Columns.create();
        columns.eq("projectID", id);
        return DAO.findListByColumns(columns);
    }

    @Override
    public FileProject findByProjectID(Long projectId) {
        return DAO.findFirstByColumn(Column.create("projectID",projectId));
    }

    @Override
    public FileProject findByProjectIDAndProjectFileID(Long projectID, Long projectFileID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("fileTypeID", projectFileID);
        return DAO.findFirstByColumns(columns);
    }

}