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
public class TypeStatus extends BaseStatus {

    /**
     * 个人
     */
    public final static String PERSON="0";
    /**
     * 团体
     */
    public final static String ORGANIZATION = "1";
    /**
     * 立项资格
     */
    public final static String PROJECT_VERIFY = "2";
    /**
     * 立项资料
     */
    public final static String PROJECT_DATA = "3";


    public TypeStatus() {
        add(PERSON,"个人审核");
        add(ORGANIZATION, "团体审核");
        add(PROJECT_VERIFY, "立项资格审核");
        add(PROJECT_DATA, "立项资料审核");
    }

    private static TypeStatus me;

    public static TypeStatus me() {
        if (me == null) {
            me = new TypeStatus();
        }
        return me;
    }

}
