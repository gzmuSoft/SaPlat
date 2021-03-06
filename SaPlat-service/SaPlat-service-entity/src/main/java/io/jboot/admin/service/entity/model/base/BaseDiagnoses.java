package io.jboot.admin.service.entity.model.base;

import io.jboot.db.model.JbootModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by Jboot, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDiagnoses<M extends BaseDiagnoses<M>> extends JbootModel<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public void setSpell(java.lang.String spell) {
		set("spell", spell);
	}
	
	public java.lang.String getSpell() {
		return getStr("spell");
	}

	public void setProject(java.lang.Long project) {
		set("project", project);
	}
	
	public java.lang.Long getProject() {
		return getLong("project");
	}

	public void setSurveyFieldFileID(java.lang.Long surveyFieldFileID) {
		set("surveyFieldFileID", surveyFieldFileID);
	}
	
	public java.lang.Long getSurveyFieldFileID() {
		return getLong("surveyFieldFileID");
	}

	public void setSurveyGroup(java.lang.String surveyGroup) {
		set("surveyGroup", surveyGroup);
	}
	
	public java.lang.String getSurveyGroup() {
		return getStr("surveyGroup");
	}

	public void setMainRiskPoint(java.lang.String mainRiskPoint) {
		set("mainRiskPoint", mainRiskPoint);
	}
	
	public java.lang.String getMainRiskPoint() {
		return getStr("mainRiskPoint");
	}

	public void setInvestigationReport(java.lang.String investigationReport) {
		set("investigationReport", investigationReport);
	}
	
	public java.lang.String getInvestigationReport() {
		return getStr("investigationReport");
	}

	public void setSampleRate(java.math.BigDecimal sampleRate) {
		set("sampleRate", sampleRate);
	}
	
	public java.math.BigDecimal getSampleRate() {
		return get("sampleRate");
	}

	public void setSurveyMethod(java.lang.String surveyMethod) {
		set("surveyMethod", surveyMethod);
	}
	
	public java.lang.String getSurveyMethod() {
		return getStr("surveyMethod");
	}

	public void setSurveyWay(java.lang.String surveyWay) {
		set("surveyWay", surveyWay);
	}
	
	public java.lang.String getSurveyWay() {
		return getStr("surveyWay");
	}

	public void setCreateUserID(java.lang.Long createUserID) {
		set("createUserID", createUserID);
	}
	
	public java.lang.Long getCreateUserID() {
		return getLong("createUserID");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
	}
	
	public java.util.Date getCreateTime() {
		return get("createTime");
	}

	public void setLastUpdateUserID(java.lang.Long lastUpdateUserID) {
		set("lastUpdateUserID", lastUpdateUserID);
	}
	
	public java.lang.Long getLastUpdateUserID() {
		return getLong("lastUpdateUserID");
	}

	public void setLastAccessTime(java.util.Date lastAccessTime) {
		set("lastAccessTime", lastAccessTime);
	}
	
	public java.util.Date getLastAccessTime() {
		return get("lastAccessTime");
	}

	public void setSort(java.lang.Integer sort) {
		set("sort", sort);
	}
	
	public java.lang.Integer getSort() {
		return getInt("sort");
	}

	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}
	
	public java.lang.String getRemark() {
		return getStr("remark");
	}

	public void setIsEnable(java.lang.Boolean isEnable) {
		set("isEnable", isEnable);
	}
	
	public java.lang.Boolean getIsEnable() {
		return get("isEnable");
	}

	public void setStaffArrangements(java.lang.String staffArrangements) {
		set("staffArrangements", staffArrangements);
	}
	
	public java.lang.String getStaffArrangements() {
		return getStr("staffArrangements");
	}

	public void setSurveyDesign(java.lang.String surveyDesign) {
		set("surveyDesign", surveyDesign);
	}
	
	public java.lang.String getSurveyDesign() {
		return getStr("surveyDesign");
	}

}
