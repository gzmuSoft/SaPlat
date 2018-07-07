package io.jboot.admin.validator;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * 个人群体注册表单校验
 * @author Rlax
 *
 */
public class PersonRegisterValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("person.name",1,16,"账号请在1-16位");
        System.out.println(c.getPara("user.pwd"));
        System.out.println(c.getPara("rePwd"));
        System.out.println(c.getPara("user.pwd").equals(c.getPara("rePwd")));
        if (!c.getPara("user.pwd").equals(c.getPara("rePwd"))){
            addError("两次密码输入不一致");
        }
        if (Integer.parseInt(c.getPara("person.age")) <= 0){
            addError("年龄不能小于等于0");
        }
        validateMobile("person.phone","电话号码格式不正确");
        validateEmail("user.email","邮箱格式不正确");
    }
}
