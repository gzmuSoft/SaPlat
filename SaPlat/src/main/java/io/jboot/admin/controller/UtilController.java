package io.jboot.admin.controller;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.FileProjectService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.entity.model.FileProject;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2018/7/27.
 */
@RequestMapping("/util")
public class UtilController extends BaseController {
    @JbootrpcService
    private FilesService filesService;

    @JbootrpcService
    private FileProjectService fileProjectService;
    @Before(POST.class)
    public void uploadFile() {
        //当前时间
        String nowDate = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        UploadFile upload = getFile("file", nowDate);
        //获取描述
        String description = getPara("description");
        File file = upload.getFile();
        String oldName = file.getName();
        String newName = UUID.randomUUID().toString();
        // winodws 下文件上传
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        // linux 下文件上传
//        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/"));

        String type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
        // winodws 下文件上传
        File newFile = new File(path + "\\" + newName + "." + type);
        // linux 下文件上传
//        File newFile = new File(path + "/" + newName + "." + type);

        if (!file.renameTo(newFile)) {
            throw new BusinessException("文件上传失败");
        }
        String fileUrl = "/upload" + "/" + nowDate + "/" + newName + "." + type;
        Files files = new Files();
        files.setName(oldName);
        files.setCreateTime(new Date());
        files.setDescription(description);
        files.setCreateUserID(AuthUtils.getLoginUser().getId());
        files.setIsEnable(false);
        files.setPath(fileUrl);
        files.setSize(file.length());
        files.setType(type);
        files = filesService.saveAndGet(files);
        if (files == null) {
            throw new BusinessException("文件上传失败");
        }
        Map<String, Object> map = new HashMap<String, Object>();

        //传递消息实体 data
        map.put("src", files.getPath());
        map.put("fileId", files.getId());
        map.put("title", files.getName());
        renderJson(RestResult.buildSuccess(map));
    }

    @NotNullPara("fileID")
    public void pdfView() {
        Long fileID = getParaToLong("fileID");
        Files files = filesService.findById(fileID);
        if (files == null) {
            throw new BusinessException("文件不存在");
        }
        setAttr("files", files);
        render("pdf.html");
    }

    /**
     * 提示接口
     *
     * @apiNote 跳转提示页面的通用方法，接受以下参数
     * content 提示的显示内容
     * btn1 提示是否有跳转连接文字
     * url1 如果有，跳转链接
     * data1 第一个 url 需要的数据
     * method1 页面显示方式，可选值
     * this  当前页面打开(默认)
     * new   新页面内打开
     * type1 提交方式
     * href 跳转（默认）
     * ajax ajax 提交
     * btn2 提示是否有跳转连接文字
     * url2 如果有，跳转链接
     * data2 第二个 url 需要的数据
     * method2 页面显示方式，可选值
     * this  (默认)
     * new   当前页面内打开
     * title 跳转到的页面的标题
     * icon 需要显示的图标
     * toHtml 跳转到的页面
     * 默认值：verify.html
     */
    public void tip() {
        setAttr("content", getPara("content"))
                .setAttr("btn1", getPara("btn1"))
                .setAttr("btn2", getPara("btn2"))
                .setAttr("type1", getPara("type1", "href"))
                .setAttr("url1", getPara("url1"))
                .setAttr("data1", getPara("data1", ""))
                .setAttr("method1", getPara("method1", "this"))
                .setAttr("url2", getPara("url2"))
                .setAttr("type2", getPara("type2", "href"))
                .setAttr("data2", getPara("data2", ""))
                .setAttr("method2", getPara("method2", "this"))
                .setAttr("icon", getPara("icon", "layui-icon-auz"))
                .setAttr("title", getPara("title", "新页面"))
                .render(getPara("toHtml", "verify.html"));
    }
    //判断project 是否有对应的fileType文件
    @NotNullPara({"fileTypeID","projectID"})
    public void isLive(){
        Map<String, Object> map = new HashMap<String, Object>();

        Long fileTypeID=getParaToLong("fileTypeID");
        Long projectID=getParaToLong("projectID");
        if(!(fileTypeID==null||projectID==null)){
            FileProject fileProject=fileProjectService.findByFileTypeIdAndProjectId(fileTypeID,projectID);
            if(fileProject!=null) {
                map.put("code","1");
            }
            else{
                map.put("code","0");
            }
        }
        else{
            map.put("code","0");
        }
        renderJson(RestResult.buildSuccess(map));
    }

    @NotNullPara({"fileTypeID","projectID"})
    public void projectFileView() {
        Long fileTypeID=getParaToLong("fileTypeID");
        Long projectID=getParaToLong("projectID");
        if(!(fileTypeID==null||projectID==null)) {
            FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(fileTypeID, projectID);
            Files files = filesService.findById(fileProject.getFileID());
            if (files == null) {
                files=new Files();
                files.setPath("none");
            }
            setAttr("files", files);
            render("pdf.html");
        }
    }
}
