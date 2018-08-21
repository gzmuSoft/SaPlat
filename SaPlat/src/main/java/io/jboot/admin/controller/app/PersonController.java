package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.person.PersonPostUpdateValidator;
import io.jboot.admin.validator.app.person.PersonRegisterValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 23:44 2018/7/6
 */
@RequestMapping("/app/person")
public class PersonController extends BaseController {
    @JbootrpcService
    private UserService userService;

    @JbootrpcService
    private PersonService personService;

    @JbootrpcService
    private RoleService roleService;

    @JbootrpcService
    private ExpertGroupService expertGroupService;

    @JbootrpcService
    private AuthService authService;

    @JbootrpcService
    private AffectedGroupService affectedGroupService;

    @JbootrpcService
    private FilesService filesService;

    @JbootrpcService
    private NationService nationService;

    @JbootrpcService
    private CountryService countryService;

    @JbootrpcService
    private OccupationService occupationService;

    @JbootrpcService
    private EducationalService educationalService;

    @JbootrpcService
    private PoliticalStatusService politicalStatusService;

    @JbootrpcService
    private PostService postService;

    /**
     * 初始化
     */
    public void index() {
        User loginUser = AuthUtils.getLoginUser();
        Person person = personService.findByUser(loginUser);
        AffectedGroup affectedGroup = affectedGroupService.findByPersonId(person.getId());
        if (affectedGroup == null) {
            affectedGroup = new AffectedGroup();
        }


        //加载民族
        Nation nmodel = new Nation();
        nmodel.setIsEnable(true);
        List<Nation> nations = nationService.findAll(nmodel);
        BaseStatus nationStatus = new BaseStatus() {
        };
        for (Nation nation : nations) {
            nationStatus.add(nation.getId().toString(), nation.getName());
        }

        //加载国籍
        Country cmodel = new Country();
        cmodel.setIsEnable(true);
        List<Country> contries = countryService.findAll(cmodel);
        BaseStatus contryStatus = new BaseStatus() {
        };
        for (Country contry : contries) {
            contryStatus.add(contry.getId().toString(), contry.getName());
        }

        //加载学历
        Educational emodel = new Educational();
        emodel.setIsEnable(true);
        List<Educational> educationals = educationalService.findAll(emodel);
        BaseStatus educationalStatus = new BaseStatus() {
        };
        for (Educational educational : educationals) {
            educationalStatus.add(educational.getId().toString(), educational.getName());
        }

        //加载政治面貌
        PoliticalStatus psmodel = new PoliticalStatus();
        psmodel.setIsEnable(true);
        List<PoliticalStatus> politicalStatuses = politicalStatusService.findAll(psmodel);
        BaseStatus politicalOpts = new BaseStatus() {
        };
        for (PoliticalStatus politicalStatus : politicalStatuses) {
            politicalOpts.add(politicalStatus.getId().toString(), politicalStatus.getName());
        }

        //加载职业
        Occupation omodel = new Occupation();
        omodel.setIsEnable(true);
        List<Occupation> occupationStatuses = occupationService.findAll(omodel);
        BaseStatus occupationOpts = new BaseStatus() {
        };
        for (Occupation item : occupationStatuses) {
            occupationOpts.add(item.getId().toString(), item.getName());
        }

        //加载职务
        Post pmodel = new Post();
        pmodel.setIsEnable(true);
        List<Post> posts = postService.findAll(pmodel);
        BaseStatus postStatus = new BaseStatus() {
        };
        for (Post post : posts) {
            postStatus.add(post.getId().toString(), post.getName());
        }

        PoliticalStatus thisPolitical = politicalStatusService.findById(affectedGroup.getPoliticsID());
        Country thisCountry = countryService.findById(affectedGroup.getNationalityID());
        Nation thisNation = nationService.findById(affectedGroup.getNationID());
        Educational thisEducational = educationalService.findById(affectedGroup.getEducationID());
        Post thisPost = postService.findById(affectedGroup.getDutyID());
        Occupation thisOccupation = occupationService.findById(affectedGroup.getOccupationID());

        setAttr("user", loginUser).
                setAttr("person", person).
                setAttr("affectedGroup", affectedGroup).
                setAttr("politicalOpts", politicalOpts).
                setAttr("educationalStatus", educationalStatus).
                setAttr("contryStatus", contryStatus).
                setAttr("nationStatus", nationStatus).
                setAttr("occupationOpts", occupationOpts).
                setAttr("postStatus", postStatus).
                setAttr("thisPolitical", thisPolitical).
                setAttr("thisCountry", thisCountry).
                setAttr("thisNation", thisNation).
                setAttr("thisEducational", thisEducational).
                setAttr("thisPost", thisPost).
                setAttr("thisOccupation", thisOccupation).
                render("main.html");
    }

    /**
     * 注册方法
     */
    @Before({POST.class, PersonRegisterValidator.class})
    public void postRegister() {
        Person person = getBean(Person.class, "person");
        User user = getBean(User.class, "user");
        user.setPhone(person.getPhone());
        user.setUserSource(0);//设置对应的用户来源于“个人群体”的注册
        if (userService.hasUser(user.getName())) {
            renderJson(RestResult.buildError("用户名已存在"));
            throw new BusinessException("用户名已存在");
        }
        person.setCreateTime(new Date());
        person.setLastAccessTime(new Date());
        person.setIsEnable(true);
        Long[] roles = new Long[]{roleService.findByName("个人群体").getId()};
        if (!personService.savePerson(person, user, roles)) {
            renderJson(RestResult.buildError("用户保存失败"));
            throw new BusinessException("用户保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }


    /**
     * 更新用户资料
     */
    @Before({POST.class, PersonPostUpdateValidator.class})
    public void postUpdate() {
        User loginUser = AuthUtils.getLoginUser();
        loginUser.setPhone(getPara("person.phone"));
        Person person = personService.findByUser(loginUser);
        Long file = getParaToLong("person.identity");
        person.setPhone(getPara("person.phone"));
        person.setAge(DateTime.now().getYear() - DateTime.parse(getPara("affectedGroup.birthday")).getYear());
        Files files = null;
        Files fileNow = null;
        // 第一次，没有上传身份证证件照
        if (file == null) {
            throw new BusinessException("请上传身份证证件照");
        }
        // 如果文件上传了，查找出来修改状态
        fileNow = filesService.findById(file);
        fileNow.setIsEnable(true);
        person.setIdentity(file.toString());
        // 并查一下以前是否上传过，如果上传过修改状态，将它禁用
        if (StringUtils.isNotBlank(person.getIdentity())) {
            files = filesService.findById(person.getIdentity());
            files.setIsEnable(false);
        }
        AffectedGroup affectedGroup = getBean(AffectedGroup.class, "affectedGroup");
        affectedGroup.setName(person.getName());
        affectedGroup.setPersonID(person.getId());
        affectedGroup.setMail(loginUser.getEmail());
        affectedGroup.setLastAccessTime(new Date());
        person.setAddr(affectedGroup.getResidence());
        if (affectedGroup.getResidence() == null) {
            affectedGroup.setResidence(person.getAddr());
        }
        if (affectedGroup.getPhone() == null) {
            affectedGroup.setPhone(loginUser.getPhone());
        }
        AffectedGroup group = affectedGroupService.findByPersonId(person.getId());
        if (group != null) {
            affectedGroup.setId(group.getId());
        }
        if (personService.update(person, loginUser, affectedGroup, files, fileNow)) {
            renderJson(RestResult.buildSuccess());
        } else {
            throw new BusinessException("用户更新失败");
        }
    }

    /**
     * 专家团体页面
     */
    public void expertGroup() {
        User user = AuthUtils.getLoginUser();
        ExpertGroup expertGroup = expertGroupService.findByPersonId(user.getUserID());
        Auth auth = new Auth();
        auth.setStatus("100");
        if (expertGroup != null) {
            auth = authService.findByUserAndRole(user, roleService.findByName("专家团体").getId());
        }
        Person person = personService.findByUser(user);
        AffectedGroup affectedGroup = affectedGroupService.findByPersonId(person.getId());
        setAttr("auth", auth);
        setAttr("expertGroup", expertGroup);
        setAttr("affectedGroup", affectedGroup);
        render("expertGroup.html");
    }

    /**
     * 认证页面
     */
    public void verify() {
        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        ExpertGroup expertGroup = expertGroupService.findByPersonId(person.getId());
        if (expertGroup == null) {
            expertGroup = new ExpertGroup();
            setAttr("flag", "false");
        } else {
            setAttr("flag", "true");
            Auth auth = authService.findByUserIdAndStatusAndType(user.getId(), AuthStatus.NOT_VERIFY, "0");
            if (auth == null) {
                setAttr("auth", "true");
            } else {
                setAttr("auth", "false");
            }
        }
        setAttr("user", user)
                .setAttr("person", person)
                .setAttr("expertGroup", expertGroup)
                .render("verify.html");
    }


    /**
     * 专家团体认证
     */
    @Before(POST.class)
    public void expertGroupVerify() throws IOException {
        ExpertGroup expertGroup = getBean(ExpertGroup.class, "expertGroup");
        User user = AuthUtils.getLoginUser();
        Person person = personService.findByUser(user);
        AffectedGroup affectedGroup = affectedGroupService.findByPersonId(person.getId());
        if (affectedGroup == null) {
            renderJson(RestResult.buildError("请先在个人资料中完善您的个人信息"));
            throw new BusinessException("请先在个人资料中完善您的个人信息");
        }
        expertGroup.setAffectedGroupID(affectedGroup.getId());
        expertGroup.setCreateTime(new Date());
        expertGroup.setLastAccessTime(new Date());
        expertGroup.setPersonID(person.getId());
        expertGroup.setName(person.getName());
        expertGroup.setIsEnable(true);
        ExpertGroup name = expertGroupService.findByName(expertGroup.getName());
        if (name != null) {
            expertGroup.setId(name.getId());
        }
        int file1 = Integer.parseInt(expertGroup.getWorkpictrue());
        int file2 = Integer.parseInt(expertGroup.getCertificate());
        Files files1 = filesService.findById(file1);
        Files files2 = filesService.findById(file2);
        files1.setIsEnable(true);
        files2.setIsEnable(true);
        List<Files> files = Collections.synchronizedList(new ArrayList<>());
        files.add(files1);
        files.add(files2);
        Auth auth = new Auth();
        auth.setUserId(user.getId());
        auth.setName(user.getName());
        auth.setRoleId(roleService.findByName("专家团体").getId());
        auth.setLastUpdTime(new Date());
        auth.setStatus(AuthStatus.VERIFYING);
        auth.setType(TypeStatus.PERSON);
        Auth userAndRole = authService.findByUserAndRole(user, roleService.findByName("专家团体").getId());
        if (userAndRole != null) {
            auth.setId(userAndRole.getId());
        }
        if (!expertGroupService.saveOrUpdate(expertGroup, auth, files)) {
            renderJson(RestResult.buildError("专家团体认证上传失败"));
            throw new BusinessException("专家团体认证上传失败");
        }

        renderJson(RestResult.buildSuccess());
    }

    /**
     * 专家团体取消认证
     */
    public void cancelExpertGroupAuth() {
        User user = AuthUtils.getLoginUser();
        ExpertGroup expertGroup = expertGroupService.findByPersonId(personService.findByUser(user).getId());
        Auth auth = authService.findByUserAndRole(user, roleService.findByName("专家团体").getId());
        auth.setStatus(AuthStatus.CANCEL_VERIFY);
        int cer = Integer.parseInt(expertGroup.getCertificate());
        int wpic = Integer.parseInt(expertGroup.getWorkpictrue());
        List<Files> files = Collections.synchronizedList(new ArrayList<Files>());
        files.add(filesService.findById(cer));
        files.add(filesService.findById(wpic));
        files.forEach(file -> file.setIsEnable(false));
        expertGroup.setIsEnable(false);
        if (!expertGroupService.saveOrUpdate(expertGroup, auth, files)) {
            renderJson(RestResult.buildError("修改认证状态失败"));
            throw new BusinessException("修改认证状态失败");
        }
        renderJson(RestResult.buildSuccess());
    }

}
