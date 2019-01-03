package io.jboot.admin.service.entity.status.system;


import io.jboot.admin.base.common.BaseStatus;

/**
 * 系统资源状态类
 * @author Rlax
 *
 */
public class RiskExpertise extends BaseStatus {

    public final static String VERYLOW = "1";
    public final static String LOW = "2";
    public final static String MEDIUM = "3";
    public final static String HEIGHT = "4";
    public final static String VERYHEIGHT = "5";

    public RiskExpertise() {
        add(VERYLOW, "很低");
        add(LOW, "较低");
        add(MEDIUM, "中等");
        add(HEIGHT, "较高");
        add(VERYHEIGHT, "很高");
    }

    private static RiskExpertise me;

    public static RiskExpertise me() {
        if (me == null) {
            me = new RiskExpertise();
        }
        return me;
    }

}
