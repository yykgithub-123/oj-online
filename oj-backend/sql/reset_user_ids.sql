-- 重置用户ID为简单的短ID
USE yykoj;

-- 首先备份当前数据
CREATE TABLE user_backup AS SELECT * FROM user;

-- 删除现有用户数据
DELETE FROM user WHERE isDelete = 0;

-- 重新插入用户数据，使用简单ID
INSERT INTO user (id, userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole, createTime, updateTime, isDelete) VALUES 
(1, 'yyk123', 'f19c931d48c6d4458af1d6f4497fa396', null, null, '亚克', null, '程序员亚克', 'admin', '2025-07-30 23:41:59', '2025-07-30 23:42:32', 0),
(2, 'test1', 'f19c931d48c6d4458af1d6f4497fa396', null, null, 'test1', null, null, 'user', '2025-08-01 16:24:55', '2025-08-01 16:24:57', 0),
(3, 'test2', 'f19c931d48c6d4458af1d6f4497fa396', null, null, 'test2', null, null, 'user', '2025-08-01 16:24:55', '2025-08-01 16:24:57', 0),
(4, 'test3', 'f19c931d48c6d4458af1d6f4497fa396', null, null, 'test3', null, null, 'user', '2025-08-01 16:24:55', '2025-08-01 16:24:57', 0);

-- 验证结果
SELECT '=== 新的用户表 ===' as info;
SELECT id, userName, userAccount FROM user WHERE isDelete = 0 ORDER BY id;

SELECT '=== 提交记录中的用户ID ===' as info;
SELECT DISTINCT userId FROM question_submit ORDER BY userId;

-- 验证排行榜数据
SELECT '=== 排行榜统计 ===' as info;
SELECT u.userName, 
       COUNT(DISTINCT qs.questionId) as total_solved,
       COUNT(DISTINCT CASE WHEN qs.createTime >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN qs.questionId END) as weekly_solved
FROM user u 
LEFT JOIN question_submit qs ON u.id = qs.userId AND qs.status = 2 AND qs.isDelete = 0
WHERE u.isDelete = 0
GROUP BY u.id, u.userName
ORDER BY total_solved DESC;