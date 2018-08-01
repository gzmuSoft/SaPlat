package io.jboot.admin.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.EvaSchemeService;
import io.jboot.admin.service.entity.model.EvaScheme;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class EvaSchemeServiceImpl extends JbootServiceBase<EvaScheme> implements EvaSchemeService {
    /**
     * 根据当前项目ID判断是否有对应的评估方案
     * @param id
     * @return
     */
    @Override
    public EvaScheme findByProjectID(Object id){
        if (id == null)
            return null;
        List<EvaScheme> list = DAO.findListByColumn("projectID",id);
        if (list.size()!= 0){
            return list.get(0);
        }else
            return null;
    }
}