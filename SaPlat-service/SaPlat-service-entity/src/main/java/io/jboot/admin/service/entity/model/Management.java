package io.jboot.admin.service.entity.model;

import io.jboot.db.annotation.Table;
import io.jboot.admin.service.entity.model.base.BaseManagement;

/**
 * Generated by Jboot.
 */
@Table(tableName = "management", primaryKey = "id")
public class Management extends BaseManagement<Management> {
    private String superiorName;

    public String getSuperiorName() {
        return superiorName;
    }

    public void setSuperiorName(String superiorName) {
        this.superiorName = superiorName;
    }
}
