package io.jboot.admin.controller.app;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.NotificationService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.Notification;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * Created by fengx on 2018/7/19.
 */
@RequestMapping("/app/notification")
public class NotificationController extends BaseController {
    @JbootrpcService
    private NotificationService notificationService;

    @JbootrpcService
    private UserService userService;

    /**
     * index
     */
    public void index() {
        render("main.html");
    }

    /**
     * notification表格数据
     */
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        User loginUser = AuthUtils.getLoginUser();
        Notification notification = new Notification();
        notification.setName(getPara("name"));
        notification.setContent(getPara("content"));
        notification.setReceiverID((loginUser.getId().intValue()));
        notification.setStatus(getParaToInt("status"));
        notification.setIsEnable(true);

        Date[] dates = new Date[2];
        try {
            dates[0] = getParaToDate("startDate");
            dates[1] = getParaToDate("endDate");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Page<Notification> page = notificationService.findPage(notification, dates, pageNumber, pageSize);
        renderJson(new DataTable<Notification>(page));
    }

    /**
     * 查看
     */
    @NotNullPara({"id"})
    public void view() {
        Notification notification = notificationService.findById(getParaToLong("id"));
        User createUser = userService.findById(notification.getCreateUserID());
        User receiverUser = userService.findById(notification.getReceiverID());
        setAttr("notification", notification).
                setAttr("createUser", createUser).setAttr("receiverUser", receiverUser).
                render("view.html");
        notification.setLastAccessTime(new Date());
        notification.setStatus(1);
        if (!notificationService.update(notification)) {
            throw new BusinessException("异常");
        }
    }


    /**
     * 删除
     */
    @NotNullPara({"id"})
    public void delete() {
        Long id = getParaToLong("id");
        Notification model = notificationService.findById(id);
        model.setIsEnable(false);
        if (!notificationService.update(model)) {
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void haveRead() {
        User loginUser = AuthUtils.getLoginUser();
        if (!notificationService.haveReadAll(loginUser.getId())) {
            throw new BusinessException("标记失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}