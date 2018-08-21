package io.jboot.admin.validator.app.person;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * 个人群体注册表单校验
 *
 * @author Rlax
 */
public class PersonRegisterValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("user.name", 4, 16, "账号请在4-16位");
        validateString("person.name", 2, 16, "姓名请在2位以上");
        validateString("user.pwd", 6, 16, "密码长度请在6-16位");
        validateString("person.sex",1,1,"请选择性别");
        if (!c.getPara("user.pwd").equals(c.getPara("rePwd"))) {
            addError("两次密码输入不一致");
        }
        validateMobile("person.phone", "电话号码格式不正确");
        validateEmail("user.email", "邮箱格式不正确");
    }
}
