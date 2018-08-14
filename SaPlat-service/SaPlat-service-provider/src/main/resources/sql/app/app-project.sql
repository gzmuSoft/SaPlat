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