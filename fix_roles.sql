-- ==========================================================
-- 角色规范化: 1=管理员, 2=老人, 3=家属
-- 旧映射: 1=admin, 2=老人(旧), 3=老人, 4=家属
-- 新映射: 1=admin, 2=老人,   3=家属
-- ==========================================================

-- 旧 role=3(老人) → 新 role=2
UPDATE sys_user SET role = 2 WHERE role = 3;

-- 旧 role=4(家属) → 新 role=3
UPDATE sys_user SET role = 3 WHERE role = 4;

-- 验证结果
SELECT id, username, name, role,
  CASE role WHEN 1 THEN '管理员' WHEN 2 THEN '老人' WHEN 3 THEN '家属' END AS role_name
FROM sys_user
ORDER BY role, id;
