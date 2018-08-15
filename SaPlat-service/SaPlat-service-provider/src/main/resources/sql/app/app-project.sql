#sql("project")
  select
      *
  from
       project_undertake
  where
    (
        facAgencyID = #para(facAgencyID)
          or
        (
          createUserID = #para(ID)
            AND
          facAgencyID = #para(ID)
        )
          or
        createUserID = #para(createUserID)
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
    project
  as
    a,
    project_undertake
  as
    b
where
    a.isEnable = 1
and
    a.id = b.projectID
and
    a.status = #para(status)
and
    ((b.facAgencyID = #para(facAgencyID)
or
    (b.createUserID = b.facAgencyID
  and
    b.createUserID = #para(userID))))
#end