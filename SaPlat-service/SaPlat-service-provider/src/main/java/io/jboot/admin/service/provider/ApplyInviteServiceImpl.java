package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.NotificationService;
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
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class ApplyInviteServiceImpl extends JbootServiceBase<ApplyInvite> implements ApplyInviteService {

    @Inject
    private NotificationService notificationService;

    @Inject
    private StructPersonLinkService structPersonLinkService;
    @Override
    public Page<ApplyInvite> findListByUserIdOrUserName(int pageNumber, int pageSize, ApplyInvite applyInvite){
        Columns columns = Columns.create();
        columns.eq("userID",applyInvite.getUserID());
        columns.eq("module",0);
        columns.eq("applyOrInvite",1);
        if(StrKit.notBlank(applyInvite.getName())){
            columns.like("name","%" + applyInvite.getName() + "%");
        }
        return DAO.paginateByColumns(pageNumber,pageSize,columns.getList(),"createTime desc");
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
        return DAO.paginateByColumns(pageNumber,pageSize,columns.getList(),"createTime desc");
    }

    @Override
    public boolean findIsInvite(Long expertGroupId, Long projectID) {
        Columns columns = Columns.create();
        columns.eq("projectID", projectID);
        columns.eq("userID", expertGroupId);
        columns.eq("module", 1);
        if (DAO.findFirstByColumns(columns) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<ApplyInvite> findPage(ApplyInvite model, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public List<ApplyInvite> findList(ApplyInvite model) {
        return null;
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
        return DAO.findFirst("SELECT * FROM `apply_Invite` where deadTime >= now() and userID=? and structID=? and module = 0 and status = 0 ORDER BY deadTime DESC limit 1", UserID, structID);
    }
}