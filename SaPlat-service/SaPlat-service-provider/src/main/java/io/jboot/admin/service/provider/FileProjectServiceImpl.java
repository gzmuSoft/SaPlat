package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.api.SaPlatService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;

/**
 * @author ASUS
 */
@Bean
@Singleton
@JbootrpcService
public class FileProjectServiceImpl extends JbootServiceBase<FileProject> implements FileProjectService{

    @Inject
    private FilesService filesService;

    @Inject
    private ProjectService projectService;

    /**
     * 装配完善 list 对象中所有对象的数据
     * @param list
     * @return
     */
    public List<FileProject> fitList(List<FileProject> list){
        if(list != null){
            for (FileProject item: list) {
                fitModel(item);
            }
        }
        return list;
    }

    /**
     * 装配完善 Page 对象中所有对象的数据
     * @param page
     * @return
     */
    public Page<FileProject> fitPage(Page<FileProject> page){
        if(page != null){
            List<FileProject> tList = page.getList();
            for (FileProject item: tList) {
                fitModel(item);
            }
        }
        return page;
    }

    /**
     * 装配单个实体对象的数据
     * @param model
     * @return
     */
    public FileProject fitModel(FileProject model){
        if(model != null) {
            Files fModel = filesService.findById(model.getFileID());
            if(fModel != null)
                model.setOrginalFileName(fModel.getName());
        }
        return model;
    }
    
    @Override
    public List<FileProject> findListById(Long id) {
        return fitList(DAO.findListByColumn(Column.create("parentID", id)));
    }

    @Override
    public List<FileProject> findAllByProjectID(Long id) {
        Columns columns = Columns.create();
        columns.eq("projectID", id);
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public FileProject findByFileID(Long fileId) {
        return fitModel(DAO.findFirstByColumn("fileId", fileId));
    }

    @Override
    public FileProject findByProjectID(Long projectId) {
        return fitModel(DAO.findFirstByColumn("projectID", projectId));
    }

    @Override
    public List<FileProject> findListByFileTypeIDAndProjectID(Long fileTypeID, Long projectID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("fileTypeID", fileTypeID);
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public FileProject findByProjectIDAndFileTypeID(Long projectID, Long fileTypeID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("fileTypeID", fileTypeID);
        return fitModel(DAO.findFirstByColumns(columns));
    }

    @Override
    public Page<FileProject> findPage(FileProject model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (model.getProjectID() != null && model.getFileTypeID() != null) {
            columns.eq("projectID", model.getProjectID());
            columns.eq("fileTypeID", model.getFileTypeID());
        } else if (model.getFileID() != null) {
            columns.eq("fileTypeID", model.getFileTypeID());
        }
        if (model.getRemark() != null) {
            columns.eq("remark", model.getRemark());
        }
        if (model.getIsEnable() != null) {
            columns.eq("isEnable", model.getIsEnable());
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id"));
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
    public FileProject saveFileProjectAndFilesGet(FileProject model) {
        boolean tx = Db.tx(() -> {
            if (!save(model)) {
                return false;
            }
            Files files = filesService.findById(model.getFileID());
            files.setIsEnable(true);
            return filesService.update(files);
        });
        if (tx) {
            return fitModel(model);
        }
        return null;
    }

    @Override
    public boolean updateFileProjectAndProject(FileProject fileProject, Project projectModel) {
        //boolean result = projectService.update(projectModel);
        return Db.tx(() -> {
            if(projectService.update(projectModel)){
                return null == fileProject.getId() ? save(fileProject) : update(fileProject);
            }else{
                return false;
            }
        });
    }

    @Override
    public boolean updateFileProjectAndFiles(FileProject model) {
        Files files = filesService.findById(model.getFileID());
        files.setIsEnable(true);
        return Db.tx(() -> {
            if (!update(model)) {
                return false;
            }
            return filesService.update(files);
        });
    }

    @Override
    public FileProject findByModel(FileProject model) {
        Columns columns = Columns.create();
        columns.eq("fileTypeID", model.getFileTypeID());
        columns.eq("projectID", model.getProjectID());
        if(model.getIsEnable() != null)
            columns.eq("isEnable", model.getIsEnable());

        return fitModel(DAO.findFirstByColumns(columns));
    }

    @Override
    public FileProject findByFileTypeIdAndProjectId(Long fileTypeId, Long projectId) {
        Columns columns = Columns.create();
        columns.eq("fileTypeID", fileTypeId);
        columns.eq("projectID", projectId);
        columns.eq("isEnable", true);
        return fitModel(DAO.findFirstByColumns(columns));
    }

    @Override
    public List<FileProject> findByRemark(Long questionnaireId) {
        Columns columns = Columns.create();
        columns.eq("remark", questionnaireId);
        columns.eq("isEnable",true);
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public boolean isExists(FileProject model) {
        return findByFileTypeIdAndProjectId(model.getFileTypeID(), model.getProjectID()) != null ? true : false;
    }

    @Override
    public boolean update(FileProject model, Files files) {
        return Db.tx(() -> model.update() && files.update());
    }

    @Override
    public FileProject saveAndGet(FileProject model) {
        if (!model.save()) {
            return null;
        }
        return fitModel(model);
    }
}