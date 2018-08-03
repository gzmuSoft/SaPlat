package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ProjectFileTypeService;
import io.jboot.admin.service.entity.model.ProjectFileType;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ProjectFileTypeServiceImpl extends JbootServiceBase<ProjectFileType> implements ProjectFileTypeService {
    @Override
    public Page<ProjectFileType> findPage(ProjectFileType projectFileType, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (projectFileType.getName() != null) {
            columns.like("name", "%" + projectFileType.getName() + "%");
        }
        if (projectFileType.getParentID() != null) {
            columns.eq("parentID", projectFileType.getParentID());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList());
    }

    @Override
    public List<ProjectFileType> findByParentId(Long parentId) {
        Columns columns = Columns.create();
        columns.eq("parentID", parentId);
        return DAO.findListByColumns(columns);
    }
}