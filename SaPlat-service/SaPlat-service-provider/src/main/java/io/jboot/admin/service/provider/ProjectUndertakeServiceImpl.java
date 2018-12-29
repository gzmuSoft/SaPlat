package io.jboot.admin.service.provider;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.api.ProjectUndertakeService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.ProjectUndertake;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ProjectUndertakeServiceImpl extends JbootServiceBase<ProjectUndertake> implements ProjectUndertakeService {

    @Inject
    private NotificationService notificationService;

    ProjectServiceImpl projectService = new ProjectServiceImpl();
    FacAgencyServiceImpl faService = new FacAgencyServiceImpl();

    /**
     * 装配完善List对象中所有对象的数据
     *
     * @param list
     * @return
     */
    public List<ProjectUndertake> fitList(List<ProjectUndertake> list) {
        if (list != null) {
            for (ProjectUndertake item : list) {
                fitModel(item);
            }
        }
        return list;
    }

    /**
     * 装配完善Page对象中所有对象的数据
     *
     * @param page
     * @return
     */
    private Page<ProjectUndertake> fitPage(Page<ProjectUndertake> page) {
        if (page != null) {
            List<ProjectUndertake> tList = page.getList();
            for (ProjectUndertake item : tList) {
                fitModel(item);
            }
        }
        return page;
    }

    /**
     * 装配单个实体对象的数据
     *
     * @param model
     * @return
     */
    public ProjectUndertake fitModel(ProjectUndertake model) {
        if (model != null) {
            if (model.getProjectID() > 0) {
                model.setProject(projectService.fitModel(projectService.findById(model.getProjectID())));
            }
            if (model.getFacAgencyID() > 0) {
                if(model.getProjectAssessmentMode().equals("委评")) {
                    model.setFacAgency(faService.findById(model.getFacAgencyID()));
                }
            }
        }
        return model;
    }

    @Override
    public boolean findIsInvite(Long facAgencyID, Long projectID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("facAgencyID", facAgencyID);
        columns.eq("applyOrInvite", true);
        return DAO.findFirstByColumns(columns) != null;
    }

    @Override
    public List<ProjectUndertake> findListByFacAgencyIdAndStatus(Long facAgencyId, String status) {
        Columns columns = Columns.create();
        columns.eq("facAgencyID", facAgencyId);
        columns.eq("status", status);
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public boolean findIsReceive(Long projectID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("status", 2);
        return DAO.findFirstByColumns(columns) != null;
    }

    @Override
    public Page<ProjectUndertake> findPage(ProjectUndertake projectUndertake, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (projectUndertake.getApplyOrInvite() != null) {
            columns.eq("applyOrInvite", projectUndertake.getApplyOrInvite());
        }
        if (projectUndertake.getIsEnable() != null) {
            columns.eq("isEnable", projectUndertake.getIsEnable());
        }
        if (StrKit.notBlank(projectUndertake.getName())) {
            columns.eq("name", projectUndertake.getName());
        }
        if (projectUndertake.getFacAgencyID() != null) {
            columns.eq("facAgencyID", projectUndertake.getFacAgencyID());
        }
        if (projectUndertake.getCreateUserID() != null) {
            columns.eq("createUserID", projectUndertake.getCreateUserID());
            columns.ne("facAgencyID", projectUndertake.getCreateUserID());//排除自评的项目
        }
        if (projectUndertake.getFacAgencyID() != null) {
            columns.eq("facAgencyID", projectUndertake.getFacAgencyID());
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));

    }

    @Override
    public ProjectUndertake findModel(ProjectUndertake projectUndertake) {
        Columns columns = Columns.create();
        if (projectUndertake.getApplyOrInvite() != null) {
            columns.eq("applyOrInvite", projectUndertake.getApplyOrInvite());
        }
        if (projectUndertake.getIsEnable() != null) {
            columns.eq("isEnable", projectUndertake.getIsEnable());
        }
        if (StrKit.notBlank(projectUndertake.getName())) {
            columns.eq("name", projectUndertake.getName());
        }
        if (projectUndertake.getFacAgencyID() != null) {
            columns.eq("facAgencyID", projectUndertake.getFacAgencyID());
        }
        if (projectUndertake.getCreateUserID() != null) {
            columns.eq("createUserID", projectUndertake.getCreateUserID());
        }
        if (projectUndertake.getFacAgencyID() != null) {
            columns.eq("facAgencyID", projectUndertake.getFacAgencyID());
        }
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public ProjectUndertake findByProjectIdAndFacAgencyID(Long id, Long facAgencyID) {
        Columns columns = Columns.create();
        columns.eq("projectID", id);
        columns.eq("facAgencyID", facAgencyID);
        //去除待确认的项目，2018年12月24日，刘英伟
        columns.ne("status",0);
        columns.ne("status",3);
        return fitModel(DAO.findFirstByColumns(columns));
    }

    @Override
    public ProjectUndertake findByProjectIdAndFacAgencyId(Long projectId, Long facAgencyId) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectId);
        columns.eq("facAgencyID", facAgencyId);
        return fitModel(DAO.findFirstByColumns(columns));
    }

    @Override
    public List<ProjectUndertake> findListByFacAgencyIdAndStatusAndAOI(Long facAgencyId, String status, boolean aoi) {
        Columns columns = Columns.create();
        columns.eq("facAgencyID", facAgencyId);
        columns.eq("status", status);
        columns.eq("isEnable", aoi);
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public ProjectUndertake findByProjectIdAndStatus(Long projectId, String projectStatus) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectId);
        columns.eq("status", projectStatus);
        return fitModel(DAO.findFirstByColumns(columns));
    }

    @Override
    public List<ProjectUndertake> findByCreateUserIDAndStatusAndAOI(Long createUserId, String status, boolean aoi) {
        Columns columns = Columns.create();
        columns.eq("createUserID", createUserId);
        columns.eq("status", status);
        columns.eq("applyOrInvite", aoi);
        columns.eq("isEnable", true);
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public ProjectUndertake findByProjectId(Long projectId) {
        return fitModel(DAO.findFirstByColumn(Column.create("projectID", projectId)));
    }

    @Override
    public boolean saveOrUpdateAndSend(ProjectUndertake model, Notification notification) {
        return Db.tx(() -> saveOrUpdate(model) && notificationService.save(notification));
    }

    @Override
    public List<ProjectUndertake> findListByProjectAndStatus(Long projectID, String status) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("status", status);
        return fitList(DAO.findListByColumns(columns));
    }

    @Override
    public float findAllAndUndertakeByUserId(Long userId) {
        Columns columns = Columns.create("createUserID", userId);
        List<ProjectUndertake> list = DAO.findListByColumns(columns);
        float projectAmount = list.size(), undertakeAmount = 0, undertakeRate = 0;
        for (ProjectUndertake projectUndertake : list) {
            if (projectUndertake.getStatus() == 2) {
                undertakeAmount++;
            }
        }
        return projectAmount == 0 ? undertakeRate : undertakeAmount / projectAmount * 100;
    }

    @Override
    public Page<ProjectUndertake> findPageBySql(ProjectUndertake projectUndertake, int pageNumber, int pageSize) {
        Kv c = null;
        SqlPara sqlPara = null;
        Page<ProjectUndertake> page = null;
        // 如果它是组织并且是服务机构
        if (projectUndertake.getFacAgencyID() != null && projectUndertake.getCreateUserID() != null) {
            c = Kv.by("facAgencyID", projectUndertake.getFacAgencyID());
            sqlPara = Db.getSqlPara("app-project.project", c);
            // 直接查询委评项目
            page = DAO.paginate(pageNumber, pageSize, sqlPara);
        }
        // 自评
        c = Kv.by("ID", projectUndertake.getCreateUserID());
        sqlPara = Db.getSqlPara("app-project.project-self", c);
        // 查询自评的
        Page<ProjectUndertake> self = DAO.paginate(pageNumber, pageSize, sqlPara);
        if (self == null) {
            self = new Page<>();
        }

        // 如果委评为空
        if (page != null && page.getList().size() != 0) {
            for (ProjectUndertake undertake : page.getList()) {
                self.getList().add(undertake);
            }
        }
        return fitPage(self);
    }

    @Override
    public Page<ProjectUndertake> findPageOfApplyIn(Long buildProjectUserID, int pageNumber, int pageSize) {
        Kv c = null;
        SqlPara sqlPara = null;
        c = Kv.by("buildProjectUserID", buildProjectUserID);
        sqlPara = Db.getSqlPara("app-project.project-undertake-ApplyIn", c);
        return fitPage(DAO.paginate(pageNumber, pageSize, sqlPara));
    }
}