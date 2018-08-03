package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.OrgStructureService;
import io.jboot.admin.service.entity.model.OrgStructure;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class OrgStructureServiceImpl extends JbootServiceBase<OrgStructure> implements OrgStructureService {
    @Override
    public Page<OrgStructure> findPage(OrgStructure orgStructure, int pageNumber, int pageSize, Long uid, int orgType) {
        Columns columns = Columns.create();
        columns.eq("createUserID",uid);
        columns.eq("orgType",orgType);
        if (StrKit.notBlank(orgStructure.getName())){
            columns.like("name", "%"+orgStructure.getName()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }
}