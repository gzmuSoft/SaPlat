package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.InitialRiskExpertiseService;
import io.jboot.admin.service.entity.model.InitialRiskExpertise;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class InitialRiskExpertiseServiceImpl extends JbootServiceBase<InitialRiskExpertise> implements InitialRiskExpertiseService {


    @Override
    public Page<InitialRiskExpertise> findPage(InitialRiskExpertise model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (model.getProjectID() != null){
            columns.eq("projectID",model.getProjectID());
        }
        if (model.getIsEnable() != null){
            columns.eq("isEnable",model.getIsEnable());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList());
    }

    @Override
    public List<InitialRiskExpertise> findByProjectId(Long projectId) {
        return DAO.findListByColumn("projectID",projectId);
    }

}