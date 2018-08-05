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

    @Override
    public boolean saveQuestionnair(Questionnaire questionnaire, List<QuestionnaireContent> contents, Project project) {
        return Db.tx(() -> {
            if (!projectService.update(project)){
                return false;
            }
            if (!save(questionnaire)) {
                return false;
            }else {
               questionnaire.setId(findByProjectID(project.getId()).getId());
            }
            for (QuestionnaireContent content : contents){
                if (!questionnaireContentService.save(content)) {
                    return false;
                }
                QuestionnaireContent questionnaireContent = questionnaireContentService.findByModel(content);
                QuestionnaireContentLink questionnaireContentLink = new QuestionnaireContentLink();
                questionnaireContentLink.setQuestionnaireID(questionnaire.getId());
                questionnaireContentLink.setQuestionnaireContentID(questionnaireContent.getId());
                if (!questionnaireContentLinkService.save(questionnaireContentLink)){
                    return false;
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