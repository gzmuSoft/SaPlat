package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
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
    public List<FileProject> findAllByProjectID(Long id) {
        Columns columns = Columns.create();
        columns.eq("projectID", id);
        return DAO.findListByColumns(columns);
    }

    @Override
    public FileProject findByProjectIDAndProjectFileID(Long projectID, Long projectFileID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("fileTypeID", projectFileID);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public Page<FileProject> findPage(FileProject fileProject, int pageNumber, int pageSize){
        Columns columns=Columns.create();
        if(fileProject.getFileTypeID()!=null){
            columns.eq("fileTypeID",fileProject.getFileTypeID());
        }
        if(fileProject.getProjectID()!=null){
            columns.eq("projectID",fileProject.getProjectID());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }

    @Override
    public boolean deleteFileProjectAndFiles(FileProject model){
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

}