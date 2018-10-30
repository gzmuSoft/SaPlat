package io.jboot.admin.service.provider;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.api.StructPersonLinkService;
import io.jboot.admin.service.entity.model.StructPersonLink;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Bean
@Singleton
@JbootrpcService
public class StructPersonLinkServiceImpl extends JbootServiceBase<StructPersonLink> implements StructPersonLinkService {

    @Override
    public List<StructPersonLink> findByStructId(Long orgStructureId) {
        return DAO.findListByColumn("structID", orgStructureId);
    }

    /**
     * 架构人员列表
     *
     * @param orgStructureId
     * @return
     */
    @Override
    public Map<String, Object> findByStructIdAndUsername(Long orgStructureId) {
        List<Record> list = Db.find("SELECT person.ID,person.personID,user.name,person.structID,person.createUserID,person.createTime,person.isEnable FROM `struct_person_link` as person,`sys_user` as user where person.personID = user.userID and person.structID = ?", orgStructureId);
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("code", "0");
        map.put("msg", "");
        map.put("count", list.size());
        map.put("data", list);
        return map;
    }

    @Override
    public Map<String, Object> findStructureListByPersonID(Long personID) {
        List<Record> list = Db.find("select person.*,struct.name from `struct_person_link` person, `org_structure` struct where person.structID = struct.id and person.personID = ?", personID);
        //构造layui表格数据格式
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("code", "0");
        map.put("msg", "");
        map.put("count", list.size());
        map.put("data", list);
        return map;
    }

    @Override
    public List<StructPersonLink> findByStructIdAndUserID(Long structID, Long userID) {
        Columns columns = Columns.create();
        columns.eq("structID", structID);
        columns.eq("personID", userID);
        return DAO.findListByColumns(columns.getList());
    }

    /**
     * 个人群体 - 我加入的架构列表
     * @param pageNumber
     * @param pageSize
     * @param personID
     * @return
     */
    @Override
    public Page<Record> findStructListPageByPersonID(int pageNumber, int pageSize, Long personID) {
        SqlPara sqlPara = Db.getSqlPara("app-OrgStruct.myStructure");
        //添加参数
        sqlPara.addPara(personID);
        Page<Record> page = Db.paginate(pageNumber,pageSize,sqlPara);
        return page;
    }

    @Override
    /**
     * 根据用户的userID查询用户的基本信息
     */
    public List<Record> getOrgPersonInfo(Long userId) {
        Kv para = Kv.by("userID",userId);
        SqlPara sqlPara = Db.getSqlPara("app-OrgStruct.OrgPersonInfo",para);
//        System.out.println(sqlPara);
        List<Record> list = Db.find(sqlPara);
        return list;
    }

    /**
     * 根据组织的组织id查询加入当前组织的人员列表
     * @param pagenumber
     * @param pageSize
     * @param OrganizationId
     * @return
     */
    @Override
    public Page<Record> getOrgPersonList(int pagenumber, int pageSize, Long OrganizationId) {
        //添加参数
        Kv para = Kv.by("OrgId",OrganizationId);
        SqlPara sqlPara = Db.getSqlPara("app-OrgStruct.OrgPersonList",para);
        return Db.paginate(pagenumber,pageSize,sqlPara);
    }

    /**
     * 根据组织的组织id和orgType查询加入当前组织的人员列表
     *
     * @param pagenumber
     * @param pageSize
     * @param OrganizationId
     * @param orgType
     * @return
     */
    @Override
    public Page<Record> OrgPersonListByType(int pagenumber, int pageSize, Long OrganizationId, Long orgType) {
        //添加参数
        Kv para = Kv.by("OrgId", OrganizationId).set("orgType", orgType);
        SqlPara sqlPara = Db.getSqlPara("app-OrgStruct.OrgPersonListByType", para);
        return Db.paginate(pagenumber, pageSize, sqlPara);
    }

    /**
     * 组织管理  -  架构人员列表
     * @param pageNumber
     * @param pageSize
     * @param orgStructureId
     * @return
     */
    @Override
    public Page<Record> findPersonListByStructId(int pageNumber,int pageSize, Long orgStructureId){
        SqlPara sqlPara = Db.getSqlPara("app-OrgStruct.StructurePersonList");
        //添加参数
        sqlPara.addPara(orgStructureId);

        Page<Record> page = Db.paginate(pageNumber,pageSize,sqlPara);
        return page;
    }
}


