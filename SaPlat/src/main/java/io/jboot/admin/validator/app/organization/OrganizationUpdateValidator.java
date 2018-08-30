package io.jboot.admin.validator.app.organization;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-07.
 * 组织机构注册表单校验
 *
 * @author Rlax
 */
public class OrganizationUpdateValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateString("user.name", 5, 25, "登录账号请在5-25位");
        validateString("organization.name", 4, 50, "组织名请在4-50位");
        validateString("organization.principal", 2, 7, "委办负责人姓名请在2-7位");
        validateRequiredString("organization.addr", "请输入组织机构地址");
        validateString("organization.code", 18, 18, "请输入18位组织机构代码");
        validateMobile("organization.contact", "电话号码格式不正确");
        validateEmail("user.email", "邮箱格式不正确");
    }


}
