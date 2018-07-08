package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.RiskLevelService;
import io.jboot.admin.service.entity.model.RiskLevel;
import io.jboot.admin.service.entity.status.system.DataStatus;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * -----------------------------
 *
 * @author JiayinWei
 * @version 2.0
 * -----------------------------
 * @date 10:05 2018/7/2
 */
@RequestMapping("/app/risk_level")
public class RiskLevelController extends BaseController{

    @JbootrpcService
    private RiskLevelService projectStepService;

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

        RiskLevel model = new RiskLevel();
        model.setName(getPara("name"));

        Page<RiskLevel> page = projectStepService.findPage(model, pageNumber, pageSize);

        renderJson(new DataTable<RiskLevel>(page));
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
        RiskLevel model = projectStepService.findById(id);
        setAttr("model", model).render("update.html");
    }

    public void add(){
        render("add.html");
    }

    public void postAdd(){
        RiskLevel model = getBean(RiskLevel.class, "model");
        if (projectStepService.isExisted(model.getName())){
            throw new BusinessException("所指定的风险等级名称已存在");
        }
        model.setIsEnable(true);
        if (!projectStepService.save(model)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate(){
        RiskLevel model = getBean(RiskLevel.class, "model");
        RiskLevel byId = projectStepService.findById(model.getId());
        if (byId == null){
            throw new BusinessException("所指定的风险等级名称不存在");
        }
        if (!projectStepService.update(model)){
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }
    /**
     * 启用风险等级
     */
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");

        RiskLevel model = projectStepService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.USED);
        model.setLastAccessTime(new Date());
        model.setRemark("启用对象");

        if (!projectStepService.update(model)) {
            throw new BusinessException("启用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 禁用风险等级
     */
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        RiskLevel model = projectStepService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.UNUSED);
        model.setLastAccessTime(new Date());
        model.setRemark("禁用对象");

        if (!projectStepService.update(model)) {
            throw new BusinessException("禁用失败");
        }

        renderJson(RestResult.buildSuccess());
    }
}
