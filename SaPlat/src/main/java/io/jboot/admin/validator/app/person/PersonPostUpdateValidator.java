package io.jboot.admin.validator.app.person;

import com.jfinal.core.Controller;
import io.jboot.admin.base.web.base.JsonValidator;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.entity.model.AffectedGroup;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.support.auth.AuthUtils;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 22:30 2018/8/21
 */
public class PersonPostUpdateValidator extends JsonValidator {

    @Override
    protected void validate(Controller c) {
        validateDate("affectedGroup.birthday", "请输入合法出生日期");
        validateMobile("person.phone", "请输入合法的手机号");
        validateRequiredString("person.identity", "请上传身份证证件照");
        validateRequiredString("affectedGroup.nationalityID", "请选择 个人资料 - 国籍");
        validateRequiredString("affectedGroup.nationID", "请选择 个人资料 - 民族");
        validateRequiredString("affectedGroup.politicsID", "请选择 个人资料 - 政治面貌");
        validateRequiredString("affectedGroup.educationID", "请选择 个人资料 - 学历");
        validateRequiredString("affectedGroup.occupationID", "请填写 工作信息 - 职业");
        validateRequiredString("affectedGroup.dutyID", "请填写 工作信息 - 职务");
        validateRequiredString("affectedGroup.residence", "请填写 联系方式 - 长期居住地址，1-255字符");
    }
}
