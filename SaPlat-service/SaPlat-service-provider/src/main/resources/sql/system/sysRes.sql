#sql("findByUserNameAndStatusUsed")
  SELECT
    DISTINCT	a.*
  FROM
    sys_res a,
    sys_role b,
    sys_role_res c,
    sys_user d,
    sys_user_role e
  WHERE
    a.id = c.resID
    AND b.id = c.roleID
    AND d.id = e.userID
    and e.roleID = b.id
    AND a.`isEnable` = ?
    AND b.`isEnable` = ?
    and d.`name` = ?
#end

#sql("findTopMenuByUserName")
SELECT
  a.*
FROM
  sys_res a,
  sys_role b,
  sys_role_res c,
  sys_user d,
  sys_user_role e
WHERE
  a.id = c.resID
  AND b.id = c.roleID
  AND d.id = e.userID
  and e.roleID = b.id
  AND a.`isEnable` = ?
  AND b.`isEnable` = ?
  and a.parentID = ?
  and d.`name` = ?
ORDER BY a.parentID asc, a.sort asc
#end

#sql("findLeftMenuByUserNameAndParentID")
SELECT
  a.*
FROM
  sys_res a,
  sys_role b,
  sys_role_res c,
  sys_user d,
  sys_user_role e
WHERE
  a.id = c.resID
  AND b.id = c.roleID
  AND d.id = e.userID
  and e.roleID = b.id
  AND a.`isEnable` = ?
  AND b.`isEnable` = ?
  AND FIND_IN_SET(a.id, querySysRes(?))
  AND d.`name` = ?
ORDER BY a.parentID asc, a.sort asc
#end

#sql("findByRoleIdAndStatusUsed")
SELECT
  res.*
FROM
  sys_res res,
  sys_role_res role_res,
  sys_role role
WHERE
  role_res.resID = res.id
  AND role.id = role_res.roleID
  AND res. isEnable = ?
  AND role. isEnable = ?
  AND ROLE.id = ?
  AND res.url IS NOT NULL
ORDER BY res.parentID asc, res.sort asc
#end