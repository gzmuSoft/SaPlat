package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.admin.service.api.EvaSchemeService;
import io.jboot.admin.service.api.ImpTeamService;
import io.jboot.admin.service.entity.model.*;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ImpTeamServiceImpl extends JbootServiceBase<ImpTeam> implements ImpTeamService {

    @Override
    public List<ImpTeam> findByUserID(Long id) {
        Columns columns = Columns.create();
        columns.eq("createUserID", id);
        return DAO.findListByColumns(columns);
    }

    @Override
    public ImpTeam findByUserIDAndProjectID(Long userId, Long projectId) {
        Columns columns = Columns.create();
        columns.eq("createUserID", userId);
        columns.eq("projectID", projectId);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public ImpTeam findByProjectId(Long id) {
        return DAO.findFirstByColumn(Column.create("projectID", id));
    }

    @Override
    public List<ImpTeam> findByExpertGroup(ExpertGroup... expertGroups) {
        Columns columns = Columns.create();
        for (ExpertGroup expertGroup : expertGroups) {
            columns.like("expertGroupIDs", "%" + expertGroup.getId() + "%");
        }
        return DAO.findListByColumns(columns);
    }

    @Override
    public boolean save(ImpTeam model, EvaScheme evaScheme, List<ScheduledPlan> scheduledPlans, FileForm fileForm) {
        return Db.tx(() -> {
            if (!evaScheme.save()) {
                return false;
            }
            if (evaScheme.getProjectID() != null) {
                for (ScheduledPlan scheduledPlan : scheduledPlans) {
                    scheduledPlan.setEvaSchemeID(evaScheme.getId());
                    if (!scheduledPlan.save()) {
                        return false;
                    }
                }
            }
            fileForm.setRecordID(evaScheme.getId());
            return Db.tx(() -> model.save() && fileForm.update());
        });
    }

}