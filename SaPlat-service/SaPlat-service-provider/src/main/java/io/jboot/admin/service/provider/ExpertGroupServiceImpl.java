package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.AuthService;
import io.jboot.admin.service.api.ExpertGroupService;
import io.jboot.admin.service.api.FilesService;
import io.jboot.admin.service.entity.model.Auth;
import io.jboot.admin.service.entity.model.ExpertGroup;
import io.jboot.admin.service.entity.model.Files;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ExpertGroupServiceImpl extends JbootServiceBase<ExpertGroup> implements ExpertGroupService {

    @Inject
    private AuthService authService;

    @Inject
    private FilesService filesService;

    @Override
    public Page<ExpertGroup> findPage(ExpertGroup model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (model.getIsEnable() != null) {
            columns.eq("isEnable", model.getIsEnable());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<ExpertGroup> findPage(int pageNumber, int pageSize) {
        return DAO.paginate(pageNumber, pageSize, "id desc");
    }

    @Override
    public boolean hasExpertGroup(String name) {
        return findByName(name) != null;
    }

    @Override
    public ExpertGroup findByPersonId(Long id) {
        Columns columns = Columns.create();
        columns.eq("personID", id);
        columns.eq("isEnable",1);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public boolean saveOrUpdate(ExpertGroup model, Auth auth, List<Files> files) {
        return Db.tx(() -> {
            if (!saveOrUpdate(model) || !authService.saveOrUpdate(auth)) {
                return false;
            }
            if (files == null) {
                return true;
            }
            for (Files file : files) {
                if (!filesService.update(file)) {
                    return false;
                }
            }
            return true;
        });
    }

    @Override
    public ExpertGroup findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public ExpertGroup findByOrgId(Long orgId) {
        return DAO.findFirstByColumn("orgID", orgId);
    }
}