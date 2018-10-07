package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.RejectProjectInfoService;
import io.jboot.admin.service.entity.model.RejectProjectInfo;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class RejectProjectInfoServiceImpl extends JbootServiceBase<RejectProjectInfo> implements RejectProjectInfoService {
    @Override
    public Page<RejectProjectInfo> findPage(RejectProjectInfo rejectProjectInfo, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(rejectProjectInfo.getRemark())) {
            columns.eq("remark", rejectProjectInfo.getRemark());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }
}