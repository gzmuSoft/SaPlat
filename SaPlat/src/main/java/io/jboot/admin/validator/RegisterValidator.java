package io.jboot.admin.validator;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * 登录校验
 * @author Rlax
 *
 */
public class RegisterValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("user.name", 4, 16, "用户名格式不正确");
        validateString("user.pwd", 6, 16, "密码格式不正确");
        validateEmail("user.email","邮箱格式不正确");
        validateMobile("user.phone","联系电话格式不正确");
    }
}
