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
        validateString("person.identity", 1, 10, "请上传身份证证件照");
        AffectedGroup affectedGroup = c.getBean(AffectedGroup.class, "affectedGroup");
        validateLong(affectedGroup.getNationalityID().toString(), 1, 10, "请选择 个人资料 - 国籍");
        validateLong(affectedGroup.getNationID().toString(), 1, 10, "请选择 个人资料 - 民族");
        validateLong(affectedGroup.getPoliticsID().toString(), 1, 10, "请选择 个人资料 - 政治面貌");
        validateLong(affectedGroup.getEducationID().toString(), 1, 10, "请选择 个人资料 - 学历");
        validateLong(affectedGroup.getOccupationID().toString(), 1, 10, "请填写 工作信息 - 职业");
        validateLong(affectedGroup.getDutyID().toString(), 1, 10, "请填写 工作信息 - 职务");
        validateString(affectedGroup.getResidence().trim(), 1, 255, "请填写 联系方式 - 长期居住地址，1-255字符");
    }
}
