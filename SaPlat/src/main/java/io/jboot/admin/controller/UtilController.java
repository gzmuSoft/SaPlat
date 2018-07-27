package io.jboot.admin.controller;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

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
public class UtilController extends BaseController{
    @JbootrpcService
    private FilesService filesService;

    @Before(POST.class)
    public void uploadFile(){
        //当前时间
        String nowDate =new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        UploadFile upload = getFile("file", nowDate);
        //获取描述
        String description = getPara("description");
        File file = upload.getFile();
        String oldName = file.getName();
        String newName = UUID.randomUUID().toString();
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        String type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
        File newFile = new File(path + "\\" +  newName+ "." + type);
        if(!file.renameTo(newFile)){
            throw new BusinessException("文件上传失败");
        }
        String fileUrl = "/upload" +"/"+nowDate+"/"+newName + "." + type;
        Files files = new Files();
        files.setName(oldName);
        files.setCreateTime(new Date());
        files.setDescription(description);
        files.setCreateUserID(AuthUtils.getLoginUser().getId());
        files.setIsEnable(false);
        files.setPath(fileUrl);
        files.setSize(file.length());
        files.setType(type);
        files=filesService.saveAndGet(files);
        if(files==null) {
            throw new BusinessException("文件上传失败");
        }
        Map<String, Object> map = new HashMap<String, Object>();

        //传递消息实体 data
        map.put("src", files.getPath());
        map.put("fileId",files.getId());
        map.put("title",files.getName());
        renderJson(RestResult.buildSuccess(map));
    }
}
