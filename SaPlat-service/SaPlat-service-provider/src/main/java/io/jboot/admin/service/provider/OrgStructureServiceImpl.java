package io.jboot.admin.service.provider;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.OrgStructureService;
import io.jboot.admin.service.entity.model.OrgStructure;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

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
    @Override
    public Page<Record> searchStructure(OrgStructure orgStructure, int pageNumber, int pageSize){
        Kv para = null;
        SqlPara sqlPara = null;
        if(orgStructure.getId() != null){
            para = Kv.by("structId",orgStructure.getId());
            sqlPara = Db.getSqlPara("app-OrgStruct.searchStructureByID",para);
        }else{
            String structName = orgStructure.getName()!=null?orgStructure.getName():"";
            para = Kv.by("structName",structName);
            sqlPara = Db.getSqlPara("app-OrgStruct.searchStructureByName",para);
        }
        return Db.paginate(pageNumber,pageSize,sqlPara);
    }
    @Override
    public Page<Record> findMainList(int pageNumber, int pageSize, Long uid, int orgTye,String structName){
        Kv para = Kv.by("uid",uid).set("orgType",orgTye).set("structName",structName);
        SqlPara sqlPara = Db.getSqlPara("app-OrgStruct.orgStructureList",para);
        Page<Record> page = Db.paginate(pageNumber,pageSize,sqlPara);
        return page;
    }

    @Override
    public List<OrgStructure> findByOrgIdAndType(Object orgId, int orgType){
        Columns columns = Columns.create();
        columns.eq("orgId",orgId);
        columns.eq("orgType",orgType);
        return DAO.findListByColumns(columns);
    }

}