package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.RiskPoint;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.RiskPointService;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class RiskPointServiceImpl extends JbootServiceBase<RiskPoint> implements RiskPointService {

    ProjectServiceImpl projectService = new ProjectServiceImpl();

    /**
     * 装配完善List对象中所有对象的数据
     *
     * @param list
     * @return
     */
    public List<RiskPoint> fitList(List<RiskPoint> list) {
        if (list != null) {
            for (RiskPoint item : list) {
                fitModel(item);
            }
        }
        return list;
    }

    /**
     * 装配完善Page对象中所有对象的数据
     *
     * @param page
     * @return
     */
    private Page<RiskPoint> fitPage(Page<RiskPoint> page) {
        if (page != null) {
            List<RiskPoint> tList = page.getList();
            for (RiskPoint item : tList) {
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
    public RiskPoint fitModel(RiskPoint model) {
        if (model != null) {
            if (model.getProjectID() > 0) {
                model.setProject(projectService.fitModel(projectService.findById(model.getProjectID())));
            }
        }
        return model;
    }
    /**
     * find all model
     *
     * @param model 风险点
     * @return all <RiskPoint>
     */
    @Override
    public List<RiskPoint> findAll(RiskPoint model) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (StrKit.notBlank(model.getRiskPoint())) {
            columns.like("riskPoint", "%" + model.getRiskPoint() + "%");
        }
        if (model.getProjectID() != null) {
            columns.eq("projectID", model.getProjectID());
        }
        if (StrKit.notNull(model.getIsEnable())) {
            columns.eq("isEnable", model.getIsEnable());
        }
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public Page<RiskPoint> findPage(RiskPoint model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (StrKit.notBlank(model.getRiskPoint())) {
            columns.like("riskPoint", "%" + model.getRiskPoint() + "%");
        }
        if (model.getProjectID() != null) {
            columns.eq("projectID", model.getProjectID());
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public RiskPoint findByName(String name) {
        return fitModel(DAO.findFirstByColumn("name", name));
    }

}