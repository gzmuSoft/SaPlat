package io.jboot.admin.validator.app;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-07.
 * 角色管理填写合法性校验
 *
 * @author Rlax
 */
public class RoleValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {

        if (!c.getPara("role.sort").equals("")) {
            validateInteger("role.sort", 1, Integer.MAX_VALUE, String.format("排序必须是介于1至%d之间的整数", Integer.MAX_VALUE));
        }
    }
}
