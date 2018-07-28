package io.jboot.admin.controller.app;


import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.support.auth.AuthUtils;

import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

/**
 * Created by fengx on 2018/7/25.
 */
@RequestMapping("/app/questionnaire")
public class QuestionnaireController extends BaseController {
    @JbootrpcService
    private UserRoleService userRoleService;

    @JbootrpcService
    private NationService nationService;

    @JbootrpcService
    private EducationalService educationalService;

    @JbootrpcService
    private OccupationService occupationService;

    @JbootrpcService
    private ProjectService projectService;

    @JbootrpcService
    private QuestionnaireService questionnaireService;

    @JbootrpcService
    private QuestionnaireContentService questionnaireContentService;

    @JbootrpcService
    private QuestionnaireContentLinkService questionnaireContentLinkService;
    /**
     * index
     */
    public void index() {
        User loginUser = AuthUtils.getLoginUser();
        //当前用户权限
        List<UserRole> roles = userRoleService.findListByUserId(loginUser.getId());

        //加载项目
        Project pmodel = new Project();
        pmodel.setIsEnable(true);
        //pmodel.setStatus();状态
        //当前用户
        pmodel.setUserId(loginUser.getId());
        Project project = projectService.findAll(pmodel).get(0);
        //加载问卷
        Questionnaire questionnaire = questionnaireService.findByProjectID(project.getId());
        if (questionnaire == null){
            questionnaire = new Questionnaire();
        }
        for (UserRole userRole : roles) {
            if (userRole.getRoleID()== 2) {

                //加载民族
                Nation nmodel = new Nation();
                nmodel.setIsEnable(true);
                List<Nation> nations = nationService.findAll(nmodel);
                Nation nationName = nationService.findById(questionnaire.getNationID());
                if (nationName == null){
                    nationName = new Nation();
                }
                BaseStatus nationStatus = new BaseStatus() {
                };
                for (Nation nation : nations) {
                    nationStatus.add(nation.getId().toString(), nation.getName());
                }
                //加载学历
                Educational emodel = new Educational();
                emodel.setIsEnable(true);
                List<Educational> educationals = educationalService.findAll(emodel);
                Educational educationalName = educationalService.findById(questionnaire.getDegreeOfEducationID());
                if (educationalName == null){
                    educationalName = new Educational();
                }
                BaseStatus educationalStatus = new BaseStatus() {
                };
                for (Educational educational : educationals) {
                    educationalStatus.add(educational.getId().toString(), educational.getName());
                }
                //加载职业
                Occupation omodel = new Occupation();
                omodel.setIsEnable(true);
                List<Occupation> occupations = occupationService.findAll(omodel);
                Occupation occupationName = occupationService.findById(questionnaire.getOccupationID());
                if (occupationName == null){
                    occupationName = new Occupation();
                }
                BaseStatus occupationStatus = new BaseStatus() {
                };
                for (Occupation item : occupations) {
                    occupationStatus.add(item.getId().toString(), item.getName());
                }
                setAttr("nationName",nationName.getName()).
                setAttr("educationalName",educationalName.getName()).
                setAttr("occupationName",occupationName.getName()).
                setAttr("questionnaire",questionnaire).
                setAttr("type",0).
                setAttr("project", project).
                setAttr("occupationStatus", occupationStatus).
                setAttr("educationalStatus", educationalStatus).
                setAttr("nationStatus", nationStatus).
                render("personMain.html");
                break;
            } else {
                setAttr("questionnaire",questionnaire).
                setAttr("type",1).
                setAttr("project", project).
                render("organizationMain.html");
                break;
            }
        }
    }


    /**
     * 修改项目资料
     */
    public void updateProject(){
        Project project = getBean(Project.class ,"project");
        project.setLastAccessTime(new Date());
        if (!projectService.update(project)){
            throw new BusinessException("保存失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 判断是该修改还是添加调查问卷并执行
     */
    public void postSub(){
        Long pId = getParaToLong("pid");
        //查询是否有项目的问卷  ret问卷id
        Questionnaire questionnaire = questionnaireService.findByProjectID(pId);
        //id 存在则执行postUpdate
        //id 不存在执行postAdd
        if (questionnaire!= null){
            postUpdate(questionnaire.getId());
        }
        else{
            postAdd(pId);
        }
    }

    /**
     * 添加调查问卷
     */
    private void postAdd(Long id){
        User loginUser = AuthUtils.getLoginUser();
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setLastAccessTime(new Date());
        questionnaire.setCreateTime(new Date());
        questionnaire.setProjectID(id);
        questionnaire.setAge(getParaToInt("age"));
        questionnaire.setDepartment(getPara("department"));
        questionnaire.setSurveyTime(getParaToDate("surveyTime"));
        questionnaire.setType(getParaToInt("type"));
        questionnaire.setUnit(getPara("unit"));
        questionnaire.setUnitAddress(getPara("unitAddress"));
        questionnaire.setUnitTel(getPara("unitTel"));
        questionnaire.setPersonName(getPara("name"));
        questionnaire.setPersonGender(getParaToInt("sex"));
        questionnaire.setAddr(getPara("addr"));
        questionnaire.setIDCard(getPara("IDCard"));
        questionnaire.setPhone(getPara("phone"));
        questionnaire.setNationID(getParaToLong("nationID"));
        questionnaire.setDegreeOfEducationID(getParaToLong("degreeOfEducationID"));
        questionnaire.setOccupationID(getParaToLong("occupationID"));
        questionnaire.setCreateUserID(loginUser.getId());
        if (!questionnaireService.save(questionnaire)){
            throw new BusinessException("保存失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 修改调查问卷
     */
    private void postUpdate(Long id){
        // 在html设置类型
        Questionnaire questionnaireNotNull = new Questionnaire();
        Questionnaire questionnaire = questionnaireService.findById(id);//原本信息
        questionnaire.setLastAccessTime(new Date());
        questionnaireNotNull.setAge(getParaToInt("age"));
        if ( questionnaireNotNull.getAge() != null){
            questionnaire.setAge(questionnaireNotNull.getAge());
        }
        questionnaireNotNull.setSurveyTime(getParaToDate("surveyTime"));
        if (questionnaireNotNull.getSurveyTime() != null){
            questionnaire.setSurveyTime(questionnaireNotNull.getSurveyTime());
        }
        questionnaireNotNull.setDepartment(getPara("department"));
        if (questionnaireNotNull.getDepartment() != null){
            questionnaire.setDepartment(questionnaireNotNull.getDepartment());
        }
        questionnaireNotNull.setType(getParaToInt("type"));
        if (questionnaireNotNull.getType() != null){
            questionnaire.setType(questionnaireNotNull.getType());
        }
        questionnaireNotNull.setUnit(getPara("unit"));
        if (questionnaireNotNull.getUnit() != null){
            questionnaire.setUnit(questionnaireNotNull.getUnit());
        }
        questionnaireNotNull.setUnitAddress(getPara("unitAddress"));
        if (questionnaireNotNull.getUnitAddress() != null){
            questionnaire.setUnitAddress(questionnaireNotNull.getUnitAddress());
        }
        questionnaireNotNull.setUnitTel(getPara("unitTel"));
        if (questionnaireNotNull.getUnitTel() != null){
            questionnaire.setUnitTel(questionnaireNotNull.getUnitTel());
        }
        questionnaireNotNull.setPersonName(getPara("name"));
        if (questionnaireNotNull.getPersonName() != null){
            questionnaire.setPersonName(questionnaireNotNull.getPersonName());
        }
        questionnaireNotNull.setPersonGender(getParaToInt("sex"));
        if (questionnaireNotNull.getPersonGender() != null){
            questionnaire.setPersonGender(questionnaireNotNull.getPersonGender());
        }
        questionnaireNotNull.setAddr(getPara("addr"));
        if (questionnaireNotNull.getAddr() != null){
            questionnaire.setAddr(questionnaireNotNull.getAddr());
        }
        questionnaireNotNull.setPhone(getPara("phone"));
        if (questionnaireNotNull.getPhone() != null){
            questionnaire.setPhone(questionnaireNotNull.getPhone());
        }
        questionnaireNotNull.setIDCard(getPara("IDCard"));
        if (questionnaireNotNull.getIDCard() != null){
            questionnaire.setIDCard(questionnaireNotNull.getIDCard());
        }
        questionnaireNotNull.setNationID(getParaToLong("nationID"));
        if (questionnaireNotNull.getNationID() != null){
            questionnaire.setNationID(questionnaireNotNull.getNationID());
        }
        questionnaireNotNull.setDegreeOfEducationID(getParaToLong("degreeOfEducationID"));
        if (questionnaireNotNull.getDegreeOfEducationID() != null){
            questionnaire.setDegreeOfEducationID(questionnaireNotNull.getDegreeOfEducationID());
        }
        questionnaireNotNull.setOccupationID(getParaToLong("occupationID"));
        if (questionnaireNotNull.getOccupationID() != null){
            questionnaire.setOccupationID(questionnaireNotNull.getOccupationID());
        }
        if (!questionnaireService.update(questionnaire)){
            throw new BusinessException("保存失败！");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 调查内容table
     */
    public void tableData(){
        Long projectId = getParaToLong("pid");
        Questionnaire questionnaire = questionnaireService.findByProjectID(projectId);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        if (questionnaire.getId() != null) {
            // 在关联表通过 调查问卷Id  返回调查内容Id 集合
            Long[] longs = questionnaireContentLinkService.findContentIdByQuestionnaireId(questionnaire.getId());
            //通过调查内容IDList查询 返回page表格
            Page<QuestionnaireContent> page = questionnaireContentService.findPageById(longs, pageNumber, pageSize);
            setAttr("pid",projectId).renderJson(new DataTable<QuestionnaireContent>(page));
        }else{
            Questionnaire questionnaire1 = new Questionnaire();
            questionnaire.setLastAccessTime(new Date());
            questionnaire.setCreateTime(new Date());
            questionnaire.setProjectID(projectId);
            if (!questionnaireService.save(questionnaire)){
                throw new BusinessException("保存失败！");
            }
            //将新建的ID传进去
            questionnaire1 = questionnaireService.findByProjectID(projectId);
            Page<QuestionnaireContent> page = questionnaireContentService.findPageById(new Long[]{questionnaire1.getId()}, pageNumber, pageSize);
            setAttr("pid",projectId).renderJson(new DataTable<QuestionnaireContent>(page));
        }
    }

    /**
     * 修改调查内容
     */
    @NotNullPara("id")
    public void updateTable(){
        Long contentId = getParaToLong("id");
        QuestionnaireContent questionnaireContent = questionnaireContentService.findById(contentId);
        if (questionnaireContent != null){
            questionnaireContent.setName(getPara("name"));
            questionnaireContent.setContent(getPara("content"));
            if(!questionnaireContentService.update(questionnaireContent)){
                throw new BusinessException("保存失败！");
            }
        }else {
            throw new BusinessException("内容不存在！");
        }
    }

    /**
     * 删除调查内容(调查内容表)
     */
    @NotNullPara({"id"})
    public void delete() {
        Long contentId = getParaToLong("id");
        Long linkId = questionnaireContentLinkService.findIdByContentId(contentId);
        if (linkId != null){
            if (!questionnaireContentLinkService.deleteById(linkId)){
                throw new BusinessException("删除失败");
            }
        }
        if (!questionnaireContentService.deleteById(contentId)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 添加调查内容
     */
    @NotNullPara("pid")
    public void addContent(){
        Questionnaire questionnaire = questionnaireService.findByProjectID(getParaToLong("pid"));
        QuestionnaireContent questionnaireContent = new QuestionnaireContent();
        Date date = new Date();
        questionnaireContent.setCreateTime(date);
        questionnaireContent.setRemark(date.toString());
        if (!questionnaireContentService.save(questionnaireContent)) {
            throw new BusinessException("添加失败");
        }
        questionnaireContent = questionnaireContentService.findByModel(questionnaireContent);
        if (questionnaireContent != null){
            QuestionnaireContentLink questionnaireContentLink = new QuestionnaireContentLink();
            questionnaireContentLink.setQuestionnaireID(questionnaire.getId());
            questionnaireContentLink.setQuestionnaireContentID(questionnaireContent.getId());
            if (!questionnaireContentLinkService.save(questionnaireContentLink)){
                throw new BusinessException("添加失败");
            }
        }else {
            throw new BusinessException("添加失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
