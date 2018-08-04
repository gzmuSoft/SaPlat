package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.admin.service.api.StructPersonLinkService;
import io.jboot.admin.service.entity.model.StructPersonLink;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.LinkedList;
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
     * @param orgStructureId
     * @return
     */
    @Override
    public Map<String,Object> findByStructIdAndUsername(Long orgStructureId){
        List<Record> list = Db.find("SELECT person.ID,person.personID,user.name,person.structID,person.createUserID,person.createTime,person.isEnable FROM `struct_person_link` as person,`sys_user` as user where person.personID = user.id and person.structID = ?",orgStructureId);
        Map<String,Object> map = new ConcurrentHashMap<String, Object>();
        map.put("code","0");
        map.put("msg","");
        map.put("count",list.size());
        map.put("data",list);
        return map;
    }
    @Override
    public Map<String,Object> findStructureListByPersonID(Long personID){
        List<Record> list = Db.find("select person.*,struct.name from `struct_person_link` person, `org_structure` struct where person.structID = struct.id and person.personID = ?",personID);
        //构造layui表格数据格式
        Map<String,Object> map = new ConcurrentHashMap<String,Object>();
        map.put("code","0");
        map.put("msg","");
        map.put("count",list.size());
        map.put("data",list);
        return map;
    }
}


