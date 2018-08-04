package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Db;
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
        List<StructPersonLink> list = Db.query("SELECT person.ID,person.personID,user.name,person.structID,person.createUserID,person.createTime,person.isEnable FROM `struct_person_link` as person,`sys_user` as user where person.personID = user.id and person.structID = ?",orgStructureId);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("code","0");
        map.put("msg","");
        map.put("count",list.size());
        map.put("data",list);
        return map;
    }
    @Override
    public List<StructPersonLink> findByPersonID(Long personID){
        List<StructPersonLink> list = new LinkedList<StructPersonLink>();
        return list;
    }
}