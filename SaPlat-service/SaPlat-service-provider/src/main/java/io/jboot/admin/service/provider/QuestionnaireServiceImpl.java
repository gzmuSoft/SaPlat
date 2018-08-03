package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.QuestionnaireService;
import io.jboot.admin.service.entity.model.Questionnaire;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class QuestionnaireServiceImpl extends JbootServiceBase<Questionnaire> implements QuestionnaireService {

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
}