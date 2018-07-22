package io.jboot.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.api.NewsService;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.service.entity.model.News;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Before(POST.class)
    public String uploadFile()
    {
        UploadFile upload = getFile("file", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        String description = getPara("description");
        File file = upload.getFile();
        String oldName = file.getName();
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        String type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
        String fileUrl = "/upload/" + UUID.randomUUID() + "." + type;
        File newFile = new File(path + "\\" + UUID.randomUUID() + "." + type);
        file.renameTo(newFile);
        Files files = new Files();
        files.setName(oldName);
        files.setCreateTime(new Date());
        files.setDescription(description);
        files.setCreateUserID(AuthUtils.getLoginUser().getId());
        files.setIsEnable(false);
        files.setPath(newFile.getName());
        files.setSize(file.length());
        files.setType(type);
        //filesService.save(files);
        Map<String,Object> map = new HashMap<String,Object>();
        Map<String,Object> map2 = new HashMap<String,Object>();
        map.put("code",0);//0表示成功，1失败
        map.put("msg","success");//提示消息
        map.put("data",map2);
        map2.put("src",fileUrl);//图片url
        String result = new JSONObject(map).toString();
        System.out.println(result);
        return result;
    }
}
