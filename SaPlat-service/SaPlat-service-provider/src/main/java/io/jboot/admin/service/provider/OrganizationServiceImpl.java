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

@Bean
@Singleton
@JbootrpcService
public class OrganizationServiceImpl extends JbootServiceBase<Organization> implements OrganizationService {

    @Inject
    private UserService userService;

    /**
     * 分页查询
     * @param organization 组织
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Organization> findPage(Organization organization, int pageNumber, int pageSize){
        Columns columns = Columns.create();
        if (StrKit.notBlank(organization.getName())){
            columns.like("name", "%"+organization.getName()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean saveOrganization(Organization model, User user, Long[] roles) {
        return Db.tx(() -> userService.saveUser(user, roles) && save(model));
    }
}