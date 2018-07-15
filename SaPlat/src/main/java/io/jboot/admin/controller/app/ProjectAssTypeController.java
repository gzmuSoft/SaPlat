package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ProjectAssTypeService;
import io.jboot.admin.service.entity.status.system.DataStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.admin.service.entity.model.ProjectAssType;

import java.util.Date;

/**
 * -----------------------------
 *
 * @author JiayinWei
 * @version 2.0
 * -----------------------------
 * @date 10:05 2018/7/2
 */
@RequestMapping("/app/project_ass_type")
public class ProjectAssTypeController extends BaseController{

    @JbootrpcService
    private ProjectAssTypeService projectAssTypeService;

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

        ProjectAssType model = new ProjectAssType();
        model.setName(getPara("name"));
        model.setCode(getPara("code"));

        Page<ProjectAssType> page = projectAssTypeService.findPage(model, pageNumber, pageSize);

        renderJson(new DataTable<ProjectAssType>(page));
    }

    /**
     * delete
     */
    public void delete(){
        Long id = getParaToLong("id");
        if (!projectAssTypeService.deleteById(id)){
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update(){
        Long id = getParaToLong("id");
        ProjectAssType model = projectAssTypeService.findById(id);
        setAttr("model", model).render("update.html");
    }

    public void add(){
        render("add.html");
    }

    public void postAdd(){
        ProjectAssType model = getBean(ProjectAssType.class, "model");
        if (projectAssTypeService.isExisted(model.getName())){
            throw new BusinessException("所指定的项目评估类型名称已存在");
        }
        model.setCreateUserID(AuthUtils.getLoginUser().getId());//使创建用户编号为当前用户的编号
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        model.setIsEnable(true);
        if (!projectAssTypeService.save(model)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate(){
        ProjectAssType model = getBean(ProjectAssType.class, "model");
        ProjectAssType byId = projectAssTypeService.findById(model.getId());
        if (byId == null){
            throw new BusinessException("所指定的项目评估类型不存在");
        }
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        if (!projectAssTypeService.update(model)){
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 启用项目阶段
     */
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");

        ProjectAssType model = projectAssTypeService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.USED);
        model.setLastAccessTime(new Date());
        model.setRemark("启用对象");

        if (!projectAssTypeService.update(model)) {
            throw new BusinessException("启用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 禁用项目阶段
     */
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        ProjectAssType model = projectAssTypeService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.UNUSED);
        model.setLastAccessTime(new Date());
        model.setRemark("禁用对象");

        if (!projectAssTypeService.update(model)) {
            throw new BusinessException("禁用失败");
        }

        renderJson(RestResult.buildSuccess());
    }
}
