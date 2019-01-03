package io.jboot.admin.service.entity.status.system;


import io.jboot.admin.base.common.BaseStatus;

/**
 * 系统资源状态类
 * @author Rlax
 *
 */
public class IncidenceExpertise extends BaseStatus {

    public final static String NEGLIGIBLE = "1";
    public final static String LESSER = "2";
    public final static String MEDIUM = "3";
    public final static String LARGER = "4";
    public final static String SEVERITY = "5";

    public IncidenceExpertise() {
        add(NEGLIGIBLE, "可忽略");
        add(LESSER, "较小");
        add(MEDIUM, "中等");
        add(LARGER, "较大");
        add(SEVERITY, "严重");
    }

    private static IncidenceExpertise me;

    public static IncidenceExpertise me() {
        if (me == null) {
            me = new IncidenceExpertise();
        }
        return me;
    }

}
