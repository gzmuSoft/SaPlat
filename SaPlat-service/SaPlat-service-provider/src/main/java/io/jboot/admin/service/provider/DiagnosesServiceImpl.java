package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.DiagnosesService;
import io.jboot.admin.service.entity.model.Diagnoses;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class DiagnosesServiceImpl extends JbootServiceBase<Diagnoses> implements DiagnosesService {

    @Override
    public List<Diagnoses> findListByProjectId(Long projectId) {
        return DAO.findListByColumn(Column.create("project",projectId));
    }

    @Override
    public Page<Diagnoses> findPage(Diagnoses diagnoses, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (diagnoses.getProject() != null){
            columns.eq("project",diagnoses.getProject());
        }

        if (diagnoses.getIsEnable() != null) {
            columns.eq("isEnable", diagnoses.getIsEnable());
        }

        if (diagnoses.getName() != null) {
            columns.like("name", "%" + diagnoses.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList());
    }
}