package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ResultCode;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.DataStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.system.ProAssReviewValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * -----------------------------
 *
 * @author JiayinWei
 * @version 2.0
 * -----------------------------
 * @date 10:05 2018/7/2
 */
@RequestMapping("/app/ass_review")
public class ProAssReviewController extends BaseController {

    @JbootrpcService
    private ProAssReviewService proAssReviewService;

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    @JbootrpcService
    private FileProjectService fileProjectService;

    @JbootrpcService
    private FilesService filesService;

    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private PersonService personService;

    /**
     * 根据角色类型和项目id打开相关界面
     * 只接收3种角色类型：1->服务机构，2->管理机构和3->专家
     * */
    @NotNullPara({"projectId","roleType"})
    public void findPageAndShow(){
        Long projectId = getParaToLong("projectId") ;
        Integer roleType = getParaToInt("roleType");
        switch (roleType){
            //服务机构页面，有文件目录和提交意见的表单，加载所有项目相关文件
            case 1:
                setAttr("projectId",projectId).
                render("main_srv.html");
                break;
            //管理机构页面，有文件目录，加载所有项目相关文件
            case 2:
                setAttr("projectId",projectId).
                render("main_mgr.html");
                break;
            //专家审查中页面，有提交意见的表单，只有预审报告文件
            case 3:
                setAttr("projectId",projectId).setAttr("reportPath",getPrepReport(projectId)).
                render("main_expert.html");
                break;
            //直接显示预审报告和所有评审意见
            case 4:
                String reportFilePath=getPrepReport(projectId);
                Long reportFileId = mReportFileId;
                setAttr("projectId",projectId)
                        .setAttr("reportPath",reportFilePath)
                        .setAttr("fileId",reportFileId).
                        render("main_report_rec.html");
                break;
            //专家审查完成页面，无提交意见的表单，只有预审报告文件
            case 5:
                setAttr("projectId",projectId).setAttr("reportPath",getPrepReport(projectId)).
                        render("main_expert_view.html");
            default:
        }
    }

    //为减少对原有逻辑的修改，用于临时存储文件ID
    private Long mReportFileId = 0L;

    /**
     * index
     */
    public void index() {
        setAttr("projectId",1).
        render("main_mgr.html");
    }


    //渲染文件目录
    @NotNullPara("projectId")
    public void fileTree() {
        Long projectId = getParaToLong("projectId");
        renderJson(RestResult.buildSuccess(proAssReviewService.findFileTreeByProject(projectId)));
    }

    public void findProAssReviewByFileIdAndProjectId() {
        List<ProAssReview> proAssReviews = proAssReviewService.findByFileIdAndProjectId(6L, 28L);
        renderJson(RestResult.buildSuccess(proAssReviews));
    }

    /**
     * 加载预审报告审查意见与反馈列表
     */
    @NotNullPara("projectId")
    public void recData() {
        Long projectId = getParaToLong("projectId");
        Long fileId = getParaToLong("fileId");
        //ProjectFileType projectFileType = projectFileTypeService.findByName("8. 预审报告上传");
        //FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(projectFileType.getId(), projectId);

        List<ProAssReview> proAssReviews = proAssReviewService.findByFileIdAndProjectId(fileId, projectId);

        for(ProAssReview par: proAssReviews){
            User user = userService.findById(par.getCreateUserID());
            if(null != user){
                Person person = personService.findByUser(user);
                if(null != person) {
                    par.setRemark(person.getName());
                }
            }
        }

        Map<String, Object> res = new ConcurrentHashMap<>();
        res.put("list", proAssReviews);
        res.put("code", ResultCode.SUCCESS);
        res.put("projectId", projectId);
        renderJson(res);
    }

    private String getPrepReport(Long projectId){
        ProjectFileType projectFileType = projectFileTypeService.findByName("8. 预审报告上传");
        FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(projectFileType.getId(), projectId);
        mReportFileId = fileProject.getFileID();
        Files file = filesService.findById(mReportFileId);
        return file.getPath().trim();
    }

    /**
     * 加载预审报告审查意见与反馈列表
     */
    @NotNullPara("projectId")
    public void recDataFilterCreatorId() {
        Long projectId = getParaToLong("projectId");
        User user = AuthUtils.getLoginUser();

        //加载文件类型
        ProjectFileType projectFileType = projectFileTypeService.findByName("8. 预审报告上传");

        //加载文件与项目的关联信息
        FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(projectFileType.getId(), projectId);

        //加载审查意见
        List<ProAssReview> proAssReviews = proAssReviewService.findByFileIdAndProjectId(fileProject.getFileID(), projectId);
        List<ProAssReview> pars = new ArrayList<ProAssReview>();
        for(ProAssReview item : proAssReviews){
            if(user.getId().longValue() == item.getCreateUserID().longValue()){
                pars.add(item);
            }
        }

        Map<String, Object> res = new ConcurrentHashMap<>();
        res.put("list", pars);
        res.put("code", ResultCode.SUCCESS);
        res.put("projectId", projectId);
        renderJson(res);
    }


    /**
     * delete
     */
    public void delete() {
        Long id = getParaToLong("id");
        if (!proAssReviewService.deleteById(id)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update() {
        Long id = getParaToLong("id");
        ProAssReview model = proAssReviewService.findById(id);
        setAttr("model", model).render("update.html");
    }

    public void add() {
        render("add.html");
    }

    @Before({POST.class, ProAssReviewValidator.class})
    public void postAdd() {
        ProAssReview model = getBean(ProAssReview.class, "model");
        UUID.randomUUID().toString().replace("-", "").toLowerCase();
        model.setName(UUID.randomUUID().toString().replace("-", "").toLowerCase());
        if (proAssReviewService.isExisted(model.getName())) {
            throw new BusinessException("所指定的项目阶段名称已存在");
        }

        ProjectFileType projectFileType = projectFileTypeService.findByName("8. 预审报告上传");
        FileProject fileProject = fileProjectService.findByFileTypeIdAndProjectId(projectFileType.getId(), model.getProjectID());
        model.setFileID(fileProject.getFileID());

        model.setCreateUserID(AuthUtils.getLoginUser().getId());//使创建用户编号为当前用户的编号
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        model.setIsEnable(true);
        if (!proAssReviewService.save(model)) {
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @Before({POST.class, ProAssReviewValidator.class})
    public void postUpdate() {
        ProAssReview model = getBean(ProAssReview.class, "model");
        ProAssReview byId = proAssReviewService.findById(model.getId());
        if (byId == null) {
            throw new BusinessException("所指定的项目阶段名称不存在");
        }
        model.setLastUpdateUserID(AuthUtils.getLoginUser().getId());//使末次更新用户编号为当前用户的编号
        if (!proAssReviewService.update(model)) {
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 启用项目阶段
     */
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");

        ProAssReview model = proAssReviewService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.USED);
        model.setLastAccessTime(new Date());
        model.setRemark("启用对象");

        if (!proAssReviewService.update(model)) {
            throw new BusinessException("启用失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 禁用项目阶段
     */
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        ProAssReview model = proAssReviewService.findById(id);
        if (model == null) {
            throw new BusinessException("对象不存在");
        }

        model.setStatus(DataStatus.UNUSED);
        model.setLastAccessTime(new Date());
        model.setRemark("禁用对象");

        if (!proAssReviewService.update(model)) {
            throw new BusinessException("禁用失败");
        }

        renderJson(RestResult.buildSuccess());
    }
}
