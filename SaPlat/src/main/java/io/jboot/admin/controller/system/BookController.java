package io.jboot.admin.controller.system;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.base.common.RestResult;
import io.jboot.admin.base.exception.BusinessException;
import io.jboot.admin.base.interceptor.NotNullPara;
import io.jboot.admin.base.rest.datatable.DataTable;
import io.jboot.admin.base.web.base.BaseController;
import io.jboot.admin.service.api.BookService;
import io.jboot.admin.service.entity.model.Book;
import io.jboot.core.rpc.annotation.JbootrpcService;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * -----------------------------
 *
 * @author EchoLZY
 * @version 2.0
 * -----------------------------
 * @date 10:05 2018/7/2
 */
@RequestMapping("/system/book")
public class BookController extends BaseController{

    @JbootrpcService
    private BookService bookService;

    /**
     * index
     */
    public void index() {
        render("main.html");
    }
    /**
     * res表格数据
     */
    public void tableData() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);

        Book book = new Book();
        book.setName(getPara("name"));
        book.setDes(getPara("des"));

        Page<Book> page = bookService.findPage(book, pageNumber, pageSize);

        renderJson(new DataTable<Book>(page));
    }

    /**
     * delete
     */
    public void delete(){
        Long id = getParaToLong("id");
        if (!bookService.deleteById(id)){
            throw new BusinessException("删除失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    @NotNullPara({"id"})
    public void update(){
        Long id = getParaToLong("id");
        Book book = bookService.findById(id);
        setAttr("book",book).render("update.html");
    }

    public void add(){
        render("add.html");
    }

    public void postAdd(){
        Book book = getBean(Book.class, "book");
        if (bookService.hasBook(book.getName())){
            throw new BusinessException("书名已存在");
        }
        if (!bookService.save(book)){
            throw new BusinessException("保存失败");
        }
        renderJson(RestResult.buildSuccess());
    }

    public void postUpdate(){
        Book book = getBean(Book.class, "book");
        Book byId = bookService.findById(book.getId());
        if (byId == null){
            throw new BusinessException("书名不存在");
        }
        if (!bookService.update(book)){
            throw new BusinessException("修改失败");
        }
        renderJson(RestResult.buildSuccess());
    }
}
