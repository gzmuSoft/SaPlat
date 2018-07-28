package io.jboot.admin.validator.app;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * 用户管理表单校验
 * @author Rlax
 *
 */
public class UserValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        if(!c.getPara("user.phone").equals("")){
            validateMobile("user.phone","电话号码格式不正确");
        }
        if(!c.getPara("user.email").equals("")) {
            validateEmail("user.email", "邮箱格式不正确");
        }
    }
}