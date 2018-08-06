package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ProjectService;
import io.jboot.admin.service.api.QuestionnaireContentLinkService;
import io.jboot.admin.service.api.QuestionnaireContentService;
import io.jboot.admin.service.entity.model.Project;
import io.jboot.admin.service.entity.model.QuestionnaireContent;
import io.jboot.admin.service.entity.model.QuestionnaireContentLink;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.QuestionnaireService;
import io.jboot.admin.service.entity.model.Questionnaire;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    //删除
    public boolean deleteQuestionnaire(Long questionnaireId, Long[] contentIds, Long[] linkIds){
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
    public boolean updateQuestionnaire(Questionnaire questionnaire, List<QuestionnaireContent> contents, Project project){
        return Db.tx(() -> {
            if (!projectService.update(project)){
                return false;
            }
            //删除原有的关联
            Long[] contentIds = questionnaireContentLinkService.findContentIdByQuestionnaireId(questionnaire.getId());
            //循环删除内容的关联
            for (Long contentId : contentIds) {
                Long id = questionnaireContentLinkService.findIdByContentId(contentId);//连接id
                if (id != null) {
                    if (!questionnaireContentLinkService.deleteById(id))
                        return false;
                }
            }
            //删除内容
            for (Long contentId : contentIds) {
                if (!questionnaireContentService.deleteById(contentId))
                    return false;
            }
            if (!update(questionnaire)) {
                return false;
            }
            //关联
            QuestionnaireContentLink questionnaireContentLink;
            for (QuestionnaireContent content : contents) {
                if (!questionnaireContentService.save(content)) {
                    return false;
                }else {
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
            if (!projectService.update(project)){
                return false;
            }
            if (!save(questionnaire)) {
                return false;
            }
            QuestionnaireContentLink questionnaireContentLink;
            for (QuestionnaireContent content : contents){
                if (!questionnaireContentService.save(content)) {
                    return false;
                }else {
                    questionnaireContentLink = new QuestionnaireContentLink();
                    questionnaireContentLink.setLastAccessTime(new Date());
                    questionnaireContentLink.setQuestionnaireID(questionnaire.getId());
                    questionnaireContentLink.setQuestionnaireContentID(content.getId());
                    if (!questionnaireContentLinkService.save(questionnaireContentLink)){
                        return false;
                    }
                }
            }
            return true;
        });
    }

    @Override
    public Page<Questionnaire> findPage(Questionnaire model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (model.getProjectID() != null){
            columns.eq("projectID",model.getProjectID());
        }
        if (model.getIsEnable() != null){
            columns.eq("isEnable",model.getIsEnable());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList());
    }

}