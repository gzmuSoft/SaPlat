package io.jboot.admin.service.provider;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.admin.service.api.BookService;
import io.jboot.admin.service.entity.model.Book;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
@JbootrpcService
public class BookServiceImpl extends JbootServiceBase<Book> implements BookService {

    @Override
    public Page<Book> findPage(Book book, int pageNumber, int pageSize) {
        Columns columns = Columns.create();
        if (StrKit.notBlank(book.getName())){
            columns.like("name", "%"+book.getName()+"%");
        }
        if (StrKit.notBlank(book.getDes())){
            columns.like("des", "%"+book.getDes()+"%");
        }
        return DAO.paginateByColumns(pageNumber, pageSize, columns.getList(), "id desc");
    }

    @Override
    public boolean hasBook(String name) {
        return findByName(name) != null;
    }

    @Override
    public Book findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }
}