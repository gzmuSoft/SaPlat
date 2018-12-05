package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.AuthProject;
import io.jboot.admin.service.entity.model.Organization;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.admin.service.entity.model.RiskPoint;
import io.jboot.admin.service.entity.status.system.DataStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * -----------------------------
 *
 * @author JiayinWei
 * @version 2.0
 * -----------------------------
 * @date 10:05 2018/12/2
 */
@RequestMapping("/app/risk_point")
public class RiskPointController extends BaseController {

    @JbootrpcService
    private RiskPointService riskPointService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private ProjectAssTypeService projectAssTypeService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private OrganizationService organizationService;

    @JbootrpcService
    private AuthProjectService authProjectService;

    @JbootrpcService
    private RoleService roleService;
    /**
     * index
     */
    public void index() {
        render("main.html");
    }

    /**
     * index
     */
    public void projectRiskPointIndex() {
        Organization organization = null;
        Project pModel = null;
        Long id = getParaToLong("id");

        if (null != id) {
            pModel = projectService.findById(id); //获取项目信息
            if (null == pModel) {
                pModel = new Project();
            }

            pModel.setTypeName(projectAssTypeService.findById(pModel.getPaTypeID()).getName());
            organization = organizationService.findById(userService.findById(pModel.getUserId()).getUserID()); //获取组织信息
            if (null == organization) {
                organization = new Organization();
            }
        } else {
            organization = new Organization();
            pModel = new Project();
        }

        RiskPoint model = new RiskPoint();
        if(id > 0 )
            model.setProjectID(id);

        Page<RiskPoint> page = riskPointService.findPage(model, 1, 30);
        RiskPoint curRiskPoint = null;
        if(page == null){
            page = new Page<RiskPoint>();
            curRiskPoint = new RiskPoint();
        }
        else{
            curRiskPoint = page.getList().get(0);
        }
        setAttr("pModel", pModel).
                setAttr("organization", organization).
                setAttr("curRiskPoint", curRiskPoint).
                render("projectRiskPoint.html");
    }
    /**
     * res表格数据
     */
    public void projectRiskPointData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        Long projectID = getParaToLong("projectID", 0L);

        RiskPoint model = new RiskPoint();
        if(projectID > 0 )
            model.setProjectID(projectID);

        Page<RiskPoint> page = riskPointService.findPage(model, pageNumber, pageSize);
        RiskPoint curRiskPoint = null;
        if(page == null){
            page = new Page<RiskPoint>();
            curRiskPoint = new RiskPoint();
        }
        else{
            curRiskPoint = page.getList().get(0);
        }
        setAttr("curRiskPoint", curRiskPoint).
        renderJson(new DataTable<RiskPoint>(page));
    }

    /**
     * res表格数据
     */
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        RiskPoint model = new RiskPoint();
        model.setName(getPara("name"));

        Page<RiskPoint> page = riskPointService.findPage(model, pageNumber, pageSize);

        if(page == null)
            page = new Page<RiskPoint>();
        renderJson(new DataTable<RiskPoint>(page));
    }

    /**
     * delete
     */
    public void delete() {
        Long id = getParaToLong("id");
        if (!riskPointService.deleteById(id)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update() {
        Long id = getParaToLong("id");
        RiskPoint model = riskPointService.findById(id);
        setAttr("model", model).render("update.html");
    }

    public void add() {
        render("add.html");
    }

    public void postAdd() {
        RiskPoint model = getBean(RiskPoint.class, "model");
        model.setCreateUserID(AuthUtils.getLoginUser().getId());//使创建用户编号为当前用户的编号
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        model.setIsEnable(true);
        if (!riskPointService.save(model)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate() {
        RiskPoint model = getBean(RiskPoint.class, "model");
        RiskPoint byId = riskPointService.findById(model.getId());
        if (byId == null) {
            throw new BusinessException("所指定的国家名称不存在");
        }
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        if (!riskPointService.update(model)) {
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

        RiskPoint model = riskPointService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.USED);
        model.setLastAccessTime(new Date());
        model.setRemark("启用对象");

        if (!riskPointService.update(model)) {
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

        RiskPoint model = riskPointService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.UNUSED);
        model.setLastAccessTime(new Date());
        model.setRemark("禁用对象");

        if (!riskPointService.update(model)) {
            throw new BusinessException("禁用失败");
        }

        renderJson(RestResult.buildSuccess());
    }
}
