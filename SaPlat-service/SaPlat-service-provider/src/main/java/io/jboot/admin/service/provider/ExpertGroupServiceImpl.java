package io.jboot.admin.service.provider;


import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ExpertGroupServiceImpl extends JbootServiceBase<ExpertGroup> implements ExpertGroupService{

    @Inject
    private AuthService authService;

    @Inject
    private FilesService filesService;

    @Inject
    private UserRoleService userRoleService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PersonService personService;

    @Inject
    private NotificationService notificationService;
    UserServiceImpl userServiceNew = new UserServiceImpl();
    /**
     * 装配完善Page对象中所有对象的数据
     * @param page
     * @return
     */
    public Page<ExpertGroup> fitPage(Page<ExpertGroup> page){
        if(page != null){
            List<ExpertGroup> tList = page.getList();
            for (ExpertGroup item: tList) {
                fitModel(item);
            }
        }
        return page;
    }

    /**
     * 装配单个实体对象的数据
     * @param model
     * @return
     */
    public ExpertGroup fitModel(ExpertGroup model) {
        if (model != null) {
            User user = new User();
            user.setUserID(model.getPersonID());
            user.setUserSource(0);// 0 代表个人

            model.setUser(userServiceNew.findModel(user));
        }
        return model;
    }

    @Override
    public Page<ExpertGroup> findPage(ExpertGroup model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (model.getIsEnable() != null) {
            columns.eq("isEnable", model.getIsEnable());
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public Page<ExpertGroup> findPage(ExpertGroup model, Date[] dates, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (StrKit.notNull(model.getIsEnable())) {
            columns.eq("isEnable", model.getIsEnable());
        }
        if (StrKit.notNull(dates[0])){
            columns.ge("createTime", dates[0]);
        }
        if (StrKit.notNull(dates[1])){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dates[1]);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dates[1] = calendar.getTime();
            columns.le("createTime", dates[1]);
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public Page<ExpertGroup> findPage(int pageNumber, int pageSize) {
        return fitPage(DAO.paginate(pageNumber, pageSize, "id desc"));
    }

    @Override
    public boolean hasExpertGroup(String name) {
        return findByName(name) != null;
    }

    /**
     * find all model by orgID
     *
     * @return all <ExpertGroup
     */
    @Override
    public List<Record> findAllByOrgId(Long orgId){
        Kv para = Kv.by("orgID",orgId);
        SqlPara sqlPara = Db.getSqlPara("app-OrgStruct.ExportGroupsByOrgId",para);
        return Db.find(sqlPara);
    }

    @Override
    public ExpertGroup findByPersonId(Long id) {
        Columns columns = Columns.create();
        columns.eq("personID", id);
        columns.eq("isEnable",1);
        return fitModel(DAO.findFirstByColumns(columns));
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
    public boolean useOrunuse(ExpertGroup expertGroup){
        return Db.tx(() -> {
            System.out.println(expertGroup);
            Person person=personService.findById(expertGroup.getPersonID());
            User user = userService.findByUserIdAndUserSource(person.getId(), 0);
            Role role = roleService.findByName("专家团体");
            UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), role.getId());
            if(userRole==null){
                return false;
            }
            userRole.setIsEnable(expertGroup.getIsEnable());
            return userRoleService.update(userRole) && expertGroup.update();

        });
    }
    @Override
    public boolean saveOrUpdate(ExpertGroup model, Auth auth, List<Files> files, Notification noti) {
        return Db.tx(() -> {
            if (!saveOrUpdate(model) || !authService.saveOrUpdate(auth) || !notificationService.saveOrUpdate(noti)) {
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
        return fitModel(DAO.findFirstByColumn("name", name));
    }

    @Override
    public ExpertGroup findByOrgId(Long orgId) {
        return fitModel(DAO.findFirstByColumn("orgID", orgId));
    }
}