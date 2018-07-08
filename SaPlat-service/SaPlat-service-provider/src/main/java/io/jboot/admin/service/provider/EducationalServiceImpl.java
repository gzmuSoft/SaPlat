package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Educational;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.EducationalService;
import io.jboot.admin.service.entity.model.Educational;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class EducationalServiceImpl extends JbootServiceBase<Educational> implements EducationalService {
    @Override
    public Page<Educational> findPage(Educational model, int pageNumber, int pageSize) {
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
    public Educational findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

}