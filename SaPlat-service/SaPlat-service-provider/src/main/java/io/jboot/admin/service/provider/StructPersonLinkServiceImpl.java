package io.jboot.admin.service.provider;

import io.jboot.admin.service.api.StructPersonLinkService;
import io.jboot.admin.service.entity.model.StructPersonLink;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class StructPersonLinkServiceImpl extends JbootServiceBase<StructPersonLink> implements StructPersonLinkService {

    @Override
    public List<StructPersonLink> findByStructId(Long orgStructureId) {
        return DAO.findListByColumn("structID", orgStructureId);
    }
}