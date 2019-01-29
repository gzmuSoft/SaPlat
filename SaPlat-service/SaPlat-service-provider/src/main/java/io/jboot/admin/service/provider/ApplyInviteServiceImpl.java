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
import io.jboot.admin.service.entity.model.ProAssReview;
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

    private ProAssReviewServiceImpl proAssReviewServiceService = new ProAssReviewServiceImpl();
    /**
     * 装配单个实体对象的数据
     * @param model
     * @return
     */
    public ApplyInvite fitModel(ApplyInvite model){
        Date now = new Date();
        if(model.getDeadTime().before(now)){
            if(model.getModule() == 2) {
                if (model.getStatus().equals("5"))
                    model.setStatus("7");
                else if (model.getStatus().equals("0"))
                    model.setStatus("8");
            }else if(model.getModule() == 1){
                if(!(model.getRemark()!=null && model.getRemark().equals("审查完成"))) {
                    model.setRemark("超时未审查，自动通过审查");
                    model.setReply("超时未审查，自动通过审查");
                    model.setLastAccessTime(null);
                }else{//未超时，则取得最后的审查意见
                    List<ProAssReview> parList = proAssReviewServiceService.findListByProjectIDAndCreateUserID(model.getProjectID(), model.getUserID());
                    if(parList != null && parList.size() > 0)
                        model.setReply(parList.get(0).getRecomment());
                }
            }
        }
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
        if (model.getBelongToID() != null) {
            columns.eq("belongToID", model.getBelongToID());
        }
        if(StrKit.notBlank(model.getSpell()) && model.getSpell().equals("1")){
            columns.gt("deadTime", new Date());
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public ApplyInvite findByProjectID(Long projectID) {
        return DAO.findFirstByColumn("projectID", projectID);
    }

    @Override
    public Long findCount(Long projectID, Long belongToID, Integer module){
        return Db.queryLong("SELECT COUNT(1) FROM apply_invite\n" +
                "WHERE projectID = ?\n" +
                "  AND belongToID = ?\n" +
                "  AND module = ?", projectID, belongToID, module);
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
        if (model.getBelongToID() != null) {
            columns.eq("belongToID", model.getBelongToID());
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
    public List<ApplyInvite> findLastTimeListByProjectID(Long projectID, Long belongToID){
        Kv c;
        SqlPara sqlPara = null;
        c = Kv.by("ID", projectID);

        if (belongToID != null && belongToID > 0L) {
            c.set("belongToID", belongToID);
        }
        sqlPara = Db.getSqlPara("app-project.lastInvited-by-projectID", c);
        return fitList(DAO.paginate(1, Integer.MAX_VALUE, sqlPara).getList());
    }

    @Override
    public Page<ApplyInvite> findReviewedHistoryPage(Long userID, int pageNumber, int pageSize){
        Kv c = Kv.by("userID", userID);
        SqlPara sqlPara = Db.getSqlPara("app-project.reviewed-history", c);
        return fitPage(DAO.paginate(pageNumber, pageSize, sqlPara));
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
        if (model.getBelongToID() != null) {
            columns.eq("belongToID", model.getBelongToID());
        }
        return DAO.findFirstByColumns(columns);
    }
}