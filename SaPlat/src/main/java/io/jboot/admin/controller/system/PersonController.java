package io.jboot.admin.controller.system;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.PersonService;
import io.jboot.admin.service.entity.model.Person;
import io.jboot.admin.validator.system.PersonValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.Date;

/**
 * Created by fengx on 2018/7/9.
 */
@RequestMapping("/system/person")
public class PersonController extends BaseController {
    @JbootrpcService
    private PersonService personService;

    /**
     * index
     */
    public void index() {
        render("main.html");
    }

    /**
     * person表格数据
     */
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        Person person = new Person();
        person.setName(getPara("name"));
        person.setIsEnable(getParaToBoolean("isEnable"));
        Date[] dates = new Date[2];
        try {
            dates[0] = getParaToDate("startDate");
            dates[1] = getParaToDate("endDate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Page<Person> page = personService.findPage(person, dates, pageNumber, pageSize);

        renderJson(new DataTable<Person>(page));
    }


    /**
     * update
     */
    @NotNullPara({"id"})
    public void update() {
        Long id = getParaToLong("id");
        Person person = personService.findById(id);
        setAttr("person", person).render("update.html");
    }

    /**
     * 修改提交
     */
    @Before({POST.class, PersonValidator.class})
    public void postUpdate() {
        Person person = getBean(Person.class, "person");
        Person byId = personService.findById(person.getId());
        if (byId == null) {
            throw new BusinessException("个人群体不存在");
        }
        //设置最后登录时间
        person.setLastAccessTime(new Date());
        if (!personService.update(person)) {
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 启用个人
     */
    @NotNullPara({"id"})
    public void use() {
        Long id = getParaToLong("id");

        Person person = personService.findById(id);
        if (person == null) {
            throw new BusinessException("编号为" + id + "的个人不存在");
        }
        //设置最后登录时间
        person.setLastAccessTime(new Date());
        //设置是否可用
        person.setIsEnable(true);
        if (!personService.useOrunuse(person)) {
            renderJson(RestResult.buildError("操作失败，用户可能未通过审核"));
            return;
        }
        renderJson(RestResult.buildSuccess());
    }

    /**
     * 停用个人
     */
    @NotNullPara({"id"})
    public void unuse() {
        Long id = getParaToLong("id");

        Person person = personService.findById(id);
        if (person == null) {
            throw new BusinessException("编号为" + id + "的个人不存在");
        }
        person.setIsEnable(false);
        person.setLastAccessTime(new Date());

        if (!personService.useOrunuse(person)) {
            renderJson(RestResult.buildError("操作失败，用户可能未通过审核"));
            return;
        }

        renderJson(RestResult.buildSuccess());
    }
}
