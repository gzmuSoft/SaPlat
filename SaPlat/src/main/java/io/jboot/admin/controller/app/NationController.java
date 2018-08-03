package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.NationService;
import io.jboot.admin.service.entity.model.Nation;
import io.jboot.admin.service.entity.status.system.DataStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.NationValidator;
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
@RequestMapping("/app/nation")
public class NationController extends BaseController {

    @JbootrpcService
    private NationService nationService;

    /**
     * index
     */
    public void index() {
        String pdfURL = "/upload/test.pdf";
        setAttr("pdfURL", pdfURL).render("main.html");
    }

    /**
     * res表格数据
     */
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        Nation model = new Nation();
        model.setName(getPara("name"));

        Page<Nation> page = nationService.findPage(model, pageNumber, pageSize);

        renderJson(new DataTable<Nation>(page));
    }

    /**
     * delete
     */
    public void delete() {
        Long id = getParaToLong("id");
        if (!nationService.deleteById(id)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update() {
        Long id = getParaToLong("id");
        Nation model = nationService.findById(id);
        setAttr("model", model).render("update.html");
    }

    public void add() {
        render("add.html");
    }

    @Before({POST.class, NationValidator.class})
    public void postAdd() {
        Nation model = getBean(Nation.class, "model");
        if (nationService.isExisted(model.getName())) {
            throw new BusinessException("所指定的民族名称已存在");
        }
        model.setCreateUserID(AuthUtils.getLoginUser().getId());//使创建用户编号为当前用户的编号
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        model.setIsEnable(true);
        if (!nationService.save(model)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @Before({POST.class, NationValidator.class})
    public void postUpdate() {
        Nation model = getBean(Nation.class, "model");
        Nation byId = nationService.findById(model.getId());
        if (byId == null) {
            throw new BusinessException("所指定的民族名称不存在");
        }
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        if (!nationService.update(model)) {
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 启用民族
     */
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");

        Nation model = nationService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.USED);
        model.setLastAccessTime(new Date());
        model.setRemark("启用对象");

        if (!nationService.update(model)) {
            throw new BusinessException("启用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 禁用民族
     */
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        Nation model = nationService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.UNUSED);
        model.setLastAccessTime(new Date());
        model.setRemark("禁用对象");

        if (!nationService.update(model)) {
            throw new BusinessException("禁用失败");
        }

        renderJson(RestResult.buildSuccess());
    }
}
