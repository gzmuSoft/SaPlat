package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ExpertGroupService;
import io.jboot.admin.service.entity.model.ExpertGroup;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class ExpertGroupServiceImpl extends JbootServiceBase<ExpertGroup> implements ExpertGroupService {

    @Override
    public Page<ExpertGroup> findPage(ExpertGroup expert_group, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(expert_group.getName())){
            columns.like("name", "%"+expert_group.getName()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean hasExpertGroup(String name) {
        return findByName(name) != null;
    }

    @Override
    public ExpertGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }
}