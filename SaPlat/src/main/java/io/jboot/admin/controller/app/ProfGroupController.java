package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ProfGroupService;
import io.jboot.admin.service.entity.model.ProfGroup;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * -----------------------------
 *
 * @author LiuChuanJin
 * @version 2.0
 *          -----------------------------
 * @date 10:05 2018/10/9
 */
@RequestMapping("/app/prof_group")
public class ProfGroupController extends BaseController {

    @JbootrpcService
    private ProfGroupService profGroupService;

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

        ProfGroup model = new ProfGroup();
        model.setName(getPara("name"));

        Page<ProfGroup> page = profGroupService.findPage(model, pageNumber, pageSize);

        renderJson(new DataTable<ProfGroup>(page));
    }


}
