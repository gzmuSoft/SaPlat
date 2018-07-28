package io.jboot.admin.service.entity.status.system;

import io.jboot.admin.base.common.BaseStatus;

public class PublicStatus  extends BaseStatus {
    /**
     * 取消认证
     */
    public final static String PUB = "0";
    /**
     * 未认证
     */
    public final static String ROLE = "1";
    /**
     * 已认证
     */
    public final static String USER = "2";

    public PublicStatus() {
        add(PUB, "公开");
        add(ROLE, "角色");
        add(USER, "用户");
    }

    private static PublicStatus ps;

    public static PublicStatus ps() {
        if (ps == null) {
            ps = new PublicStatus();
        }
        return ps;
    }
}
