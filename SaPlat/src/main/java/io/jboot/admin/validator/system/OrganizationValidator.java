package io.jboot.admin.validator.system;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;


   /*     * 修改组织校验器
        * @author
        *
        */
public class OrganizationValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateMobile("organization.contact","电话号码格式不正确");
        validateOgCode("organization.code","机构代码格式不正确，8位数字-1位数字，共计10位");
        if(!c.getPara("organization.name").equals("")){
            validateString("organization.name",1,50,"名称的长度必须是介于1至50个字符之间");
        }
        if(!c.getPara("organization.addr").equals("")){
            validateString("organization.addr",3,128,"地址的长度必须是介于3至128个字符之间");
        }
    }


}
