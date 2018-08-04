package io.jboot.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/app/news")
public class NewsController extends BaseController {
    @JbootrpcService
    private NewsService newsService;
    @JbootrpcService
    private FilesService filesService;
    @JbootrpcService
    private AuthService authService;
    @JbootrpcService
    private RoleService roleService;
    @JbootrpcService
    private PersonService personService;

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        News model = new News();

        String ctime = getPara("ctime", "ctime");
        String title = getPara("title", null);

        Long createUserID = getParaToLong("createUserID");
        Long lastUpdateUserID = getParaToLong("lastUpdateUserID");

        model.setTitle(title);
        model.setCreateUserID(createUserID);
        model.setLastUpdateUserID(lastUpdateUserID);

        int cindex = ctime.indexOf("/");
        if (cindex > 0) {
            model.setCstime(ctime.substring(0, cindex - 1));
            model.setCetime(ctime.substring(cindex + 2));
        }
        Page<News> page = newsService.findPage(model, pageNumber, pageSize);
        renderJson(new DataTable<News>(page));
    }

    public void add() {
        List<Role> roles = roleService.findByStatusUsed();
        List<Person> persons = personService.findAll();
        BaseStatus roleStatus = new BaseStatus() {
        };
        BaseStatus personStatus = new BaseStatus() {
        };
        for (Role post : roles) {
            roleStatus.add(post.getId().toString(), post.getName());
        }
        for (Person person : persons) {
            personStatus.add(person.getId().toString(), person.getName());
        }
        setAttr("roleStatus", roleStatus).
                setAttr("personStatus", personStatus).
                render("add.html");
    }

    @Before(POST.class)
    public void postAdd() {
        News model = getBean(News.class, "model");
        //System.out.println(model.toString());
        User user = AuthUtils.getLoginUser();
        model.setCreateUserID(user.getUserID());
        model.setIsEnable(true);
        if (!newsService.save(model)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void update() {
        Long id = getParaToLong("id");
        News model = newsService.findById(id);
        List<Role> roles = roleService.findByStatusUsed();
        List<Person> persons = personService.findAll();
        BaseStatus roleStatus = new BaseStatus() {
        };
        BaseStatus personStatus = new BaseStatus() {
        };
        for (Role post : roles) {
            roleStatus.add(post.getId().toString(), post.getName());
        }
        for (Person person : persons) {
            personStatus.add(person.getId().toString(), person.getName());
        }
        setAttr("roleStatus", roleStatus).
                setAttr("personStatus", personStatus).
                setAttr("model", model).
                render("update.html");
    }

    public void postUpdate() {
        News model = getBean(News.class, "model");
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        //System.out.println(model.toString());
        if (!newsService.update(model)) {
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * delete
     */
    public void delete() {
        Long id = getParaToLong("id");
        News model = newsService.findById(id);
        model.setIsEnable(false);
        newsService.update(model);
        renderJson(RestResult.buildSuccess());
    }

    @Before(POST.class)
    public void uploadFile() {
        String strUploadPath = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        UploadFile upload = getFile("file", strUploadPath);
        File file = upload.getFile();
        String oldName = file.getName();
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        String type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
        String strNewFileName = UUID.randomUUID() + "." + type;
        String fileUrl = "/upload/" + strUploadPath + "/" + strNewFileName;
        File newFile = new File(path + "/" + strNewFileName);
        file.renameTo(newFile);
        Files files = new Files();
        files.setName(oldName);
        files.setCreateTime(new Date());
        files.setCreateUserID(AuthUtils.getLoginUser().getId());
        files.setIsEnable(false);
        files.setPath(fileUrl);
        files.setSize(file.length());
        files.setType(type);
        filesService.save(files);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        map.put("code", 0);//0表示成功，1失败
        map.put("msg", "success");//提示消息
        map.put("data", map2);
        map2.put("src", fileUrl);//图片url
        String result = new JSONObject(map).toString();
        //System.out.println(result);
        renderJson(map);
    }

    public Map<String, Object> uploadFiles(UploadFile upload, String strUploadPath) {
        // 文件名称生成策略（日期时间+uuid ）
        File file = upload.getFile();
        String oldName = file.getName();
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        String type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
        String strNewFileName = UUID.randomUUID() + "." + type;
        String fileUrl = "/upload/" + strUploadPath + "/" + strNewFileName;
        File newFile = new File(path + "/" + strNewFileName);
        file.renameTo(newFile);
        Files files = new Files();
        files.setName(oldName);
        files.setCreateTime(new Date());
        files.setCreateUserID(AuthUtils.getLoginUser().getId());
        files.setIsEnable(false);
        files.setPath(fileUrl);
        files.setSize(file.length());
        files.setType(type);
        filesService.save(files);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", "SUCCESS");
        map.put("original", oldName);//原来的文件名
        map.put("size", file.getTotalSpace());//文件大小
        map.put("title", oldName);//随意，代表的是鼠标经过图片时显示的文字
        map.put("type", type);//文件后缀名
        map.put("url", fileUrl);//这里的url字段表示的是上传后的图片在图片服务器的完整地址（http://ip:端口/***/***/***.jpg）
        String result = new JSONObject(map).toString();
        return map;
    }

    public void ueditor() {
        String action = getPara("action");
        Ueditor ueditor = new Ueditor();
        try {
            if ("config".equals(action)) {    //如果是初始化
                //System.out.println(UeditorConfig.UEDITOR_CONFIG);
                renderJson(UeditorConfig.UEDITOR_CONFIG);
            } else if ("uploadimage".equals(action) || "uploadvideo".equals(action) || "uploadfile".equals(action)) {
                //如果是上传图片、视频、和其他文件
                String strUploadPath = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
                UploadFile upload = getFile("upfile", strUploadPath);
                renderJson(uploadFiles(upload, strUploadPath));
            }
        } catch (Exception e) {
        }
    }
}
