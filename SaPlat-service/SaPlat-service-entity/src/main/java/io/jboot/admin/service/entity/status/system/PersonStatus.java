package io.jboot.admin.service.entity.status.system;


import io.jboot.admin.base.common.BaseStatus;

/**
 * 系统资源状态类
 * @author Rlax
 *
 */
public class PersonStatus extends BaseStatus {

    public final static String UNUSED = "0";
    public final static String USED = "1";

    public PersonStatus() {
        add(UNUSED, "停用");
        add(USED, "启用");
    }

    private static PersonStatus me;

    public static PersonStatus me() {
        if (me == null) {
            me = new PersonStatus();
        }
        return me;
    }

}
