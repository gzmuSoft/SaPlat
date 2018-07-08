package io.jboot.admin.validator.app;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-07.
 * 组织机构注册表单校验
 * @author Rlax
 *
 */
public class OrganizationValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("organization.name",4,16,"组织名请在4-16位");
        validateString("organization.principal",2,7,"委办负责人姓名请在2-7位");
        validateRequiredString("organization.addr","请输入组织机构地址");
        validateRequiredString("organization.code","请输入组织机构代码");
        if (!c.getPara("user.pwd").equals(c.getPara("rePwd"))){
            addError("两次密码输入不一致");
        }
        validateMobile("organization.contact","电话号码格式不正确");
        validateEmail("user.email","邮箱格式不正确");
    }

}
