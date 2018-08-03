package io.jboot.admin.validator.app;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-07.
 * 新闻填写合法性校验
 *
 * @author Rlax
 */
public class NewsValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        if (!c.getPara("model.title").equals("")) {
            validateString("model.title", 1, 50, "名称的长度必须是介于1至50个字符之间");
        }
        if (!c.getPara("model.content").equals("")) {
            validateString("model.content", 1, 2048, "名称的长度必须是介于1至2048个字符之间");
        }
    }
}
