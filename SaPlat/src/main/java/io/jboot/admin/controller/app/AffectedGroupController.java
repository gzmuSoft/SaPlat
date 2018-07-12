package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.AffectedGroupService;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.api.RoleService;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;
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
@RequestMapping("/app/affected_group")
public class AffectedGroupController extends BaseController{

    @JbootrpcService
    private AffectedGroupService affectedGroupService;

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
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        AffectedGroup model = new AffectedGroup();
        model.setName(getPara("name"));

        Page<AffectedGroup> page = affectedGroupService.findPage(model, pageNumber, pageSize);

        renderJson(new DataTable<AffectedGroup>(page));
    }

    /**
     * delete
     */
    public void delete(){
        Long id = getParaToLong("id");
        if (!affectedGroupService.deleteById(id)){
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update(){
        Long id = getParaToLong("id");
        AffectedGroup model = affectedGroupService.findById(id);
        setAttr("model", model).render("update.html");
    }

    public void add(){
        render("add.html");
    }

    public void postAdd(){
        AffectedGroup model = getBean(AffectedGroup.class, "model");
        //if (affectedGroupService.hasAffectedGroup(model.getName())){
        //    throw new BusinessException("所指定的影响群体名称已存在");
        //}
        model.setIsEnable(1);
        if (!affectedGroupService.save(model)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate(){
        AffectedGroup model = getBean(AffectedGroup.class, "model");
        AffectedGroup byId = affectedGroupService.findById(model.getId());
        if (byId == null){
            throw new BusinessException("所指定的影响群体名称不存在");
        }
        if (!affectedGroupService.update(model)){
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 启用影响群体
     */
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");

        AffectedGroup model = affectedGroupService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.USED);
        model.setLastAccessTime(new Date());
        model.setRemark("启用对象");

        if (!affectedGroupService.update(model)) {
            throw new BusinessException("启用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 禁用影响群体
     */
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        AffectedGroup model = affectedGroupService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.UNUSED);
        model.setLastAccessTime(new Date());
        model.setRemark("禁用对象");

        if (!affectedGroupService.update(model)) {
            throw new BusinessException("禁用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 影响群体认证,信息填写页面
     */
    public void verify(){
        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        AffectedGroup affectedGroup = affectedGroupService.findByPersonId(person.getId());
        if (affectedGroup == null){
            affectedGroup = new AffectedGroup();
        } else {
            if (affectedGroup.getIsEnable() == 0){
                affectedGroup = new AffectedGroup();
            }
        }
        setAttr("affectedGroup",affectedGroup)
                .setAttr("person",person)
                .setAttr("user",user)
                .render("verify.html");

    }
}
