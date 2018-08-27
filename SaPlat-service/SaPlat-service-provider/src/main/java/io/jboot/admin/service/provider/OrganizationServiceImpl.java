package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.OrganizationService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.Organization;
import io.jboot.admin.service.entity.model.User;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class OrganizationServiceImpl extends JbootServiceBase<Organization> implements OrganizationService{
    @Inject
    private UserService userService;

    UserServiceImpl userServiceNew = new UserServiceImpl();

    /**
     * 装配完善Page对象中所有对象的数据
     * @param page
     * @return
     */
    public Page<Organization> fitPage(Page<Organization> page){
        if(page != null){
            List<Organization> tList = page.getList();
            for (Organization item: tList) {
                fitModel(item);
            }
        }
        return page;
    }

    /**
     * 装配单个实体对象的数据
     * @param model
     * @return
     */
    public Organization fitModel(Organization model){
        if(model!=null) {
            User user = new User();
            user.setUserID(model.getId());
            user.setUserSource(1);// 1 代表组织机构
            model.setUser(userServiceNew.findModel(user));
        }
        return model;
    }

    /**
     * 分页查询
     *
     * @param organization 组织
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public Page<Organization> findPage(Organization organization, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(organization.getName())) {
            columns.like("name", "%" + organization.getName() + "%");
        }
        return fitPage(DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc"));
    }

    @Override
    public boolean save(Organization organization) {
        organization.setCreateTime(new Date());
        organization.setLastAccessTime(new Date());
        organization.setIsEnable(true);
        return Db.tx(organization::save);
    }

    @Override
    public boolean saveOrganization(Organization model, User user, Long[] roles) {
        return Db.tx(() -> {
            if (!save(model)) {
                return false;
            }
            user.setUserID(model.getId());
            return userService.saveUser(user, roles);
        });
    }

    @Override
    public boolean hasUser(String name) {
        return findByName(name) != null;
    }

    @Override
    public Organization findByName(String name) {
        return fitModel(DAO.findFirstByColumn("name", name));
    }

    @Override
    public boolean update(Organization organization, User user) {
        return Db.tx(() -> organization.update() && user.update());
    }

}