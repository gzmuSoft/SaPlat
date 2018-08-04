package io.jboot.admin.controller.system;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ProjectFileTypeService;
import io.jboot.admin.service.entity.model.ProjectFileType;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.ProjectFileTypeValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * Created by Administrator on 2018/7/28.
 */
@RequestMapping("/system/projectfiletype")
public class ProjectFileTypeController extends BaseController {

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @Before(GET.class)
    public void index() {
        render("main.html");
    }

    @Before(GET.class)
    @NotNullPara("id")
    public void file() {
        ProjectFileType projectFileType = projectFileTypeService.findById(getParaToLong("id"));
        setAttr("projectFileType", projectFileType);
        render("file.html");
    }

    @Before(GET.class)
    @NotNullPara({"pageNumber", "pageSize", "parentID"})
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        ProjectFileType projectFileType = new ProjectFileType();
        if (!"".equals(getPara("name"))) {
            projectFileType.setName(getPara("name"));
        }
        projectFileType.setParentID(getParaToLong("parentID"));
        Page<ProjectFileType> page = projectFileTypeService.findPage(projectFileType, pageNumber, pageSize);
        renderJson(new DataTable<ProjectFileType>(page));
    }


    @Before(GET.class)
    @NotNullPara("id")
    public void update() {
        ProjectFileType projectFileType = projectFileTypeService.findById(getParaToLong("id"));
        if (projectFileType == null) {
            throw new BusinessException("没有此项目");
        }
        setAttr("projectFileType", projectFileType);
        render("update.html");
    }

    @Before({POST.class, ProjectFileTypeValidator.class})
    public void postUpdate() {
        ProjectFileType projectFileType = getBean(ProjectFileType.class, "projectFileType");
        projectFileType.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        projectFileType.setRemark("修改项目文件");

        if (!projectFileTypeService.update(projectFileType)) {
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @Before(GET.class)
    @NotNullPara("parentID")
    public void add() {
        Long parentID = getParaToLong("parentID");
        ProjectFileType parentProjectProjectFileType;
        if (parentID == -1) {
            parentProjectProjectFileType = new ProjectFileType();
            parentProjectProjectFileType.setId((long) -1);
        } else {
            parentProjectProjectFileType = projectFileTypeService.findById(getParaToLong("parentID"));
        }
        if (parentProjectProjectFileType == null) {
            throw new BusinessException("没有此项目");
        }
        setAttr("parentProjectProjectFileType", parentProjectProjectFileType);
        render("add.html");
    }


    @Before({POST.class, ProjectFileTypeValidator.class})
    public void postAdd() {
        ProjectFileType projectFileType = getBean(ProjectFileType.class, "projectFileType");

        projectFileType.setLastUpdateUserID(AuthUtils.getLoginUser().getId());
        projectFileType.setRemark("保存项目资料");

        if (!projectFileTypeService.save(projectFileType)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }


    @Before(POST.class)
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");
        ProjectFileType projectFileType = projectFileTypeService.findById(id);
        if (projectFileType == null) {
            throw new BusinessException("编号为" + id + "的项目文档不存在");
        }
        projectFileType.setRemark("启用项目文档");
        projectFileType.setIsEnable(true);
        if (!projectFileTypeService.update(projectFileType)) {
            throw new BusinessException("启用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    @Before(POST.class)
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        ProjectFileType projectFileType = projectFileTypeService.findById(id);
        if (projectFileType == null) {
            throw new BusinessException("编号为" + id + "的项目文档不存在");
        }
        projectFileType.setIsEnable(false);
        projectFileType.setRemark("停用项目文档");

        if (!projectFileTypeService.update(projectFileType)) {
            throw new BusinessException("停用失败");
        }

        renderJson(RestResult.buildSuccess());
    }
}
