package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.ReviewGroupService;
import io.jboot.admin.service.entity.model.ReviewGroup;
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
@RequestMapping("/app/review_group")
public class ReviewGroupController extends BaseController {

    @JbootrpcService
    private ReviewGroupService reviewGroupService;


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

        ReviewGroup model = new ReviewGroup();
        model.setName(getPara("name"));

        Page<ReviewGroup> page = reviewGroupService.findPage(model, pageNumber, pageSize);

        renderJson(new DataTable<ReviewGroup>(page));
    }

}
