package io.jboot.admin.service.entity.status.system;

import io.jboot.admin.base.common.BaseStatus;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 18:04 2018/7/9
 */
public class AuthStatus extends BaseStatus {
    /**
     * 取消认证
     */
    public final static String CANCEL_VERIFY = "0";
    /**
     * 未认证
     */
    public final static String NOT_VERIFY = "1";
    /**
     * 已认证
     */
    public final static String IS_VERIFY = "2";

    /**
     * 认证中
     */
    public final static String VERIFYING = "3";


    public AuthStatus() {
        add(CANCEL_VERIFY,"取消审核");
        add(NOT_VERIFY, "审核失败");
        add(IS_VERIFY, "审核成功");
        add(VERIFYING, "审核中");
    }

    private static AuthStatus me;

    public static AuthStatus me() {
        if (me == null) {
            me = new AuthStatus();
        }
        return me;
    }

}