package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * -----------------------------
 *
 * @author LiuChuanJin
 * @version 2.0
 *          -----------------------------
 * @date 10:05 2018/8/4
 */
@RequestMapping("/app/track")
public class TrackController extends BaseController {

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @JbootrpcService
    private FilesService filesService;


    public void index() {

    }

    /**
     * 风险跟踪管理登记表-表格渲染
     */
    @NotNullPara({"id", "fileTypeID"})
    public void fileTable() {
        Long id = getParaToLong("id");
        Long fileTypeID = getParaToLong("fileTypeID");
        FileProject model = new FileProject();
        model.setFileTypeID(fileTypeID);
        model.setProjectID(id);
        model.setIsEnable(true);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Page<FileProject> page = fileProjectService.findPage(model, pageNumber, pageSize);
        for (int i = 0; i < page.getList().size(); i++) {
            page.getList().get(i).setCreateUserName(userService.findById(page.getList().get(i).getCreateUserID()).getName());
            page.getList().get(i).setName(projectFileTypeService.findById(page.getList().get(i).getFileTypeID()).getName());
        }
        renderJson(new DataTable<FileProject>(page));
    }

    /**
     * 风险跟踪管理登记表-页面
     */
    public void toRiskTrack() {
        Long projectFileTypeID = projectFileTypeService.findByName("风险跟踪管理登记表").getId();
        setAttr("projectFileTypeID", projectFileTypeID).setAttr("projectID", 114).render("riskTrackingManagement.html");
    }

    /**
     * 风险跟踪管理登记表-删除
     */
    @NotNullPara({"id", "fileID"})
    public void deleteRiskTrackingMessage() {
        FileProject fileProject = fileProjectService.findById(getParaToLong("id"));
        Files files = filesService.findById(getParaToLong("fileID"));
        if (files != null && fileProject != null) {
            fileProject.setIsEnable(false);
            files.setIsEnable(false);
            if (!fileProjectService.update(fileProject, files)) {
                renderJson(RestResult.buildError("删除失败"));
                throw new BusinessException("删除失败");
            }
        }
    }

    /**
     * 跟踪资料移交表-页面
     */
    public void toTrackData() {
        Long projectID = 114L;
        Long projectFileTypeID = projectFileTypeService.findByName("跟踪资料移交表").getId();
        Boolean flag = false;
        if (fileProjectService.findByFileTypeIdAndProjectId(projectFileTypeID, projectID) != null) {
            flag = true;
        }
        setAttr("flag", flag).setAttr("projectFileTypeID", projectFileTypeID)
                .setAttr("projectID", projectID).render("trackingDataTransfer.html");
    }
}

