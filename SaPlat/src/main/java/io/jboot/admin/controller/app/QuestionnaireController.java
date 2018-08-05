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

import java.util.ArrayList;
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
        //当前用户
        pmodel.setUserId(loginUser.getId());
        //这个位置穿点开项目的Id
        Project project = projectService.findAll(pmodel).get(0);

        for (UserRole userRole : roles) {
            if (userRole.getRoleID() == 2) {

                //加载民族
                Nation nmodel = new Nation();
                nmodel.setIsEnable(true);
                List<Nation> nations = nationService.findAll(nmodel);
                BaseStatus nationStatus = new BaseStatus() {
                };
                for (Nation nation : nations) {
                    nationStatus.add(nation.getId().toString(), nation.getName());
                }
                //加载学历
                Educational emodel = new Educational();
                emodel.setIsEnable(true);
                List<Educational> educationals = educationalService.findAll(emodel);
                BaseStatus educationalStatus = new BaseStatus() {
                };
                for (Educational educational : educationals) {
                    educationalStatus.add(educational.getId().toString(), educational.getName());
                }
                //加载职业
                Occupation omodel = new Occupation();
                omodel.setIsEnable(true);
                List<Occupation> occupations = occupationService.findAll(omodel);
                BaseStatus occupationStatus = new BaseStatus() {
                };
                for (Occupation item : occupations) {
                    occupationStatus.add(item.getId().toString(), item.getName());
                }
                setAttr("type", 0).
                        setAttr("project", project).
                        setAttr("occupationStatus", occupationStatus).
                        setAttr("educationalStatus", educationalStatus).
                        setAttr("nationStatus", nationStatus).
                        render("personMain.html");
                break;
            } else {
                setAttr("type", 1).
                        setAttr("project", project).
                        render("organizationMain.html");
                break;
            }
        }
    }

    /*
     *提交保存调查问卷
     */
    public void add() {
        User loginUser = AuthUtils.getLoginUser();
        //项目资料
        Project project = getBean(Project.class, "project");
        project.setLastAccessTime(new Date());
        project.setLastUpdateUserID(loginUser.getId());
        //调查问卷
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setLastAccessTime(new Date());
        questionnaire.setCreateTime(new Date());
        questionnaire.setProjectID(project.getId());
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
        //调查内容
        String[] questionnaireContents = getParaValues("content");
        List<QuestionnaireContent> contents = new ArrayList<QuestionnaireContent>();
        QuestionnaireContent questionnaireContent;
        for (int i = 0; i < questionnaireContents.length ; i++) {
            questionnaireContent = new QuestionnaireContent();
            questionnaireContent.setContent(questionnaireContents[i]);
            questionnaireContent.setCreateTime(new Date());
            questionnaireContent.setLastAccessTime(new Date());
            questionnaireContent.setCreateUserID(loginUser.getId());
            contents.add(i,questionnaireContent);
        }
        if (!questionnaireService.saveQuestionnairen(questionnaire,contents,project)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
