package io.jboot.admin.service.entity.status.system;

import io.jboot.admin.base.common.BaseStatus;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 *          -----------------------------
 * @date 18:04 2018/7/9
 */
public class TypeStatus extends BaseStatus {

    /**
     * 个人
     */
    public final static String PERSON = "0";
    /**
     * 组织
     */
    public final static String ORGANIZATION = "1";
    /**
     * 立项资格
     */
    public final static String PROJECT_VERIFY = "2";


    public TypeStatus() {
        add(PERSON, "个人审核");
        add(ORGANIZATION, "团体审核");
        add(PROJECT_VERIFY, "立项资格审核");
    }

    private static TypeStatus me;

    public static TypeStatus me() {
        if (me == null) {
            me = new TypeStatus();
        }
        return me;
    }

}
