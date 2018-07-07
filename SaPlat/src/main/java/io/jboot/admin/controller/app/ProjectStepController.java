package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ProjectStepService;
import io.jboot.admin.service.entity.model.ExpertGroup;
import io.jboot.admin.service.entity.model.ProjectStep;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * -----------------------------
 *
 * @author JiayinWei
 * @version 2.0
 * -----------------------------
 * @date 10:05 2018/7/2
 */
@RequestMapping("/app/project_step")
public class ProjectStepController extends BaseController{

    @JbootrpcService
    private ProjectStepService projectStepService;

    /**
     * index
     */
    public void index() {
        render("main.html");
    }
    /**
     * res表格数据
     */
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        ProjectStep project_step = new ProjectStep();
        project_step.setName(getPara("name"));

        Page<ProjectStep> page = projectStepService.findPage(project_step, pageNumber, pageSize);

        renderJson(new DataTable<ProjectStep>(page));
    }

    /**
     * delete
     */
    public void delete(){
        Long id = getParaToLong("id");
        if (!projectStepService.deleteById(id)){
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update(){
        Long id = getParaToLong("id");
        ProjectStep project_step = projectStepService.findById(id);
        setAttr("expert_group", project_step).render("update.html");
    }

    public void add(){
        render("add.html");
    }

    public void postAdd(){
        ProjectStep project_step = getBean(ProjectStep.class, "project_step");
        if (projectStepService.hasProjectStep(project_step.getName())){
            throw new BusinessException("所指定的专家团体名称已存在");
        }
        if (!projectStepService.save(project_step)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate(){
        ProjectStep project_step = getBean(ProjectStep.class, "project_step");
        ProjectStep byId = projectStepService.findById(project_step.getId());
        if (byId == null){
            throw new BusinessException("所指定的专家团体名称不存在");
        }
        if (!projectStepService.update(project_step)){
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
