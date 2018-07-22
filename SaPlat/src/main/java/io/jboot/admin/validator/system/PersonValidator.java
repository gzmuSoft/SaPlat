package io.jboot.admin.validator.system;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;

/**
 * 修改个人群体校验器
 * @author Rlax
 *
 */
public class PersonValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateMobile("person.phone","电话号码格式不正确");
        if(!c.getPara("person.name").equals("")){
            validateString("person.name",1,50,"名称的长度必须是介于1至50个字符之间");
        }
        if(!c.getPara("person.addr").equals("")){
            validateString("person.addr",3,128,"地址的长度必须是介于3至128个字符之间");
        }
        }


}