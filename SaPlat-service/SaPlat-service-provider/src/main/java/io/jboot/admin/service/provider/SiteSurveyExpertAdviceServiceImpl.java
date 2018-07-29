package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.SiteSurveyExpertAdviceService;
import io.jboot.admin.service.entity.model.SiteSurveyExpertAdvice;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class SiteSurveyExpertAdviceServiceImpl extends JbootServiceBase<SiteSurveyExpertAdvice> implements SiteSurveyExpertAdviceService {

    @Override
    public SiteSurveyExpertAdvice findByColumn(String columnName, String value, String logic) {
        return DAO.findFirstByColumn(Column.create(columnName,value,logic));
    }

    @Override
    public SiteSurveyExpertAdvice findByColumns(String[] columnNames, Object[] values) {
        Columns columns = Columns.create();
        if (columnNames.length != values.length){
            return null;
        }
        for (int i = 0; i < columnNames.length; i++) {
            columns.eq(columnNames[i],values[i]);
        }
        return DAO.findFirstByColumns(columns);
    }
}