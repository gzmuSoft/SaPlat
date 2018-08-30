package io.jboot.admin.validator;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * 登录校验
 *
 * @author Rlax
 */
public class LoginValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("loginName", 5, 25, "用户名格式不正确，请在5-25位");
        validateString("password", 6, 16, "密码格式不正确，请在6-20位");
        validateString("capval", 4, 4, "验证码格式不正确");
        validateCaptcha("capval", "验证码不正确");
    }
}
