package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.entity.model.Questionnaire;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.QuestionnaireContentService;
import io.jboot.admin.service.entity.model.QuestionnaireContent;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class QuestionnaireContentServiceImpl extends JbootServiceBase<QuestionnaireContent> implements QuestionnaireContentService {
    /**
     * 改需求后废弃的方法 。。。
     * 分页查询系统信息
     * @param ids ids
     * @return 页
     */
    @Override
    public Page<QuestionnaireContent> findPageById(Long[] ids, int pageNumber, int pageSize){
        String select = "SELECT * ";
        StringBuilder sql = new StringBuilder("FROM questionnaire_content WHERE `id` IN(");
        for (int i = 0; i < ids.length; i++) {
            if (i != ids.length - 1){
               sql.append("?,");
            } else {
                sql.append("?)");
            }
        }
        return DAO.paginate(pageNumber, pageSize, false, select, sql.toString(),ids);
    }

    /**
     * find model by primary key
     *
     * @param model
     * @return
     */
    @Override
    public QuestionnaireContent findByModel(QuestionnaireContent model){
        if (model == null)
            return null;
        List<QuestionnaireContent> list = DAO.findListByColumn("content",model.getContent());
        if (list.size() != 0){
            return list.get(0);
        }else
            return null;
    }
}