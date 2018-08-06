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
    public boolean saveAndUpdateAndSend(ApplyInvite applyInvite, Notification notification, StructPersonLink structPersonLink){
        return Db.tx(()-> applyInvite.update() && notificationService.save(notification) && structPersonLinkService.save(structPersonLink));
    }
    @Override
    public boolean saveOrUpdateAndSend(ApplyInvite applyInvite,Notification notification){
        return Db.tx(()->applyInvite.saveOrUpdate() && notificationService.save(notification));
    }
}