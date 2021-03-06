#sql("project")
  select
      *
  from
       project_undertake
  where
    (
        facAgencyID = #para(facAgencyID)
    )
    and
        isEnable = 1
    and
        status = 2
  order by createTime desc
#end

#sql("project-self")
  select
      *
  from
       project_undertake
  where
        createUserID = #para(ID)
      and
        facAgencyID = #para(ID)
      and
        isEnable = 1
      and
        status = 2
  order by createTime desc
#end

#sql("project-xxx")
SELECT
    a.*
FROM
    project as a, project_undertake as b
where
    a.id = b.projectID
and
    b.status = 2
and
    a.status = #para(status)
and
    (b.facAgencyID = #para(facAgencyID)
    or (b.createUserID = b.facAgencyID and b.createUserID = #para(userID))
    )
#end

#sql("project-backRecord")
SELECT
    a.*
FROM
    project as a, project_undertake as b
where
    a.id = b.projectID
and
    a.status = #para(status)
and
    b.facAgencyID = #para(facAgencyID)
#end

#sql("project-Reviewing")
SELECT
    a.*
FROM
    project as a, project_undertake as b
where
    a.id = b.projectID
  and
    b.status = 2
  and
  #if(Remark)
      (a.status = #para(status)
      or	a.status = #para(Remark))
  #else
    a.status = #para(status)
  #end
  #if(name)
	and	a.name like concat('%', #para(name) ,'%')
	#end
	#if(paTypeID)
	and	paTypeID = #para(paTypeID)
	#end
  and
    #if(facAgencyID)
      ((a.assessmentMode='自评' and b.facAgencyID = #para(createUserID))
      OR (a.assessmentMode='委评' and b.facAgencyID = #para(facAgencyID)))
    #else if(managementID)
      managementID in (
      #for(id : mgr_list)
        #(for.index > 0 ? ", " : "")#(id)
      #end)
    #else
      a.createUserID = #para(createUserID)
    #end
#end

#sql("project-Reviewed")
SELECT
    a.*
FROM
    project as a, project_undertake as b
where
    a.id = b.projectID
  and
    b.status = 2
  and
  #if(Remark)
      (a.status = #para(status)
      or	a.status = #para(Remark))
  #else
    a.status = #para(status)
  #end
  #if(name)
	and	a.name like concat('%', #para(name) ,'%')
	#end
	#if(paTypeID)
	and	paTypeID = #para(paTypeID)
	#end
  and
    #if(facAgencyID)
      ((a.assessmentMode='自评' and b.facAgencyID = #para(createUserID))
      OR (a.assessmentMode='委评' and b.facAgencyID = #para(facAgencyID)))
    #else
      a.createUserID = #para(createUserID)
    #end
#end

#sql("project-undertake-ApplyIn")
SELECT
  *
FROM
  project_undertake
WHERE
  applyOrInvite = 0
  and projectID in
  (
    SELECT ID FROM project where userId=#para(buildProjectUserID)
    #if(name)
    and name like concat('%', #para(name) ,'%')
    #end
  )
  #if(status)
  and status=#para(status)
  #end
  order by projectID, createTime desc
#end

#sql("project-by-mgr")
SELECT
  *
FROM
  project
WHERE
  managementID in (
    #for(id : mgr_list)
      #(for.index > 0 ? ", " : "")#(id)
    #end)
  #if(name)
	and	name like concat('%', #para(name) ,'%')
	#end
	#if(status)
	and	status = #para(status)
	#end
	#if(paTypeID)
	and	paTypeID = #para(paTypeID)
	#end
  #if(managementID)
  and	managementID = #para(managementID)
  #end
	#if(minAmount)
	and	amount >= #para(minAmount)
	#end
	#if(maxAmount)
	and	amount <= #para(maxAmount)
	#end
	#if(isEnable)
	and	isEnable = #para(isEnable)
	#end
  order by createTime desc
#end

#sql("project-by-checked")
SELECT
  *
FROM
  project
WHERE
  managementID = #para(managementID)
  #if(Remark)
      and (status = #para(status)
      or	status = #para(Remark))
  #else
    and status = #para(status)
  #end
  #if(name)
	and	name like concat('%', #para(name) ,'%')
	#end
	#if(paTypeID)
	and	paTypeID = #para(paTypeID)
	#end
	#if(isEnable)
	and	isEnable = #para(isEnable)
	#end
  order by createTime desc
#end

#sql("project-by-checked-service")
SELECT * FROM
project
WHERE ID IN
  (
    SELECT
      DISTINCT projectID
    FROM
      project_undertake
    WHERE
      ((facAgencyID IN (SELECT ID FROM fac_agency WHERE orgID IN (SELECT userID FROM sys_user WHERE ID = #para(userID))) AND status = 2))
  )
  #if(name)
  and	name like concat('%', #para(name) ,'%')
  #end
  and (status = 12 or status = 7)
  #if(paTypeID)
  and	paTypeID = #para(paTypeID)
  #end
  #if(isEnable)
  and	isEnable = #para(isEnable)
  #end
  order by createTime desc
#end

#sql("project-by-creater")
SELECT
  *
FROM
  project
WHERE
  userId = #para(userID)
  #if(name)
	and	name like concat('%', #para(name) ,'%')
	#end
	#if(status)
	and	status = #para(status)
	#end
	#if(paTypeID)
	and	paTypeID = #para(paTypeID)
	#end
  #if(managementID)
  and	managementID = #para(managementID)
  #end
	#if(minAmount)
	and	amount >= #para(minAmount)
	#end
	#if(maxAmount)
	and	amount <= #para(maxAmount)
	#end
	#if(isEnable)
	and	isEnable = #para(isEnable)
	#end
  order by createTime desc
#end

#sql("project-by-service")
SELECT * FROM
project
WHERE ID IN
  (
    SELECT
      DISTINCT projectID
    FROM
      project_undertake
    WHERE
      ((facAgencyID = createUserID	AND facAgencyID = #para(userID))
      OR
      (facAgencyID IN (SELECT ID FROM fac_agency WHERE orgID IN (SELECT userID FROM sys_user WHERE ID = #para(userID))) AND `status` = 2))
  )
  #if(name)
  and	name like concat('%', #para(name) ,'%')
  #end
  #if(status)
  and	status = #para(status)
  #end
  #if(paTypeID)
  and	paTypeID = #para(paTypeID)
  #end
  #if(managementID)
  and	managementID = #para(managementID)
  #end
	#if(minAmount)
	and	amount >= #para(minAmount)
	#end
	#if(maxAmount)
	and	amount <= #para(maxAmount)
	#end
  #if(isEnable)
  and	isEnable = #para(isEnable)
  #end
  order by createTime desc
#end

#sql("invitedExpert-by-projectID")
SELECT * FROM person WHERE id IN(
	SELECT userID FROM sys_user WHERE ID IN(
		SELECT userID FROM apply_invite
		WHERE projectID = #para(ID)
    #if(belongToID)
    and	belongToID = #para(belongToID)
    #end
		AND  module = 1 AND isEnable = 1 AND createTime > (
		  SELECT IFNULL(MAX(createTime), '2000-1-1') FROM reject_project_info WHERE projectID = apply_invite.projectID
		)
	)
)
AND isEnable = 1
#end

#sql("lastInvited-by-projectID")
SELECT * FROM apply_invite
WHERE projectID = #para(ID)
  #if(belongToID)
  and	belongToID = #para(belongToID)
  #end
 AND createTime > (
  SELECT IFNULL(MAX(createTime), '2000-1-1') FROM reject_project_info WHERE projectID = apply_invite.projectID
)
AND  module = 1 AND isEnable = 1
#end

#sql("reviewed-history")
SELECT * FROM apply_invite
WHERE userID = #para(userID)
  AND	(remark = '审查完成' OR (status=2 AND deadTime < now()))
AND isEnable = 1
AND module = 1
order by createTime desc
#end