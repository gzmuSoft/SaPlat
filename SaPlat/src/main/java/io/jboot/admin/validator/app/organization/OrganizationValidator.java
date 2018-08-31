package io.jboot.admin.validator.app.organization;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
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
        validateString("user.name", 5, 25, "账号名称请在5-25位");
        validateString("organization.name", 4, 50, "组织机构名称请在4-50位");
        validateString("user.pwd", 6, 16, "密码请在6-20位");
        if (!c.getPara("user.pwd").equals(c.getPara("rePwd"))) {
            addError("两次密码输入不一致");
        }
        validateString("organization.principal", 2, 20, "委办负责人姓名请在2-20位");
        validateRequiredString("organization.addr", "请输入组织机构地址");
        //注册时可不输入组织机构代码，认证时必须输入，刘英伟，2018.08.27
        //validateString("organization.code", 18, 18, "请输入18位组织机构代码");
        validateMobile("organization.contact", "电话号码格式不正确");
        validateEmail("user.email", "邮箱格式不正确");
        if(StrKit.notBlank(c.getPara("organization.code"))) {
            validateString("organization.code", 18, 18, "请输入18位组织机构代码");
        }
    }
}
