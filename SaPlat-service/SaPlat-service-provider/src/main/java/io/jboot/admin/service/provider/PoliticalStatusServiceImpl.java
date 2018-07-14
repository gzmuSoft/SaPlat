package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.PoliticalStatus;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.PoliticalStatusService;
import io.jboot.admin.service.entity.model.PoliticalStatus;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class PoliticalStatusServiceImpl extends JbootServiceBase<PoliticalStatus> implements PoliticalStatusService {
    @Override
    public Page<PoliticalStatus> findPage(PoliticalStatus model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())){
            columns.like("name", "%" + model.getName()+"%");
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