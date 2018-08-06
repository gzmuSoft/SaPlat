package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.admin.service.entity.model.FileForm;
import io.jboot.admin.service.entity.model.Files;

import java.util.List;

public interface FileFormService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public FileForm findById(Object id);


    /**
     * find all model
     *
     * @return all <FileForm
     */
    public List<FileForm> findAll();

    /**
     * find  model by tableName and recordID and fieldName
     *
     * @param tableName
     * @param fieldName
     * @param recordID
     * @return
     */
    public FileForm findFirstByTableNameAndRecordIDAndFileName(String tableName, String fieldName, Long recordID);

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
    public boolean delete(FileForm model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(FileForm model);

    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public FileForm saveAndGet(FileForm model);

    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(FileForm model);

    /**
     * save or update model
     *
     * @param model
     * @param files
     * @return
     */
    public FileForm saveOrUpdateAndGet(FileForm model, Files files);
    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(FileForm model);


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