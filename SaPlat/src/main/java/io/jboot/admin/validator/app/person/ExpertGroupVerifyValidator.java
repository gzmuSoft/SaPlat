package io.jboot.admin.validator.app.person;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.JsonValidator;
import io.jboot.admin.service.entity.model.ExpertGroup;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 23:46 2018/8/21
 */
public class ExpertGroupVerifyValidator extends JsonValidator {
    @Override
    protected void validate(Controller c) {
        validateRequiredString("expertGroup.workstate", "请选择 在职状态");
        validateDate("expertGroup.worktime", "请输入在职时间");
        validateRequiredString("expertGroup.department", "请输入部门名称");
        validateRequiredString("expertGroup.workpictrue", "请上传工作证件照片");
        validateRequiredString("expertGroup.certificate", "请上传专业资格证书");
        validateRequiredString("expertGroup.profess", "请填写专业资格内容");
        validateRequiredString("expertGroup.project", "请填写参与项目经历");
    }
}
