package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.api.NewsService;
import io.jboot.admin.service.entity.model.News;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/app/news")
public class NewsController extends BaseController {
    @JbootrpcService
    private NewsService newsService;
    @JbootrpcService
    private FilesService filesService;

    /**
     * index
     */
    public void index() {
        render("main.html");
    }
    public void add(){
        render("add.html");
    }

    /**
     * res表格数据
     */
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        News model = new News();
        model.setName(getPara("name"));

        Page<News> page = newsService.findPage(model, pageNumber, pageSize);
        renderJson(new DataTable<News>(page));
    }
}
