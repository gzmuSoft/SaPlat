package io.jboot.admin.validator.app;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-29.
 * 立项资料填写合法性校验
 *
 * @author Ry
 */
public class ProjectValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        if (!c.getPara("project.name").equals("")) {
            validateString("project.name", 1, 254, "名称的长度必须是介于1至254个字符之间");
        }
        if (!c.getPara("project.site").equals("")) {
            validateString("project.site", 1, 256, "名称的长度必须是介于1至256个字符之间");
        }
        if (!c.getPara("leaderGroup.leader").equals("")) {
            validateString("leaderGroup.leader", 1, 64, "名称的长度必须是介于1至64个字符之间");
        }
        if (!c.getPara("leaderGroup.deputy").equals("")) {
            validateString("leaderGroup.deputy", 1, 64, "名称的长度必须是介于1至64个字符之间");
        }
        if (!c.getPara("leaderGroup.crew").equals("")) {
            validateString("leaderGroup.crew", 1, 1024, "名称的长度必须是介于1至1024个字符之间");
        }
    }
}
