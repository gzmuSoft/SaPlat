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
      ,user.ID as userID
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

#sql("OrgPersonList")
    select
        p.*,
				s.name as userAccount
    from
        person as p,
				sys_user as s
    where
        p.id in (
                select distinct
                    personID
                from
                    struct_person_link as s
                where
                    s.structID in (
                                  select distinct
                                     id
                                  from
                                      org_structure
                                  where orgID = #para(OrgId))
        )
				and (
					s.userID = p.id
					and
					s.userSource=0
				)
				ORDER BY createTime desc
#end

#sql("OrgPersonListByType")
    select
        *
    from
        sys_user as s
    where
        s.userID in (
                select distinct
                    personID
                from
                    struct_person_link as s
                where
                    s.structID in (
                                  select distinct
                                     id
                                  from
                                      org_structure
                                  where orgType = #para(orgType) and orgID = #para(OrgId))
        )
and userSource = 0
#end

#sql("OrgPersonInfo")
    SELECT
      s.name as user,
      p.name,
      p.sex,
      p.age,
      p.phone,
      s.email
    FROM
      sys_user AS s
      JOIN person AS p
    WHERE
      s.userID = p.id
    AND
      s.userSource = 0
    AND
      s.userID = #para(userID)
#end

#sql("ExportGroupsByOrgId")
SELECT * FROM expert_group WHERE personID IN
(
	SELECT personID FROM struct_person_link WHERE structID IN
	(
		SELECT id FROM org_structure where orgID = #para(orgID)
	)
)
#end
