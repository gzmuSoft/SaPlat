package io.jboot.admin.service.entity.status.system;


import io.jboot.admin.base.common.BaseStatus;

/**
 * 系统资源状态类
 * @author Rlax
 *
 */
public class NewsType extends BaseStatus {

    public final static String POLICIC = "0";
    public final static String TRACKING = "1";
    public final static String POLICY = "2";

    public NewsType() {
        add(POLICIC, "政要文件");
        add(TRACKING, "跟踪动态");
        add(POLICY, "最新政策");
    }

    private static NewsType me;

    public static NewsType me() {
        if (me == null) {
            me = new NewsType();
        }
        return me;
    }

}
