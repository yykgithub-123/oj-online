-- =====================================================
-- 批量插入测试数据 - 使用现有用户和题目代码
-- 执行时间: 2026-03-29
-- 说明: 直接INSERT，兼容IDEA数据库控制台
-- =====================================================

-- 插入提交记录 - 两数之和 (题目ID=1)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":156}', 2, 1, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 58 DAY), NOW(), 0, 156, 42, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":89}', 2, 1, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 55 DAY), NOW(), 0, 89, 41, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":38,"time":45}', 3, 1, 1950582655286054916, DATE_SUB(NOW(), INTERVAL 52 DAY), NOW(), 0, 45, 38, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":134}', 2, 1, 1951677564449124353, DATE_SUB(NOW(), INTERVAL 48 DAY), NOW(), 0, 134, 40, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":43,"time":142}', 2, 1, 1951677564449124354, DATE_SUB(NOW(), INTERVAL 45 DAY), NOW(), 0, 142, 43, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":128}', 2, 1, 1951677564449124355, DATE_SUB(NOW(), INTERVAL 42 DAY), NOW(), 0, 128, 41, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":39,"time":115}', 2, 1, 1951677564449124356, DATE_SUB(NOW(), INTERVAL 38 DAY), NOW(), 0, 115, 39, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":138}', 2, 1, 1951677564449124357, DATE_SUB(NOW(), INTERVAL 35 DAY), NOW(), 0, 138, 42, 500 FROM question WHERE id = 1 LIMIT 1;

-- 无重复字符的最长子串 (题目ID=2)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":8}', 2, 2, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 50 DAY), NOW(), 0, 8, 42, 500 FROM question WHERE id = 2 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":44,"time":12}', 2, 2, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 45 DAY), NOW(), 0, 12, 44, 500 FROM question WHERE id = 2 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":38,"time":5}', 3, 2, 1950582655286054917, DATE_SUB(NOW(), INTERVAL 40 DAY), NOW(), 0, 5, 38, 500 FROM question WHERE id = 2 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":6}', 2, 2, 1951677564449124358, DATE_SUB(NOW(), INTERVAL 35 DAY), NOW(), 0, 6, 41, 500 FROM question WHERE id = 2 LIMIT 1;

-- 最长回文子串 (题目ID=3)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":25}', 2, 3, 1950582655286054916, DATE_SUB(NOW(), INTERVAL 48 DAY), NOW(), 0, 25, 42, 500 FROM question WHERE id = 3 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":48,"time":35}', 2, 3, 1951677564449124359, DATE_SUB(NOW(), INTERVAL 42 DAY), NOW(), 0, 35, 48, 500 FROM question WHERE id = 3 LIMIT 1;

-- 三数之和 (题目ID=4)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":45,"time":18}', 2, 4, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 45 DAY), NOW(), 0, 18, 45, 500 FROM question WHERE id = 4 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":44,"time":15}', 2, 4, 1951677564449124360, DATE_SUB(NOW(), INTERVAL 38 DAY), NOW(), 0, 15, 44, 500 FROM question WHERE id = 4 LIMIT 1;

-- 盛最多水的容器 (题目ID=5)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":43,"time":22}', 2, 5, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 40 DAY), NOW(), 0, 22, 43, 500 FROM question WHERE id = 5 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":40,"time":10}', 3, 5, 1951677564449124361, DATE_SUB(NOW(), INTERVAL 35 DAY), NOW(), 0, 10, 40, 500 FROM question WHERE id = 5 LIMIT 1;

-- 有效的括号 (题目ID=6)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":2}', 2, 6, 1950582655286054917, DATE_SUB(NOW(), INTERVAL 38 DAY), NOW(), 0, 2, 40, 500 FROM question WHERE id = 6 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":39,"time":1}', 2, 6, 1991841612048580610, DATE_SUB(NOW(), INTERVAL 32 DAY), NOW(), 0, 1, 39, 500 FROM question WHERE id = 6 LIMIT 1;

-- 合并两个有序链表 (题目ID=7)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":1}', 2, 7, 1951677564449124353, DATE_SUB(NOW(), INTERVAL 35 DAY), NOW(), 0, 1, 41, 500 FROM question WHERE id = 7 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":0}', 2, 7, 2030186024174137345, DATE_SUB(NOW(), INTERVAL 28 DAY), NOW(), 0, 0, 40, 500 FROM question WHERE id = 7 LIMIT 1;

-- 旋转数组 (题目ID=8)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":5}', 2, 8, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), 0, 5, 42, 500 FROM question WHERE id = 8 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":38,"time":3}', 3, 8, 1951677564449124355, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW(), 0, 3, 38, 500 FROM question WHERE id = 8 LIMIT 1;

-- 最小路径和 (题目ID=9)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":44,"time":15}', 2, 9, 1950582655286054916, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW(), 0, 15, 44, 500 FROM question WHERE id = 9 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":45,"time":18}', 2, 9, 1951677564449124358, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), 0, 18, 45, 500 FROM question WHERE id = 9 LIMIT 1;

-- 爬楼梯 (题目ID=10)
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":38,"time":0}', 2, 10, 1950582655286054917, DATE_SUB(NOW(), INTERVAL 22 DAY), NOW(), 0, 0, 38, 500 FROM question WHERE id = 10 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":37,"time":0}', 2, 10, 1951677564449124360, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW(), 0, 0, 37, 500 FROM question WHERE id = 10 LIMIT 1;

-- 近期提交
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":132}', 2, 1, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), 0, 132, 41, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":38,"time":3}', 3, 2, 1951677564449124354, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW(), 0, 3, 38, 500 FROM question WHERE id = 2 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":2}', 2, 6, 1991841612048580610, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW(), 0, 2, 41, 500 FROM question WHERE id = 6 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":128}', 2, 1, 2030186024174137345, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), 0, 128, 40, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":38,"time":0}', 2, 10, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0, 0, 38, 500 FROM question WHERE id = 10 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":145}', 2, 1, 1951677564449124356, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0, 145, 42, 500 FROM question WHERE id = 1 LIMIT 1;

INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":39,"time":0}', 2, 10, 1951677564449124357, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0, 0, 39, 500 FROM question WHERE id = 10 LIMIT 1;

-- 题目11-30提交数据（主用户：1950582655286054914 亚克）
-- 题目11
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":85}', 2, 11, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), 0, 85, 42, 500 FROM question WHERE id = 11 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":72}', 2, 11, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW(), 0, 72, 40, 500 FROM question WHERE id = 11 LIMIT 1;

-- 题目12
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":38,"time":55}', 2, 12, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), 0, 55, 38, 500 FROM question WHERE id = 12 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":36,"time":30}', 3, 12, 1951677564449124354, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW(), 0, 30, 36, 500 FROM question WHERE id = 12 LIMIT 1;

-- 题目13
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":44,"time":120}', 2, 13, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW(), 0, 120, 44, 500 FROM question WHERE id = 13 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":98}', 2, 13, 1950582655286054917, DATE_SUB(NOW(), INTERVAL 22 DAY), NOW(), 0, 98, 42, 500 FROM question WHERE id = 13 LIMIT 1;

-- 题目14
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":45}', 2, 14, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW(), 0, 45, 40, 500 FROM question WHERE id = 14 LIMIT 1;

-- 题目15
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":46,"time":180}', 2, 15, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), 0, 180, 46, 500 FROM question WHERE id = 15 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":44,"time":165}', 2, 15, 1991841612048580610, DATE_SUB(NOW(), INTERVAL 28 DAY), NOW(), 0, 165, 44, 500 FROM question WHERE id = 15 LIMIT 1;

-- 题目16
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":38,"time":25}', 2, 16, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), 0, 25, 38, 500 FROM question WHERE id = 16 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":36,"time":20}', 3, 16, 1951677564449124356, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW(), 0, 20, 36, 500 FROM question WHERE id = 16 LIMIT 1;

-- 题目17
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":95}', 2, 17, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), 0, 95, 42, 500 FROM question WHERE id = 17 LIMIT 1;

-- 题目18
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":68}', 2, 18, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW(), 0, 68, 40, 500 FROM question WHERE id = 18 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":39,"time":55}', 2, 18, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), 0, 55, 39, 500 FROM question WHERE id = 18 LIMIT 1;

-- 题目19
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":44,"time":145}', 2, 19, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 22 DAY), NOW(), 0, 145, 44, 500 FROM question WHERE id = 19 LIMIT 1;

-- 题目20
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":78}', 2, 20, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW(), 0, 78, 41, 500 FROM question WHERE id = 20 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":38,"time":45}', 3, 20, 2030186024174137345, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), 0, 45, 38, 500 FROM question WHERE id = 20 LIMIT 1;

-- 题目21-30（亚克为主，近期提交）
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":43,"time":88}', 2, 21, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 14 DAY), NOW(), 0, 88, 43, 500 FROM question WHERE id = 21 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":92}', 2, 22, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 13 DAY), NOW(), 0, 92, 42, 500 FROM question WHERE id = 22 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":45,"time":156}', 2, 23, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW(), 0, 156, 45, 500 FROM question WHERE id = 23 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":65}', 2, 24, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 11 DAY), NOW(), 0, 65, 40, 500 FROM question WHERE id = 24 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":38,"time":42}', 3, 25, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), 0, 42, 38, 500 FROM question WHERE id = 25 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":44,"time":110}', 2, 26, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 9 DAY), NOW(), 0, 110, 44, 500 FROM question WHERE id = 26 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":75}', 2, 27, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW(), 0, 75, 41, 500 FROM question WHERE id = 27 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":43,"time":98}', 2, 28, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW(), 0, 98, 43, 500 FROM question WHERE id = 28 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":46,"time":135}', 2, 29, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW(), 0, 135, 46, 500 FROM question WHERE id = 29 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":82}', 2, 30, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), 0, 82, 42, 500 FROM question WHERE id = 30 LIMIT 1;

-- 其他用户补充提交
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":41,"time":70}', 2, 21, 1950582655286054915, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), 0, 70, 41, 500 FROM question WHERE id = 21 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":39,"time":58}', 2, 25, 1950582655286054914, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 0, 58, 39, 500 FROM question WHERE id = 25 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":40,"time":62}', 2, 26, 1951677564449124353, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW(), 0, 62, 40, 500 FROM question WHERE id = 26 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Accepted","memory":42,"time":88}', 2, 28, 1991841612048580610, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), 0, 88, 42, 500 FROM question WHERE id = 28 LIMIT 1;
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete, executionTime, memoryUsage, codeLength)
SELECT 'java', IFNULL(answer, sourceCode), '{"message":"Wrong Answer","memory":38,"time":55}', 3, 30, 1950582655286054917, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0, 55, 38, 500 FROM question WHERE id = 30 LIMIT 1;

-- 更新题目统计
UPDATE question q
SET
    submitNum = (SELECT COUNT(*) FROM question_submit WHERE questionId = q.id AND isDelete = 0),
    acceptedNum = (SELECT COUNT(*) FROM question_submit WHERE questionId = q.id AND isDelete = 0 AND status = 2)
WHERE isDelete = 0;

-- 验证结果
SELECT
    q.id,
    q.title,
    q.submitNum,
    q.acceptedNum,
    CONCAT(ROUND(q.acceptedNum / NULLIF(q.submitNum, 0) * 100, 1), '%') as passRate
FROM question q
WHERE q.isDelete = 0
ORDER BY q.id;