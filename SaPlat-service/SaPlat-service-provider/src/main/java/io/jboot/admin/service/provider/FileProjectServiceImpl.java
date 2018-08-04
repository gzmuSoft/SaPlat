package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author ASUS
 */
@Bean
@Singleton
@JbootrpcService
public class FileProjectServiceImpl extends JbootServiceBase<FileProject> implements FileProjectService {

    @Inject
    private FilesService filesService;

    @Override
    public List<FileProject> findListById(Long id) {
        return DAO.findListByColumn(Column.create("parentID",id));
    }

    @Override
    public List<FileProject> findAllByProjectID(Long id) {
        Columns columns = Columns.create();
        columns.eq("projectID", id);
        return DAO.findListByColumns(columns);
    }

    @Override
    public FileProject findByProjectID(Long projectId) {
        return DAO.findFirstByColumn(Column.create("projectID", projectId));
    }


    @Override
    public List<FileProject> findListByFileTypeIDAndProjectID(Long fileTypeID,Long projectID){
        Columns columns = Columns.create();
        columns.eq("projectID",projectID);
        columns.eq("fileTypeID",fileTypeID);
        return DAO.findListByColumns(columns);
    }

    @Override
    public FileProject findByProjectIDAndFileTypeID(Long projectID, Long fileTypeID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("fileTypeID", fileTypeID);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public Page<FileProject> findPage(FileProject fileProject, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (fileProject.getFileTypeID() != null) {
            columns.eq("fileTypeID", fileProject.getFileTypeID());
        }
        if (fileProject.getProjectID() != null) {
            columns.eq("projectID", fileProject.getProjectID());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }

    @Override
    public boolean deleteFileProjectAndFiles(FileProject model) {
        return Db.tx(() -> {
            if (!delete(model)) {
                return false;
            }
            Files files = filesService.findById(model.getFileID());
            files.setIsEnable(false);
            return filesService.update(files);
        });
    }

    @Override
    public boolean saveFileProjectAndFiles(FileProject model) {
        return Db.tx(() -> {
            if (!save(model)) {
                return false;
            }
            Files files = filesService.findById(model.getFileID());
            files.setIsEnable(true);
            return filesService.update(files);
        });
    }

    @Override
    public boolean updateFileProjectAndFiles(FileProject model){
        return Db.tx(() -> {
            if (!update(model)) {
                return false;
            }
            Files files = filesService.findById(model.getFileID());
            files.setIsEnable(true);
            return filesService.update(files);
        });
    }


    @Override
    public FileProject findByFileTypeIdAndProjectId(Long fileTypeId, Long projectId) {
        Columns columns = Columns.create();
        columns.eq("fileTypeID", fileTypeId);
        columns.eq("projectID", projectId);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public boolean update(FileProject model, Files files) {
        return Db.tx(() -> model.update() && files.update());
    }

    @Override
    public FileProject saveAndGet(FileProject model){
        if(!model.save()){
            return null;
        }
        return model;
    }
}