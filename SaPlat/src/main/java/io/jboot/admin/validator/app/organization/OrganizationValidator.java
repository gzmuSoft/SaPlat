package io.jboot.admin.validator.app.organization;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-07.
 * 组织机构注册表单校验
 *
 * @author Rlax
 */
public class OrganizationValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("user.name", 4, 16, "登录账号请在4-16位");
        validateString("organization.name", 4, 16, "组织名请在4-16位");
        validateString("user.pwd", 6, 16, "密码请在6-16位");
        if (!c.getPara("user.pwd").equals(c.getPara("rePwd"))) {
            addError("两次密码输入不一致");
        }
        validateString("organization.principal", 2, 7, "委办负责人姓名请在2-7位");
        validateRequiredString("organization.addr", "请输入组织机构地址");
        //注册时可不输入组织机构代码，认证时必须输入，刘英伟，2018.08.27
        //validateString("organization.code", 18, 18, "请输入18位组织机构代码");
        validateMobile("organization.contact", "电话号码格式不正确");
        validateEmail("user.email", "邮箱格式不正确");
    }


}
