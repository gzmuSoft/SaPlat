package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ReviewGroupService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.ReviewGroup;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class ReviewGroupServiceImpl extends JbootServiceBase<ReviewGroup> implements ReviewGroupService {
    @Override
    public ReviewGroup findByOrgId(Long orgId) {
        return DAO.findFirstByColumn("orgID", orgId);
    }

    @Override
    public boolean saveOrUpdate(ReviewGroup model, Auth auth) {
        return Db.tx(() -> model.saveOrUpdate() && auth.saveOrUpdate());
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public ReviewGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }


    @Override
    public Page<ReviewGroup> findPage(ReviewGroup reviewGroup, int pageNumber, int pageSize){
        Columns columns = Columns.create();
        if(null!=reviewGroup.getName()){
            columns.like("name", "%" + reviewGroup.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id");
    }
}