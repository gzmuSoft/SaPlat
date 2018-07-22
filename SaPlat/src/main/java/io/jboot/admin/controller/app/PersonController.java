package io.jboot.admin.controller.app;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.upload.UploadFile;
import io.jboot.admin.base.common.BaseStatus;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.common.ResultCode;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.*;
import io.jboot.admin.service.entity.model.*;
import io.jboot.admin.service.entity.status.system.AuthStatus;
import io.jboot.admin.service.entity.status.system.TypeStatus;
import io.jboot.admin.support.auth.AuthUtils;
import io.jboot.admin.validator.app.PersonRegisterValidator;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        List<Nation> nations = nationService.findAll();
        BaseStatus nationStatus = new BaseStatus(){};
        for(Nation nation : nations){
            nationStatus.add(nation.getId().toString(),nation.getName());
        }

        //加载国籍
        List<Country>  contries= countryService.findAll();
        BaseStatus contryStatus = new BaseStatus(){};
        for(Country contry : contries){
            contryStatus.add(contry.getId().toString(),contry.getName());
        }

        //加载学历
        List<Educational>  educationals= educationalService.findAll();
        BaseStatus educationalStatus = new BaseStatus(){};
        for(Educational educational : educationals){
            educationalStatus.add(educational.getId().toString(),educational.getName());
        }

        //加载政治面貌
        List<PoliticalStatus>  politicalStatuses= politicalStatusService.findAll();
        BaseStatus politicalOpts = new BaseStatus(){};
        for(PoliticalStatus politicalStatus : politicalStatuses){
            politicalOpts.add(politicalStatus.getId().toString(),politicalStatus.getName());
        }

        //加载职业
        List<Occupation>  occupationStatuses= occupationService.findAll();
        BaseStatus occupationOpts = new BaseStatus(){};
        for(Occupation item : occupationStatuses){
            occupationOpts.add(item.getId().toString(),item.getName());
        }

        //加载职务
        List<Post> posts = postService.findAll();
        BaseStatus postStatus = new BaseStatus(){};
        for(Post post : posts){
            postStatus.add(post.getId().toString(),post.getName());
        }

        setAttr("politicalOpts", politicalOpts).
            setAttr("educationalStatus", educationalStatus).
            setAttr("contryStatus", contryStatus).
            setAttr("nationStatus", nationStatus).
            setAttr("occupationOpts", occupationOpts).
            setAttr("person", person).
            setAttr("affectedGroup", affectedGroup).
            setAttr("user", loginUser).
                setAttr("postStatus", postStatus).
            render("main.html");
    }

    /**
     * 注册页面
     */
    public void register() {
        render("register.html");
    }

    /**
     * 注册方法
     */
    @Before({POST.class, PersonRegisterValidator.class})
    public void postRegister() {
        Person person = getBean(Person.class, "person");
        User user = getBean(User.class, "user");
        user.setPhone(person.getPhone());
        user.setUserSource(0);
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
     * 文件上传
     */
    @Before(POST.class)
    public void upload() {
        UploadFile upload = getFile("file", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        String description = getPara("description");
        File file = upload.getFile();
        String oldName = file.getName();
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        String type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
        File newFile = new File(path + "\\" + UUID.randomUUID() + "." + type);
        if (!file.renameTo(newFile)) {
            if (file.delete()) {
                renderJson(RestResult.buildError("文件上传失败，请重新尝试！501"));
                throw new BusinessException("文件上传失败，请重新尝试！501");
            }
            renderJson(RestResult.buildError("文件上传失败，请重新尝试！502"));
            throw new BusinessException("文件上传失败，请重新尝试！502");
        }
        Files files = new Files();
        files.setName(oldName);
        files.setCreateTime(new Date());
        files.setDescription(description);
        files.setCreateUserID(AuthUtils.getLoginUser().getId());
        files.setIsEnable(false);
        files.setPath(newFile.getName());
        files.setSize(file.length());
        files.setType(type);
        if (!filesService.save(files)) {
            renderJson(RestResult.buildError("文件上传失败，请重新尝试！503"));
            throw new BusinessException("文件上传失败，请重新尝试！503");
        }
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("file", files.getPath());
        map.put("code", ResultCode.SUCCESS);
        renderJson(map);
    }


    /**
     * 更新用户资料
     */
    @Before(POST.class)
    public void postUpdate() {
        User loginUser = AuthUtils.getLoginUser();
        loginUser.setEmail(getPara("user.email"));
        Person person = personService.findByUser(loginUser);
        person.setPhone(getPara("person.phone"));
        person.setAge(Integer.parseInt(getPara("person.age")));
        person.setAddr(getPara("person.addr"));
        AffectedGroup affectedGroup = getBean(AffectedGroup.class, "affectedGroup");
        System.out.println(affectedGroup.toJson());
        affectedGroup.setName(person.getName());
        affectedGroup.setPersonID(person.getId());
        affectedGroup.setMail(loginUser.getEmail());
        affectedGroup.setLastAccessTime(new Date());
        if (affectedGroup.getResidence() == null) {
            affectedGroup.setResidence(person.getAddr());
        }
        if (affectedGroup.getPhone() == null) {
            affectedGroup.setPhone(loginUser.getPhone());
        }
        if (!personService.update(person, loginUser, affectedGroup)) {
            renderJson(RestResult.buildError("用户更新失败"));
            throw new BusinessException("用户更新失败");
        }
        renderJson(RestResult.buildSuccess());
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
        setAttr("auth", auth);
        setAttr("expertGroup", expertGroup);
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
        } else if (StringUtils.isNotBlank(expertGroup.getWorkpictrue())
                && StringUtils.isNotBlank(expertGroup.getCertificate())){
            Files file1 = filesService.findById(Integer.parseInt(expertGroup.getWorkpictrue()));
            Files file2 = filesService.findById(Integer.parseInt(expertGroup.getCertificate()));
            file1.setIsEnable(false);
            file2.setIsEnable(false);
            if (!filesService.update(file1) || !filesService.update(file2)){
                renderJson(RestResult.buildError("用户资料失败"));
                throw new BusinessException("用户资料失败");
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
        if (affectedGroupService.findByPersonId(person.getId()) == null) {
            renderJson(RestResult.buildError("请先在个人资料中完善您的个人信息"));
            throw new BusinessException("请先在个人资料中完善您的个人信息");
        }
        expertGroup.setName(person.getName());
        expertGroup.setCreateTime(new Date());
        expertGroup.setLastAccessTime(new Date());
        expertGroup.setPersonID(person.getId());
        expertGroup.setIsEnable(true);
        ExpertGroup name = expertGroupService.findByName(expertGroup.getName());
        if (name != null) {
            if (name.getIsEnable()) {
                renderJson(RestResult.buildError("专家团体已存在"));
                throw new BusinessException("专家团体已存在");
            }
            expertGroup.setId(name.getId());
        }
        String file1Path = getPara("file1");
        String file2Path = getPara("file2");

        List<Files> files = filesService.findByPath(file1Path, file2Path);
        files.forEach(file -> file.setIsEnable(true));
        expertGroup.setCertificate(String.valueOf(files.get(0).getId()));
        expertGroup.setWorkpictrue(String.valueOf(files.get(1).getId()));
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
    @Before(POST.class)
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
