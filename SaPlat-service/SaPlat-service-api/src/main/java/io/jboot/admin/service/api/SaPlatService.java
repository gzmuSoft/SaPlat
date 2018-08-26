package io.jboot.admin.service.api;

import com.jfinal.plugin.activerecord.Page;

import java.util.List;

/**
 * 用于装配对象及对象列表数据的接口
 * @param <T> 需要装配的对象的类型
 */

public interface SaPlatService<T> {

    /**
     * 装配完善 list 对象中所有对象的数据
     * @param list
     * @return
     */
    public default List<T> fitList(List<T> list){
        if(list != null){
            for (T item: list) {
                fitModel(item);
            }
        }
        return list;
    }

    /**
     * 装配完善 Page 对象中所有对象的数据
     * @param page
     * @return
     */
    public default Page<T> fitPage(Page<T> page){
        if(page != null){
            List<T> tList = page.getList();
            for (T item: tList) {
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
    public default T fitModel(T model){
        return model;
    }
}