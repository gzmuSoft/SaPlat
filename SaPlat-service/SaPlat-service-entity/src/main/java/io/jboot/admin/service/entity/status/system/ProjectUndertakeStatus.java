package io.jboot.admin.service.entity.status.system;

import io.jboot.admin.base.common.BaseStatus;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 22:41 2018/7/19
 */
public class ProjectUndertakeStatus extends BaseStatus{
    /**
     * 待确认
     */
    public final static String WAITING = "0";
    /**
     * 拒绝
     */
    public final static String REFUSE = "1";
    /**
     * 同意
     */
    public final static String ACCEPT = "2";
    /**
     * 已被他人承接
     */
    public final static String UNDERTAKE = "3";

    public ProjectUndertakeStatus(){
        add(WAITING, "待确认");
        add(REFUSE, "已拒绝");
        add(ACCEPT, "已同意");
        add(UNDERTAKE, "已被他人承接");
    }
    private static ProjectUndertakeStatus me;

    public static ProjectUndertakeStatus me() {
        if (me == null) {
            me = new ProjectUndertakeStatus();
        }
        return me;
    }
}
