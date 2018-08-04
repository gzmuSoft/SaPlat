package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ZTree;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.api.ProjectFileTypeService;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.ProjectFileType;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Administrator on 2018/8/4.
 */

@RequestMapping("app/fileupload")
public class FileUploadController extends BaseController {

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @JbootrpcService
    private FilesService filesService;

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private ProjectService projectService;


    @Before(GET.class)
    @NotNullPara({"id", "projectID"})
    public void index() {
        Long projectID=getParaToLong("projectID");
//        Project project=projectService.findById(projectID);
//        if(!project.getUserId().equals(AuthUtils.getLoginUser().getId())){
//            renderJson(RestResult.buildError());
//        }
        setAttr("projectID", getParaToLong("projectID"));
        setAttr("parentID", getParaToLong("id"));
        render("main.html");
    }

    @Before(GET.class)
    @NotNullPara("parentID")
    public void tree() {
        Long parentID = getParaToLong("parentID");
        List<ZTree> ztree = projectFileTypeService.findTreeOnUse(parentID);

        renderJson(RestResult.buildSuccess(ztree));
    }

    @Before(GET.class)
    @NotNullPara({"fileTypeID","projectID","pageNumber", "pageSize"})
    public void tableData() {
        Long fileTypeID=getParaToLong("fileTypeID");
        Long projectID=getParaToLong("projectID");
        FileProject fileProject=new FileProject();
        fileProject.setFileTypeID(fileTypeID);
        fileProject.setProjectID(projectID);
        Integer pageNumber=getParaToInt("pageNumber");
        Integer pageSize=getParaToInt("pageSize");
        Page<FileProject> page = fileProjectService.findPage(fileProject, pageNumber, pageSize);
        renderJson(new DataTable<FileProject>(page));
    }


    public void save(){
        FileProject fileProject=getBean(FileProject.class,"fileProject");
//        Project project=projectService.findById(fileProject.getProjectID());
//        if(!project.getUserId().equals(AuthUtils.getLoginUser().getId())){
//            renderJson(RestResult.buildError());
//        }
        ProjectFileType projectFileType=projectFileTypeService.findById(fileProject.getFileTypeID());
        fileProject.setName(projectFileType.getName());
        if(!fileProjectService.saveFileProjectAndFiles(fileProject)){
            throw new BusinessException("保存失败,请重试");
        }
        renderJson(RestResult.buildSuccess());
    }
    @NotNullPara({"id","projectID"})
    public void delete() {
//        Project project=projectService.findById(getParaToLong("projectID"));
//        if(!project.getUserId().equals(AuthUtils.getLoginUser().getId())){
//            renderJson(RestResult.buildError());
//        }
        FileProject fileProject=fileProjectService.findById(getParaToLong("id"));
        if(!fileProjectService.deleteFileProjectAndFiles(fileProject)){
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
