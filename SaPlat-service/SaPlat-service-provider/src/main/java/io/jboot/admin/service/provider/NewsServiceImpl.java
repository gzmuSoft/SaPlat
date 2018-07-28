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
        if (StrKit.notBlank(model.getName())){
            columns.like("name", "%" + model.getName()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public Page<News> findReverses(int size) {
        return DAO.paginateByColumns(1, size, null,"id desc");
    }
}
