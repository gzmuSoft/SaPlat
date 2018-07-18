package io.jboot.admin.service.entity.status.system;

import io.jboot.admin.base.common.BaseStatus;

/**
 * -----------------------------
 *
 * @author LiuChuanjin
 * @version 2.0
 *          -----------------------------
 * @date 12:04 2018/7/15
 */
public class ProjectTypeStatus extends BaseStatus {

    /**
     * 资料审核
     */
    public final static String INFORMATION_REVIEW = "0";
    /**
     * 项目评估
     */
    public final static String PROJECT_EVALUATION = "1";


    public ProjectTypeStatus() {
        add(INFORMATION_REVIEW, "资料审核");
        add(PROJECT_EVALUATION, "项目评估");
    }

    private static ProjectTypeStatus me;

    public static ProjectTypeStatus me() {
        if (me == null) {
            me = new ProjectTypeStatus();
        }
        return me;
    }
}

