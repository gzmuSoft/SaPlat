#sql("findByUserName")
  SELECT
    b.*
  FROM
    sys_role b,
    sys_user d,
    sys_user_role e
  WHERE
    b.id = e.roleID
    AND d.id = e.userID
    AND d.`name` = ?
#end