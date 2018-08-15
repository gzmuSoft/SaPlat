package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.ProjectFileTypeService;
import io.jboot.admin.service.entity.model.ProjectFileType;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ProjectAssTypeService;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.admin.service.entity.model.ProjectAssType;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ProjectAssTypeServiceImpl extends JbootServiceBase<ProjectAssType> implements ProjectAssTypeService {

    @JbootrpcService
    private ProjectFileTypeService projectFileTypeService;

    /**
     * find all model
     * @param model 项目评估类型
     * @return all <ProjectAssType>
     */
    @Override
    public List<ProjectAssType> findAll(ProjectAssType model)
    {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())){
            columns.like("name", "%" + model.getName()+"%");
        }
        if (StrKit.notNull(model.getIsEnable())){
            columns.eq("isEnable", model.getIsEnable());
        }
        return DAO.findListByColumns(columns);
    }

    @Override
    public Page<ProjectAssType> findPage(ProjectAssType model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())){
            columns.like("name", "%" + model.getName()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id asc");
    }

    @Override
    public boolean isExisted(String name) {
        return findByName(name) != null;
    }

    @Override
    public ProjectAssType findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public boolean save(ProjectAssType model){
        return Db.tx(() -> {
            ProjectFileType parent=projectFileTypeService.findByName("立项");
            ProjectFileType projectFileType=new ProjectFileType();
            projectFileType.setParentID(parent.getId());
            projectFileType.setCreateUserID(model.getCreateUserID());
            projectFileType.setName(model.getName());
            projectFileType.setRemark("立项评估类型");
            return projectFileType.save()&&model.save();
        });
    }

    @Override
    public boolean update(ProjectAssType model){
        return Db.tx(() -> {
            ProjectFileType parent=projectFileTypeService.findByName("立项");
            ProjectAssType oldModel = findById(model.getId());
            ProjectFileType projectFileType=projectFileTypeService.findByNameAndParentID(oldModel.getName(),parent.getId());
            projectFileType.setName(model.getName());
            return projectFileType.update()&&model.update();
        });
    }
}