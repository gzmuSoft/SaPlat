package io.jboot.admin.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ZTree;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.api.ProAssReviewService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.service.entity.model.ProAssReview;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/app/filingfrom")
public class FilingFromController extends BaseController {
    @JbootrpcService
    private ProAssReviewService proAssReviewService;
    @JbootrpcService
    private FilesService filesService;
    @JbootrpcService
    private FileProjectService fileProjectService;

    private int defaultProjectID = 183;
    /**
     * index
     */
    public void index() {
        render("main.html");
    }

    //渲染文件目录
    public void fileTree(){
        int projectID = getParaToInt("projectID", defaultProjectID);
        List<ZTree> zTree = proAssReviewService.findFileTreeByProject(projectID);
        renderJson(RestResult.buildSuccess(zTree));
    }

    public void findProAssReviewByFileIdAndProjectId(){
        List<ProAssReview> proAssReviews = proAssReviewService.findByFileIdAndProjectId(6L,defaultProjectID);
        renderJson(RestResult.buildSuccess(proAssReviews));
    }

    @Before(POST.class)
    public void uploadFile()
    {
        long projectID = getParaToInt("projectID", defaultProjectID);
        String strUploadPath = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        UploadFile upload = getFile("filepath", strUploadPath);
        File file = upload.getFile();
        String oldName = file.getName();
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        String type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
        String strNewFileName = UUID.randomUUID() + "." + type;
        String fileUrl = "/upload/" + strUploadPath + "/" + strNewFileName;
        String newName = path + "/" + strNewFileName;

        File newFile = new File(newName);
        file.renameTo(newFile);
        Files files = new Files();
        files.setName(oldName);
        files.setCreateTime(new Date());
        files.setCreateUserID(AuthUtils.getLoginUser().getId());
        files.setIsEnable(false);
        files.setPath(fileUrl);
        files.setSize(file.length());
        files.setType(type);
        files=filesService.saveAndGet(files);

        FileProject fileProject = new FileProject();
        long fileTypeID = 40;
        fileProject.setName("初评报告");
        fileProject.setProjectID(projectID);
        fileProject.setFileID(files.getId());
        fileProject.setCreateUserID(AuthUtils.getLoginUser().getId());
        fileProject.setFileTypeID(fileTypeID);
        fileProject.setIsEnable(true);
        fileProjectService.save(fileProject);

        Map<String,Object> map = new HashMap<String,Object>();
        Map<String,Object> map2 = new HashMap<String,Object>();
        map.put("code",0);//0表示成功，1失败
        map.put("msg","success");//提示消息
        map.put("data",map2);
        map2.put("src",fileUrl);//图片url
        String result = new JSONObject(map).toString();
        //System.out.println(result);
        renderJson(map);
    }
}
