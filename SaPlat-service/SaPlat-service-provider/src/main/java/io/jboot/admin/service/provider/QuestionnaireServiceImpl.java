package io.jboot.admin.service.provider;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class QuestionnaireServiceImpl extends JbootServiceBase<Questionnaire> implements QuestionnaireService {

    @Inject
    QuestionnaireContentService questionnaireContentService;
    @Inject
    QuestionnaireContentLinkService questionnaireContentLinkService;
    @Inject
    ProjectService projectService;
    @Inject
    FileProjectService fileProjectService;
    @Inject
    FilesService filesService;

    /**
     * 根据当前项目ID判断是否有对应的问卷
     *
     * @param id
     * @return
     */
    @Override
    public Questionnaire findByProjectID(Object id) {
        if (id == null)
            return null;
        List<Questionnaire> list = DAO.findListByColumn("projectID", id);
        if (list.size() != 0) {
            return list.get(0);
        } else
            return null;
    }

    @Override
    public boolean deleteQuestionnaire(Questionnaire model) {
        return Db.tx(() -> {
            model.setIsEnable(false);
            if (!update(model)){
                return false;
            }
            List<FileProject> fileProjects = fileProjectService.findByRemark(model.getId());
            for (FileProject fileProject : fileProjects) {
                fileProject.setIsEnable(false);
                if (!fileProjectService.update(fileProject)){
                    return false;
                }
                Files file = filesService.findById(fileProject.getFileID());
                file.setIsEnable(false);
                if (!filesService.update(file)){
                    return false;
                }
            }
            return true;
        });
    }

    //删除
    @Override
    public boolean deleteQuestionnaire(Long questionnaireId, Long[] contentIds, Long[] linkIds) {
        return Db.tx(() -> {
            for (int i = 0; i < linkIds.length; i++) {
                if (!questionnaireContentLinkService.deleteById(linkIds[i]))
                    return false;
            }
            for (int i = 0; i < contentIds.length; i++) {
                if (!questionnaireContentService.deleteById(contentIds[i]))
                    return false;
            }
            if (!deleteById(questionnaireId))
                return false;
            return true;
        });
    }

    //更新
    @Override
    public boolean updateQuestionnaire(Questionnaire questionnaire, List<QuestionnaireContent> contents, Project project) {
        return Db.tx(() -> {
            if (!projectService.update(project)) {
                return false;
            }
            //删除原有的关联
            Long[] contentIds = questionnaireContentLinkService.findContentIdByQuestionnaireId(questionnaire.getId());
            //循环删除内容的关联
            for (Long contentId : contentIds) {
                Long id = questionnaireContentLinkService.findIdByContentId(contentId);//连接id
                if (id != null) {
                    if (!questionnaireContentLinkService.deleteById(id)) {
                        return false;
                    }
                }
            }
            //删除内容
            for (Long contentId : contentIds) {
                if (!questionnaireContentService.deleteById(contentId)) {
                    return false;
                }
            }
            if (!update(questionnaire)) {
                return false;
            }
            //关联
            QuestionnaireContentLink questionnaireContentLink;
            for (QuestionnaireContent content : contents) {
                if (!questionnaireContentService.save(content)) {
                    return false;
                } else {
                    questionnaireContentLink = new QuestionnaireContentLink();
                    questionnaireContentLink.setQuestionnaireID(questionnaire.getId());
                    questionnaireContentLink.setQuestionnaireContentID(content.getId());
                    questionnaireContentLink.setLastAccessTime(new Date());
                    if (!questionnaireContentLinkService.save(questionnaireContentLink)) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    //保存
    @Override
    public boolean saveQuestionnaire(Questionnaire questionnaire, List<QuestionnaireContent> contents, Project project) {
        return Db.tx(() -> {
            if (!projectService.update(project)) {
                return false;
            }
            if (!save(questionnaire)) {
                return false;
            }
            QuestionnaireContentLink questionnaireContentLink;
            for (QuestionnaireContent content : contents) {
                if (!questionnaireContentService.save(content)) {
                    return false;
                } else {
                    questionnaireContentLink = new QuestionnaireContentLink();
                    questionnaireContentLink.setLastAccessTime(new Date());
                    questionnaireContentLink.setQuestionnaireID(questionnaire.getId());
                    questionnaireContentLink.setQuestionnaireContentID(content.getId());
                    if (!questionnaireContentLinkService.save(questionnaireContentLink)) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    @Override
    public boolean saveQuestionnaire(Questionnaire questionnaire, ArrayList<Integer> ids, Project project) {
        return Db.tx(() -> {
            if (!projectService.update(project)) {
                return false;
            }
            if (questionnaire.getId() != null) {
                if (!update(questionnaire)) {
                    return false;
                }
            } else {
                if (!save(questionnaire)) {
                    return false;
                }
            }
//            if (!saveOrUpdate(questionnaire)) {
//                return false;
//            }
            for (Integer id : ids) {
                FileProject fileProject = fileProjectService.findById(id);
                if (fileProject != null){
                    fileProject.setRemark(questionnaire.getId().toString());
                    if (!fileProjectService.saveOrUpdate(fileProject)) {
                        return false;
                    }
                }
            }
            return true;
        });
    }
    public int updatefileProjectRemark(String questionnaireID, String questionnaireOldID) {
        return Db.update("UPDATE file_project SET remark='" + questionnaireID + "' WHERE remark='" + questionnaireOldID + "'");
    }

    @Override
    public boolean saveQuestionnaire(Questionnaire questionnaire, String questionnaireOldID){
        return Db.tx(() -> {
            if (questionnaire.getId() != null) {
                if (!update(questionnaire)) {
                    return false;
                }
            } else {
                if (!save(questionnaire)) {
                    return false;
                }
            }

            int iAffect = updatefileProjectRemark(questionnaire.getId().toString(), questionnaireOldID);
            return iAffect > 0 ? true : false;
        });
    }

    @Override
    public Page<Questionnaire> findPage(Questionnaire model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (model.getProjectID() != null) {
            columns.eq("projectID", model.getProjectID());
        }
        if (model.getIsEnable() != null) {
            columns.eq("isEnable", model.getIsEnable());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList());
    }

}