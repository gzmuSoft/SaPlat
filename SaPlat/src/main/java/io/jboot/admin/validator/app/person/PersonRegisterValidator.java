package io.jboot.admin.validator.app.person;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * 个人群体注册表单校验
 *
 * @author Rlax
 */
public class PersonRegisterValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("user.name", 5, 25, "账号名称请在5-25位");
        if(StrKit.notBlank(c.getPara("user.name")) && c.getPara("user.name").equals(c.getPara("user.pwd"))){
            addError("密码不能和账号名称相同，请重新输入");
        }
        validatePassWord("user.pwd", "只能包含字母、数字以及标点符号（除空格），字母、数字和标点符号至少包含2种，密码长度为6-20位");
        if (!c.getPara("user.pwd").equals(c.getPara("rePwd"))) {
            addError("两次输入的密码不一致，请重新输入");
        }
        validateString("person.name", 2, 20, "姓名请在2-20位");
        validateString("person.sex",1,1,"请选择性别");
        validateMobile("person.phone", "手机格式不正确");
        validateEmail("user.email", "邮箱格式不正确");
        validateString("user.email", 6, 60, "邮箱长度请在6-60位");
    }
}
