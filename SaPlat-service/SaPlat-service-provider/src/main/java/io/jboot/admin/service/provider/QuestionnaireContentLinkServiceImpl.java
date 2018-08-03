package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.QuestionnaireContentLinkService;
import io.jboot.admin.service.entity.model.QuestionnaireContentLink;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class QuestionnaireContentLinkServiceImpl extends JbootServiceBase<QuestionnaireContentLink> implements QuestionnaireContentLinkService {
    /**
     * 调查问卷ID查找属于问卷的内容ID
     *
     * @param id
     * @return all QuestionnaireContentID
     */
    @Override
    public Long[] findContentIdByQuestionnaireId(Long id) {
        Columns columns = Columns.create();
        if (id != null) {
            columns.eq("questionnaireID", id);
        }
        List<QuestionnaireContentLink> questionnaireContentLinks = DAO.findListByColumns(columns);
        Long[] contentIds = new Long[questionnaireContentLinks.size()];
        for (int i = 0; i < questionnaireContentLinks.size(); i++) {
            contentIds[i] = questionnaireContentLinks.get(i).getQuestionnaireContentID();
        }
        return contentIds;
    }

    /**
     * 根据调查内容id 查出关联id
     *
     * @param contentId
     * @return all QuestionnaireContentID
     */
    @Override
    public Long findIdByContentId(Long contentId) {
        if (contentId == null) {
            return null;
        }
        List<QuestionnaireContentLink> list = DAO.findListByColumn("questionnaireContentID", contentId);
        if (list.size() != 0) {
            return list.get(0).getId();
        } else
            return null;
    }
}