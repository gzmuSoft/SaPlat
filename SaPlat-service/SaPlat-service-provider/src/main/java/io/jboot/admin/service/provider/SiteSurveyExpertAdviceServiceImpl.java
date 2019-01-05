package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ExpertGroupService;
import io.jboot.admin.service.api.SaPlatService;
import io.jboot.admin.service.entity.model.ExpertGroup;
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
public class SiteSurveyExpertAdviceServiceImpl extends JbootServiceBase<SiteSurveyExpertAdvice> implements SiteSurveyExpertAdviceService{

    private ExpertGroupServiceImpl expertGroupService=new ExpertGroupServiceImpl();

    /**
     * 装配完善 list 对象中所有对象的数据
     * @param list
     * @return
     */
    public List<SiteSurveyExpertAdvice> fitList(List<SiteSurveyExpertAdvice> list){
        if(list != null){
            for (SiteSurveyExpertAdvice item: list) {
                fitModel(item);
            }
        }
        return list;
    }

    /**
     * 装配完善 Page 对象中所有对象的数据
     * @param page
     * @return
     */
    public Page<SiteSurveyExpertAdvice> fitPage(Page<SiteSurveyExpertAdvice> page){
        if(page != null){
            List<SiteSurveyExpertAdvice> tList = page.getList();
            for (SiteSurveyExpertAdvice item: tList) {
                fitModel(item);
            }
        }
        return page;
    }
    /**
     * 装配单个实体对象的数据
     *
     * @param model
     * @return
     */
    public SiteSurveyExpertAdvice fitModel(SiteSurveyExpertAdvice model) {
        if (null != model) {
            try {
                ExpertGroup expertGroup = expertGroupService.findById(model.getExpertID());
                if (expertGroup != null) {
                    model.setExpertName(expertGroup.getName());
                }
            } catch (Exception ex){
                return model;
            }
        }
        return model;
    }

    public SiteSurveyExpertAdvice findByColumn(String columnName, String value, String logic) {
        return fitModel(DAO.findFirstByColumn(Column.create(columnName, value, logic)));
    }

    @Override
    public List<SiteSurveyExpertAdvice> findListByProjectId(Long projectId) {
        return fitList(DAO.findListByColumn(Column.create("projectID", projectId)));
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
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList()));
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
        return fitModel(DAO.findFirstByColumns(columns));
    }
}