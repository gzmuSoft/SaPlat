package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.Book;

import java.util.List;

public interface BookService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Book findById(Object id);


    /**
     * 分页查询系统书籍信息
     * @param book 书籍
     * @return 页
     */
    public Page<Book> findPage(Book book, int pageNumber, int pageSize);

    /**
     * 根据书名查询书籍信息
     * @param name
     * @return
     */
    public Book findByName(String name);


    /**
     * 书籍是否存在
     * @param name
     * @return 存在返回-true，否则返回false
     */
    public boolean hasBook(String name);


    /**
     * find all model
     *
     * @return all <Book
     */
    public List<Book> findAll();

    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Book model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Book model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Book model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Book model);


    public void join(Page<? extends Model> page, String joinOnField);
    public void join(Page<? extends Model> page, String joinOnField, String[] attrs);
    public void join(Page<? extends Model> page, String joinOnField, String joinName);
    public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs);
    public void join(List<? extends Model> models, String joinOnField);
    public void join(List<? extends Model> models, String joinOnField, String[] attrs);
    public void join(List<? extends Model> models, String joinOnField, String joinName);
    public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs);
    public void join(Model model, String joinOnField);
    public void join(Model model, String joinOnField, String[] attrs);
    public void join(Model model, String joinOnField, String joinName);
    public void join(Model model, String joinOnField, String joinName, String[] attrs);

    public void keep(Model model, String... attrs);
    public void keep(List<? extends Model> models, String... attrs);
}