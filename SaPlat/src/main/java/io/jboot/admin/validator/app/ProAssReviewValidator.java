package io.jboot.admin.validator.app;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;

/**
 * Created by ASUS on 2018-07-07.
 * 国家信息填写合法性校验
 * @author Rlax
 *
 */
public class ProAssReviewValidator extends JsonValidator {
 
    @Override
    protected void validate(Controller c) {
        if(!c.getPara("model.name").equals("")){
            validateString("model.name",1,50,"名称的长度必须是介于1至50个字符之间");
        }
        if(!c.getPara("model.sort").equals("")) {
            validateInteger("model.sort", 1, Integer.MAX_VALUE, String.format("排序必须是介于1至%d之间", Integer.MAX_VALUE));
        }
    }
}
