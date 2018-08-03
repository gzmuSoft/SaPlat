package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.PoliticalStatus;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PoliticalStatusService;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class PoliticalStatusServiceImpl extends JbootServiceBase<PoliticalStatus> implements PoliticalStatusService {

    /**
     * find all model
     *
     * @param model 政治面貌
     * @return all <PoliticalStatus>
     */
    public List<PoliticalStatus> findAll(PoliticalStatus model) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (StrKit.notNull(model.getIsEnable())) {
            columns.eq("isEnable", model.getIsEnable());
        }
        return DAO.findListByColumns(columns);
    }

    @Override
    public Page<PoliticalStatus> findPage(PoliticalStatus model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public PoliticalStatus findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }
}