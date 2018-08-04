package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.SiteSurveyExpertAdviceService;
import io.jboot.admin.service.entity.model.SiteSurveyExpertAdvice;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class SiteSurveyExpertAdviceServiceImpl extends JbootServiceBase<SiteSurveyExpertAdvice> implements SiteSurveyExpertAdviceService {

    @Override
    public SiteSurveyExpertAdvice findByColumn(String columnName, String value, String logic) {
        return DAO.findFirstByColumn(Column.create(columnName, value, logic));
    }

    @Override
    public List<SiteSurveyExpertAdvice> findListByProjectId(Long projectId) {
        return DAO.findListByColumn(Column.create("projectID", projectId));
    }

    @Override
    public Page<SiteSurveyExpertAdvice> findPage(SiteSurveyExpertAdvice siteSurveyExpertAdvice, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (siteSurveyExpertAdvice.getIsEnable() != null) {
            columns.eq("isEnable", siteSurveyExpertAdvice.getIsEnable());
        }
        if (siteSurveyExpertAdvice.getProjectID() != null) {
            columns.eq("projectID", siteSurveyExpertAdvice.getProjectID());
        }
        if (siteSurveyExpertAdvice.getName() != null) {
            columns.like("name", "%" + siteSurveyExpertAdvice.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList());
    }


    @Override
    public SiteSurveyExpertAdvice findByColumns(String[] columnNames, Object[] values) {
        Columns columns = Columns.create();
        if (columnNames.length != values.length) {
            return null;
        }
        for (int i = 0; i < columnNames.length; i++) {
            columns.eq(columnNames[i], values[i]);
        }
        return DAO.findFirstByColumns(columns);
    }
}