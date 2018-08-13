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
  FROM
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