package io.jboot.admin.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.ZTree;
import io.jboot.admin.service.api.ProjectFileTypeService;
import io.jboot.admin.service.entity.model.ProjectFileType;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.ArrayList;
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
    public List<ZTree> findTreeOnUse(Long parentID) {
        ProjectFileType parentProjectFileType=null;
        Columns columns = Columns.create();
        //设置isEnable为启用状态 1启用 0 禁用
        columns.eq("isEnable", 1);
        if(parentID!=null) {
            columns.eq("parentID",parentID);
            parentProjectFileType=DAO.findFirstByColumn("id",parentID);
        }
        List<ProjectFileType> list = DAO.findListByColumns(columns, "parentID desc, sort asc");
//        if(parentProjectFileType!=null){
//            list.add(parentProjectFileType);
//        }
        List<ZTree> zList = new ArrayList<ZTree>();
        for (ProjectFileType projectFileType : list) {
            ZTree ztree = new ZTree(projectFileType.getId(), projectFileType.getName(), projectFileType.getParentID());
            zList.add(ztree);
        }
        return zList;
    }

    @Override
    public ProjectFileType findByNameAndParentID(String name, Long parentID) {
        Columns columns=Columns.create();
        columns.eq("name",name);
        columns.eq("parentID",parentID);
        return DAO.findFirstByColumns(columns);
    }
}