package io.jboot.admin.service.entity.model;

import io.jboot.db.annotation.Table;
import io.jboot.admin.service.entity.model.base.BaseNews;

import java.util.Date;

/**
 * Generated by Jboot.
 */
@Table(tableName = "news", primaryKey = "id")
public class News extends BaseNews<News> {
	public String cstime;
	public String cetime;

    public String getCstime() {
        return cstime;
    }

    public void setCstime(String cstime) {
        this.cstime = cstime;
    }

    public String getCetime() {
        return cetime;
    }

    public void setCetime(String cetime) {
        this.cetime = cetime;
    }
}
