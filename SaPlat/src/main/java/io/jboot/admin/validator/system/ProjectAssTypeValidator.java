package io.jboot.admin.validator.system;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-07.
 * 国家信息填写合法性校验
 *
 * @author Rlax
 */
public class ProjectAssTypeValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("model.name", 1, 50, "名称的长度必须是介于1至50个字符之间");
        validateInteger("model.sort", 1, Integer.MAX_VALUE, String.format("排序必须是介于1至%d之间的整数", Integer.MAX_VALUE));
    }
}
