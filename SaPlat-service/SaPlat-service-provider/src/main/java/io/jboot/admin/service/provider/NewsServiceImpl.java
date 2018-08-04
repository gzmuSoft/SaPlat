package io.jboot.admin.service.provider;


import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.api.NewsService;
import io.jboot.admin.service.entity.model.News;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class NewsServiceImpl extends JbootServiceBase<News> implements NewsService {
    @Override
    public Page<News> findPage(News model, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        columns.eq("isEnable", 1);
        if (StrKit.notBlank(model.getName())) {
            columns.like("name", "%" + model.getName() + "%");
        }
        if (StrKit.notBlank(model.getTitle())) {
            columns.like("title", "%" + model.getTitle() + "%");
        }
        if (StrKit.notNull(model.getCreateUserID())) {
            columns.eq("createUserID", "%" + model.getCreateUserID() + "%");
        }
        if (StrKit.notNull(model.getLastUpdateUserID())) {
            columns.eq("lastUpdateUserID", "%" + model.getLastUpdateUserID() + "%");
        }
        if (StrKit.notNull(model.getCstime()) && StrKit.notNull(model.getCetime())) {
            columns.ge("createTime", model.getCstime());
            columns.le("createTime", model.getCetime());
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<News> findReverses(int size) {
        return DAO.paginateByColumns(1, size, null, "id desc");
    }
}
