#sql("myStructure")
  select
      person.*
      ,struct.name
  from
       `struct_person_link` as person
      , `org_structure` as struct
  where
      person.structID = struct.id
        and
      person.personID = ?
  order by person.createTime desc
#end

#sql("StructurePersonList")
  select
       person.ID
      ,person.personID
      ,user.name
      ,person.structID
      ,person.createUserID
      ,person.createTime
      ,person.isEnable
  from
       `struct_person_link` as person
      ,`sys_user` as user
  where
      person.personID = user.userID
        and
      user.userSource = 0
        and
      person.structID = ?
  order by person.createTime desc
#end
#sql("orgStructureList")
  select
        a.*,
        if
          ( b.name IS NULL, "根架构", b.name ) as parentName
  from
      ( select
            *
        from
             org_structure
        where
            createUserID = #para(uid)
            and
            orgType = #para(orgType)
      ) as a
        left join
      ( select
            *
        from
             org_structure
        where
            createUserID = #para(uid)
            and
            orgType = #para(orgType)
      ) as b
      on a.parentID = b.ID
  where a.name like concat('%', #para(structName) ,'%')
  order by a.createTime desc
#end

#sql("searchStructureByName")
    select
         a.*
         ,b.name as belongToName
    from
         org_structure as a,
         sys_user as b
    where
         a.createUserID = b.id
           and
         a.name like concat('%', #para(structName) ,'%')
    order by a.createTime desc
  #end

#sql("searchStructureByID")
    select
        a.*
        ,b.name as belongToName
    from
        org_structure as a,
        sys_user as b
    where
        a.createUserID = b.id
          and
        a.id = #para(structId)
    order by a.createTime desc
#end
