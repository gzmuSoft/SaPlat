package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.UserRoleService;
import io.jboot.admin.service.api.UserService;
import io.jboot.admin.service.entity.model.User;
import io.jboot.admin.service.entity.model.UserRole;
import io.jboot.admin.service.entity.status.system.UserOnlineStatus;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Bean
@Singleton
@JbootrpcService
public class UserServiceImpl extends JbootServiceBase<User> implements UserService{

    @Inject
    private UserRoleService userRoleService;

    /**
     * find all model
     *
     * @param model 项目主体
     * @return all <Project>
     */
    @Override
    public User findModel(User model) {
        Columns columns = Columns.create();
        if (StrKit.notNull(model.getIsEnable())) {
            columns.eq("isEnable", model.getIsEnable());
        }
        if (model.getUserID() != null) {
            columns.eq("userID", model.getUserID());
        }
        if (model.getUserSource() != null) {
            columns.eq("userSource", model.getUserSource());
        }
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public Page<User> findPage(User user, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(user.getName())) {
            columns.like("name", "%" + user.getName() + "%");
        }
        if (StrKit.notBlank(user.getPhone())) {
            columns.like("phone", "%" + user.getPhone() + "%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<User> findPage(User user,Date[] dates, int pageNumber, int pageSize) {
        Columns columns = Columns.create();

        if (StrKit.notBlank(user.getName())) {
            columns.like("name", "%" + user.getName() + "%");
        }
        if (StrKit.notBlank(user.getPhone())) {
            columns.like("phone", "%" + user.getPhone() + "%");
        }
        if (StrKit.notNull(dates[0])){
            columns.ge("createTime", dates[0]);
        }
        if (StrKit.notNull(dates[1])){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dates[1]);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dates[1] = calendar.getTime();
            columns.le("createTime", dates[1]);
        }

        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean hasUser(String name) {
        Columns columns = Columns.create();
        columns.eq("name", name);
        return DAO.findFirstByColumns(columns) != null;
    }

    @Override
    public User findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @Override
    public User findByUserIdAndUserSource(Long userID, Long userSource) {
        Columns columns = Columns.create();
        columns.eq("userID", userID);
        columns.eq("userSource", userSource);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public boolean saveUser(User user, Long[] roles) {
        String pwd = user.getPwd();

        if (StrKit.notBlank(pwd)) {
            String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
            SimpleHash hash = new SimpleHash("md5", pwd, salt, 2);
            pwd = hash.toHex();
            user.setPwd(pwd);
            user.setSalt(salt);
        }

        user.setOnlineStatus(UserOnlineStatus.OFFLINE);
        user.setCreateTime(new Date());
        user.setLastAccessTime(new Date());
        user.setRemark("保存系统用户");

        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (!user.save()) {
                    return false;
                }

                if (roles != null) {
                    List<UserRole> list = new ArrayList<UserRole>();
                    for (Long roleId : roles) {
                        UserRole userRole = new UserRole();
                        userRole.setUserID(user.getId());
                        userRole.setRoleID(roleId);
                        list.add(userRole);
                    }
                    int[] rets = userRoleService.batchSave(list);

                    for (int ret : rets) {
                        if (ret < 1) {
                            return false;
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean updateUser(User user, Long[] roles) {
        String pwd = user.getPwd();
        if (StrKit.notBlank(pwd)) {
            String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
            SimpleHash hash = new SimpleHash("md5", pwd, salt, 2);
            pwd = hash.toHex();
            user.setPwd(pwd);
            user.setSalt(salt);
        } else {
            user.remove("pwd");
        }

        user.setLastAccessTime(new Date());
        user.setRemark("修改系统用户");

        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (!user.update()) {
                    return false;
                }

                userRoleService.deleteByUserId(user.getId());

                if (roles != null) {
                    List<UserRole> list = new ArrayList<UserRole>();
                    for (Long roleId : roles) {
                        UserRole userRole = new UserRole();
                        userRole.setUserID(user.getId());
                        userRole.setRoleID(roleId);
                        list.add(userRole);
                    }

                    int[] rets = userRoleService.batchSave(list);
                    for (int ret : rets) {
                        if (ret < 1) {
                            return false;
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public User findByUserIdAndUserSource(Long userId, Integer userSource) {
        Columns columns = Columns.create();
        columns.eq("userID", userId);
        columns.eq("userSource", userSource);
        return DAO.findFirstByColumns(columns);
    }

    @Override
    public List<User> findAll(){
        return DAO.findAll();
    }
}