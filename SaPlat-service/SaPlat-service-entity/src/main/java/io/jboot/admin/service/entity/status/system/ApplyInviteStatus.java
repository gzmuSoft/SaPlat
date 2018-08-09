package io.jboot.admin.service.entity.status.system;

import io.jboot.admin.base.common.BaseStatus;

/**
 * -----------------------------
 *
 * @author LiuChuanjin
 * @version 2.0
 *          -----------------------------
 * @date 12:04 2018/8/9
 */
public class ApplyInviteStatus extends BaseStatus {
    /**
     * 待同意
     */
    public final static String WAITE = "0";
    /**
     * 已拒绝
     */
    public final static String REFUSE = "1";
    /**
     * 已同意
     */
    public final static String AGREE = "2";
    /**
     * 通过
     */
    public final static String PASS = "3";
    /**
     * 不通过
     */
    public final static String NOPASS = "4";

    public ApplyInviteStatus() {
        add(WAITE, "待同意");
        add(AGREE, "已同意");
        add(REFUSE, "已拒绝");
        add(PASS, "通过");
        add(NOPASS, "不通过");
    }

    private static ApplyInviteStatus me;

    public static ApplyInviteStatus me() {
        if (me == null) {
            me = new ApplyInviteStatus();
        }
        return me;
    }
}
