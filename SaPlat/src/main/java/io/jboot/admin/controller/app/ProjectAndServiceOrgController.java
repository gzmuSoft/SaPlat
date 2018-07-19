package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;


@RequestMapping("/app/projectAndServiceOrg")
public class ProjectAndServiceOrgController extends BaseController {

    @JbootrpcService
    private ProjectService projectService;

    /**
     * 跳转榜单页面
     */
    public void toProjectList() {
        render("projectList.html");
    }

    /**
     * 榜单显示已发布的项目
     */
    public void projectList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Project project = new Project();
        project.setIsPublic(true);
        Page<Project> page = projectService.findPageByIsPublic(project, pageNumber, pageSize);
        renderJson(new DataTable<Project>(page));
    }

    /**
     * 查看项目详细信息
     */
    @NotNullPara({"id"})
    public void see() {
        User user = AuthUtils.getLoginUser();
        Long id = getParaToLong("id");
        Project model = projectService.findById(id);
        setAttr("model", model).render("see.html");
    }

    /**
     * 介入项目
     */
    @NotNullPara({"id"})
    public void agency() {
    }
}
