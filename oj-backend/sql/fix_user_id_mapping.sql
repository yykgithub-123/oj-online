-- 修复用户ID映射问题
USE yykoj;

-- 首先查看当前的用户ID映射
SELECT '=== 当前用户表 ===' as info;
SELECT id, userName, userAccount FROM user WHERE isDelete = 0 ORDER BY id;

SELECT '=== 当前提交记录中的用户ID ===' as info;
SELECT DISTINCT userId FROM question_submit ORDER BY userId;

-- 更新question_submit表中的userId，将简单ID映射到实际的用户ID
-- 假设：
-- userId = 1 对应 亚克 (1950582655286054914)
-- userId = 2 对应 test1 (1950582655286054915) 
-- userId = 3 对应 test2 (1950582655286054916)

UPDATE question_submit SET userId = 1950582655286054914 WHERE userId = 1;
UPDATE question_submit SET userId = 1950582655286054915 WHERE userId = 2;
UPDATE question_submit SET userId = 1950582655286054916 WHERE userId = 3;

-- 验证修复结果
SELECT '=== 修复后的提交记录统计 ===' as info;
SELECT u.userName, 
       COUNT(DISTINCT qs.questionId) as total_solved,
       COUNT(DISTINCT CASE WHEN qs.createTime >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN qs.questionId END) as weekly_solved
FROM user u 
LEFT JOIN question_submit qs ON u.id = qs.userId AND qs.status = 2 AND qs.isDelete = 0
WHERE u.isDelete = 0
GROUP BY u.id, u.userName
ORDER BY total_solved DESC;

SELECT '=== 修复后的用户ID分布 ===' as info;
SELECT userId, COUNT(*) as submission_count 
FROM question_submit 
WHERE isDelete = 0 
GROUP BY userId 
ORDER BY userId;