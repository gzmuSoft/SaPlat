#sql("myStructure")
  select
      person.*
      ,struct.name
      from
      `struct_person_link` person
      , `org_structure` struct
      where
      person.structID = struct.id
      and person.personID = ?
      order by person.createTime desc
#end