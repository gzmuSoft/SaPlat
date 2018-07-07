package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ExpertGroupService;
import io.jboot.admin.service.entity.model.ExpertGroup;
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
@RequestMapping("/app/expert_group")
public class ExpertGroupController extends BaseController{

    @JbootrpcService
    private ExpertGroupService expertGroupService;

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

        ExpertGroup expert_group = new ExpertGroup();
        expert_group.setName(getPara("name"));

        Page<ExpertGroup> page = expertGroupService.findPage(expert_group, pageNumber, pageSize);

        renderJson(new DataTable<ExpertGroup>(page));
    }

    /**
     * delete
     */
    public void delete(){
        Long id = getParaToLong("id");
        if (!expertGroupService.deleteById(id)){
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update(){
        Long id = getParaToLong("id");
        ExpertGroup expert_group = expertGroupService.findById(id);
        setAttr("expert_group", expert_group).render("update.html");
    }

    public void add(){
        render("add.html");
    }

    public void postAdd(){
        ExpertGroup expert_group = getBean(ExpertGroup.class, "expert_group");
        if (expertGroupService.hasExpertGroup(expert_group.getName())){
            throw new BusinessException("所指定的专家团体名称已存在");
        }
        if (!expertGroupService.save(expert_group)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate(){
        ExpertGroup expert_group = getBean(ExpertGroup.class, "expert_group");
        ExpertGroup byId = expertGroupService.findById(expert_group.getId());
        if (byId == null){
            throw new BusinessException("所指定的专家团体名称不存在");
        }
        if (!expertGroupService.update(expert_group)){
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
