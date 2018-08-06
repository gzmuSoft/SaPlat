package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ExpertGroupService;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.api.RoleService;
import io.jboot.admin.service.entity.model.ExpertGroup;
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
 * @date 10:05 2018/7/2
 */
@RequestMapping("/app/expert_group")
public class ExpertGroupController extends BaseController {

    @JbootrpcService
    private ExpertGroupService expertGroupService;

    @JbootrpcService
    private PersonService personService;

    @JbootrpcService
    private RoleService roleService;


    /**
     * index
     */
    public void index() {
        render("main.html");
    }

    /**
     * res表格数据
     */
    @NotNullPara({"name"})
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        ExpertGroup model = new ExpertGroup();
        model.setName(getPara("name"));

        Page<ExpertGroup> page = expertGroupService.findPage(model, pageNumber, pageSize);

        renderJson(new DataTable<ExpertGroup>(page));
    }

    /**
     * delete
     */
    public void delete() {
        Long id = getParaToLong("id");
        if (!expertGroupService.deleteById(id)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update() {
        Long id = getParaToLong("id");
        ExpertGroup model = expertGroupService.findById(id);
        setAttr("model", model).render("update.html");
    }

    public void add() {
        render("add.html");
    }

    public void postAdd() {
        ExpertGroup model = getBean(ExpertGroup.class, "model");
        if (expertGroupService.hasExpertGroup(model.getName())) {
            throw new BusinessException("所指定的专家团体名称已存在");
        }
        model.setCreateUserID(AuthUtils.getLoginUser().getId());//使创建用户编号为当前用户的编号
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        model.setIsEnable(true);
        if (!expertGroupService.save(model)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate() {
        ExpertGroup model = getBean(ExpertGroup.class, "model");
        ExpertGroup byId = expertGroupService.findById(model.getId());
        if (byId == null) {
            throw new BusinessException("所指定的专家团体名称不存在");
        }
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        if (!expertGroupService.update(model)) {
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 启用专家团体
     */
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");

        ExpertGroup model = expertGroupService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.USED);
        model.setLastAccessTime(new Date());
        model.setRemark("启用对象");

        if (!expertGroupService.update(model)) {
            throw new BusinessException("启用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 禁用专家团体
     */
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        ExpertGroup model = expertGroupService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.UNUSED);
        model.setLastAccessTime(new Date());
        model.setRemark("禁用对象");

        if (!expertGroupService.update(model)) {
            throw new BusinessException("禁用失败");
        }

        renderJson(RestResult.buildSuccess());
    }


}
