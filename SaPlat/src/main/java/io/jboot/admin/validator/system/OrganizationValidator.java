package io.jboot.admin.validator.system;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import io.jboot.admin.base.web.base.JsonValidator;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;

/**
 * 修改组织校验器
 *
 */
public class OrganizationValidator extends JsonValidator {

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

        validateString("organization.name", 1, 50, "组织机构名称的长度必须是介于1至50个字符之间");
        if( StrKit.notBlank(c.getPara("organization.code")) ) {
            validateOgCode("organization.code", "机构代码格式不正确，正确的组织机构代码格式为十八位的阿拉伯数字或大写英文字母");
        }
        validateString("organization.addr", 3, 128, "地址的长度必须是介于3至128个字符之间");
        validateString("organization.principal", 1, 50, "委办负责人的姓名不能为空且长度必须是介于1至50个字符之间");
        validateMobile("organization.contact", "手机号码格式不正确");
        validateEmail("user.email", "邮箱格式不正确");
        validateString("user.email", 6, 60, "邮箱长度请在6-60位");
    }
}
