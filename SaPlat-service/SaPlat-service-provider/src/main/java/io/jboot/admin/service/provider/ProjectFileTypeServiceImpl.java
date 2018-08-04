package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ProjectFileTypeService;
import io.jboot.admin.service.entity.model.ProjectFileType;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ProjectFileTypeServiceImpl extends JbootServiceBase<ProjectFileType> implements ProjectFileTypeService {
    @Override
    public ProjectFileType findByName(String name) {
        return DAO.findFirstByColumn(Column.create("name", name));
    }

    @Override
    public List<ProjectFileType> findListByParentId(Long parentId) {
        return DAO.findListByColumn(Column.create("parentID", parentId));
    }

    @Override
    public List<ProjectFileType> findByParentId(Long parentId) {
        return DAO.findListByColumn(Column.create("parentID", parentId));
    }

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
}