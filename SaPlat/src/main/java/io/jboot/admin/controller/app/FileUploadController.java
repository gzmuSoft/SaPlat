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
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.ProjectStatus;
import io.jboot.admin.support.auth.AuthUtils;
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

    @JbootrpcService
    private ProjectUndertakeService projectUndertakeService;

    @JbootrpcService
    private FacAgencyService facAgencyService;


    @Before(GET.class)
    @NotNullPara({"projectID"})
    public void index() {
        Long projectID = getParaToLong("projectID");
        Project project = projectService.findById(projectID);
        String flag = getPara("flag", "false");
        if (!hasPermission(project)) {
            throw new BusinessException("没有此项目权限");
        }
        setAttr("projectID", getParaToLong("projectID"));
        ProjectFileType projectFileType = projectFileTypeService.findByNameAndParentID("评估文件", -1L);
        setAttr("parentID", projectFileType.getId())
                .setAttr("flag", flag)
                .render("main.html");
    }

    @Before(GET.class)
    @NotNullPara("parentID")
    public void tree() {
        Long parentID = getParaToLong("parentID");
        List<ZTree> ztree = projectFileTypeService.findTreeOnUse(parentID);

        renderJson(RestResult.buildSuccess(ztree));
    }

    @Before(GET.class)
    @NotNullPara({"fileTypeID", "projectID", "pageNumber", "pageSize"})
    public void tableData() {
        Long fileTypeID = getParaToLong("fileTypeID");
        Long projectID = getParaToLong("projectID");
        FileProject fileProject = new FileProject();
        fileProject.setFileTypeID(fileTypeID);
        fileProject.setProjectID(projectID);
        Integer pageNumber = getParaToInt("pageNumber");
        Integer pageSize = getParaToInt("pageSize");
        Page<FileProject> page = fileProjectService.findPage(fileProject, pageNumber, pageSize);
        renderJson(new DataTable<FileProject>(page));
    }


    public void save() {
        FileProject fileProject = getBean(FileProject.class, "fileProject");
        Project project = projectService.findById(fileProject.getProjectID());
        if (!hasPermission(project)) {
            throw new BusinessException("没有此项目权限");
        }
        ProjectFileType projectFileType = projectFileTypeService.findById(fileProject.getFileTypeID());
        fileProject.setName(projectFileType.getName());
        fileProject.setCreateUserID(AuthUtils.getLoginUser().getId());
        if (!fileProjectService.saveFileProjectAndFiles(fileProject)) {
            throw new BusinessException("保存失败,请重试");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id", "projectID"})
    public void delete() {
        Project project = projectService.findById(getParaToLong("projectID"));
        if (!hasPermission(project)) {
            throw new BusinessException("没有此项目权限");
        }
        FileProject fileProject = fileProjectService.findById(getParaToLong("id"));
        if (!fileProjectService.deleteFileProjectAndFiles(fileProject)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara("id")
    public void document() {
        Project project = projectService.findById(getParaToLong("id"));
        Long percent = getParaToLong("percent");
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (!hasPermission(project)) {
            throw new BusinessException("没有此项目权限");
        }
        ProjectFileType projectFileType = projectFileTypeService.findByName("3.14 预审报告上传");
        FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(projectFileType.getId(), project.getId());
        if (fileProject == null) {
            fileProject = new FileProject();
            fileProject.setName(projectFileType.getName());
            fileProject.setProjectID(project.getId());
            fileProject.setFileTypeID(projectFileType.getId());
            fileProject.setIsEnable(false);
            fileProject.setCreateUserID(AuthUtils.getLoginUser().getId());
            fileProject = fileProjectService.saveAndGet(fileProject);
        }
        setAttr("percent", percent);
        setAttr("fileProject", fileProject);
        setAttr("flag", getPara("flag", "false"));
        render("documentMain.html");
    }

    public void documentSave() {
        FileProject fileProject = getBean(FileProject.class, "fileProject");
        Project project = projectService.findById(fileProject.getProjectID());
        if (!hasPermission(project)) {
            throw new BusinessException("没有此项目权限");
        }
        fileProject.setIsEnable(false);
        FileProject model = fileProjectService.findByFileTypeIdAndProjectId(fileProject.getFileTypeID(), fileProject.getProjectID());
        model.setFileID(fileProject.getFileID());
        if (!fileProjectService.updateFileProjectAndFiles(model)) {
            throw new BusinessException("保存失败,请重试");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"fileProjectID"})
    public void documentSub() {
        FileProject fileProject = fileProjectService.findById(getParaToLong("fileProjectID"));
        Project project = projectService.findById(fileProject.getProjectID());
        if (!hasPermission(project)) {
            throw new BusinessException("没有此项目权限");
        }
        fileProject.setIsEnable(true);
        project.setStatus(ProjectStatus.REVIEW);
        if (!fileProjectService.updateFileProjectAndProject(fileProject, project)) {
            throw new BusinessException("提交失败");
        }
        renderJson(RestResult.buildSuccess());

    }

    private boolean hasPermission(Project project) {
        User user = AuthUtils.getLoginUser();
        if ("自评".equals(project.getAssessmentMode())) {
            return project.getUserId().equals(user.getId());
        } else if ("委评".equals(project.getAssessmentMode())) {
            //判断是否为组织机构
            if (user.getUserSource() == 1) {
                FacAgency facAgency = facAgencyService.findByOrgId(user.getUserID());
                if (facAgency == null) {
                    return project.getUserId().equals(user.getId());
                } else {
                    ProjectUndertake projectUndertake = projectUndertakeService.findByProjectId(project.getId());
                    return facAgency.getId().equals(projectUndertake.getFacAgencyID());
                }
            }
        }
        return false;
    }
}
