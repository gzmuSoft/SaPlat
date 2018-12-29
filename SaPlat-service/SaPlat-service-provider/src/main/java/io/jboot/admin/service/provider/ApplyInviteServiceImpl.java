package io.jboot.admin.service.provider;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.api.SaPlatService;
import io.jboot.admin.service.api.StructPersonLinkService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.StructPersonLink;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.ApplyInviteService;
import io.jboot.admin.service.entity.model.ApplyInvite;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ApplyInviteServiceImpl extends JbootServiceBase<ApplyInvite> implements ApplyInviteService {

    @Inject
    private NotificationService notificationService;

    @Inject
    private StructPersonLinkService structPersonLinkService;

    /**
     * 装配单个实体对象的数据
     * @param model
     * @return
     */
    public ApplyInvite fitModel(ApplyInvite model){
        Date now = new Date();
        if(model.getDeadTime().before(now))
            model.setStatus("7");
        return model;
    }
    /**
     * 装配完善 list 对象中所有对象的数据
     * @param list
     * @return
     */
    public List<ApplyInvite> fitList(List<ApplyInvite> list){
        if(list != null){
            for (ApplyInvite item: list) {
                fitModel(item);
            }
        }
        return list;
    }

    /**
     * 装配完善 Page 对象中所有对象的数据
     * @param page
     * @return
     */
    public Page<ApplyInvite> fitPage(Page<ApplyInvite> page){
        if(page != null){
            List<ApplyInvite> tList = page.getList();
            for (ApplyInvite item: tList) {
                fitModel(item);
            }
        }
        return page;
    }

    @Override
    public Page<ApplyInvite> findListByUserIdOrUserName(int pageNumber, int pageSize, ApplyInvite applyInvite){
        Columns columns = Columns.create();
        columns.eq("userID",applyInvite.getUserID());
        columns.eq("module",0);
        columns.eq("applyOrInvite",1);
        if(StrKit.notBlank(applyInvite.getName())){
            columns.like("name","%" + applyInvite.getName() + "%");
        }
        return fitPage(DAO.paginateByColumns(pageNumber,pageSize,columns.getList(),"createTime desc"));
    }
    @Override
    public Page<ApplyInvite> findApplyByUserIdOrName(int pageNumber,int pageSize,ApplyInvite applyInvite){
        Columns columns = Columns.create();
        columns.eq("belongToID",applyInvite.getBelongToID());
        columns.eq("module",0);
        columns.eq("applyOrInvite",0);
        if(applyInvite.getUserID() != null){
            columns.eq("userID",applyInvite.getUserID());
        }
        if(StrKit.notBlank(applyInvite.getName())){
            columns.like("name","%" + applyInvite.getName() + "%");
        }
        return fitPage(DAO.paginateByColumns(pageNumber,pageSize,columns.getList(),"createTime desc"));
    }

    @Override
    public boolean findIsInvite(Long expertGroupId, Long projectID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("userID", expertGroupId);
        columns.eq("module", 2);
        if (DAO.findFirstByColumns(columns) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<ApplyInvite> findPage(ApplyInvite model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (model.getApplyOrInvite() != null) {
            columns.eq("applyOrInvite", model.getApplyOrInvite());
        }
        if (model.getIsEnable() != null) {
            columns.eq("isEnable", model.getIsEnable());
        }
        if (model.getStatus() != null) {
            columns.eq("status", model.getStatus());
        }
        if (model.getCreateUserID() != null) {
            columns.eq("createUserID", model.getCreateUserID());
        }
        if (model.getModule() != null) {
            columns.eq("module", model.getModule());
        }
        if (model.getUserID() != null) {
            columns.eq("userID", model.getUserID());
        }
        if (model.getRemark() != null) {
            columns.eq("remark", model.getRemark());
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public ApplyInvite findByProjectID(Long projectID) {
        return DAO.findFirstByColumn("projectID", projectID);
    }

    @Override
    public List<ApplyInvite> findList(ApplyInvite model) {
        Columns columns = Columns.create();
        if (model.getProjectID() != null) {
            columns.eq("projectID", model.getProjectID());
        }
        if (model.getUserID() != null) {
            columns.eq("userID", model.getUserID());
        }
        if (model.getModule() != null) {
            columns.eq("module", model.getModule());
        }
        if (model.getIsEnable() != null) {
            columns.eq("isEnable", model.getIsEnable());
        }
        if (model.getStatus() != null) {
            columns.eq("status", model.getStatus());
        }
        if (model.getCreateUserID() != null) {
            columns.eq("createUserID", model.getCreateUserID());
        }
        return fitList(DAO.findListByColumns(columns));
    }

    /**
     * find list
     *
     * @param projectID
     * @return
     */
    @Override
    public List<ApplyInvite> findLastTimeListByProjectID(Long projectID){
        Kv c;
        SqlPara sqlPara = null;
        c = Kv.by("ID", projectID);
        sqlPara = Db.getSqlPara("app-project.lastInvited-by-projectID", c);
        return fitList(DAO.paginate(1, Integer.MAX_VALUE, sqlPara).getList());
    }

    @Override
    public boolean saveAndUpdateAndSend(ApplyInvite applyInvite, Notification notification, StructPersonLink structPersonLink){
        return Db.tx(()-> applyInvite.update() && notificationService.save(notification) && structPersonLinkService.save(structPersonLink));
    }
    @Override
    public boolean saveOrUpdateAndSend(ApplyInvite applyInvite,Notification notification){
        return Db.tx(()->applyInvite.saveOrUpdate() && notificationService.save(notification));
    }

    @Override
    public ApplyInvite findByStructIDAndUserID(Long structID, Long UserID){
        return DAO.findFirst("SELECT * FROM `apply_invite` where deadTime >= now() and userID=? and structID=? and module = 0 and status = 0 ORDER BY deadTime DESC limit 1", UserID, structID);
    }

    @Override
    public ApplyInvite findDeadTimeByProjectID(Long projectID){
        return DAO.findFirst("SELECT * FROM `apply_invite` where projectID=? AND module=1 AND `status`>=2 ORDER BY deadTime DESC limit 1", projectID);
    }

    @Override
    public ApplyInvite findFirstByModel(ApplyInvite model) {
        Columns columns = Columns.create();
        if (model.getStatus() != null) {
            columns.eq("status", model.getStatus());
        }
        if (model.getProjectID() != null) {
            columns.eq("projectID", model.getProjectID());
        }
        if (model.getUserID() != null) {
            columns.eq("userID", model.getUserID());
        }
        if (model.getModule() != null) {
            columns.eq("module", model.getModule());
        }
        if (model.getIsEnable() != null) {
            columns.eq("isEnable", model.getIsEnable());
        }
        if (model.getUserSource() != null) {
            columns.eq("userSource", model.getUserSource());
        }
        return DAO.findFirstByColumns(columns);
    }
}