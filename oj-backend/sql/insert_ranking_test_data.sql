-- 插入排行榜测试数据
USE yykoj;

-- 首先确保有一些题目数据
INSERT IGNORE INTO question (id, title, content, tags, answer, submitNum, acceptedNum, judgeCase, judgeConfig, thumbNum, favourNum, userId, createTime, updateTime, isDelete, difficulty) VALUES
(1, '两数之和', '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那 两个 整数，并返回它们的数组下标。', '["数组", "哈希表"]', 'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> map = new HashMap<>();\n        for (int i = 0; i < nums.length; i++) {\n            int complement = target - nums[i];\n            if (map.containsKey(complement)) {\n                return new int[] { map.get(complement), i };\n            }\n            map.put(nums[i], i);\n        }\n        throw new IllegalArgumentException("No two sum solution");\n    }\n}', 150, 120, '[{"input": "[2,7,11,15], 9", "output": "[0,1]"}]', '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 1000}', 0, 0, 1950582655286054914, NOW(), NOW(), 0, '简单'),
(2, '整数反转', '给你一个 32 位的有符号整数 x ，返回将 x 中的数字部分反转后的结果。', '["数学"]', 'class Solution {\n    public int reverse(int x) {\n        int rev = 0;\n        while (x != 0) {\n            int pop = x % 10;\n            x /= 10;\n            if (rev > Integer.MAX_VALUE/10 || (rev == Integer.MAX_VALUE / 10 && pop > 7)) return 0;\n            if (rev < Integer.MIN_VALUE/10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) return 0;\n            rev = rev * 10 + pop;\n        }\n        return rev;\n    }\n}', 200, 150, '[{"input": "123", "output": "321"}]', '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 1000}', 0, 0, 1950582655286054914, NOW(), NOW(), 0, '简单'),
(3, '回文数', '给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。', '["数学"]', 'class Solution {\n    public boolean isPalindrome(int x) {\n        if (x < 0) return false;\n        int original = x;\n        int reversed = 0;\n        while (x != 0) {\n            reversed = reversed * 10 + x % 10;\n            x /= 10;\n        }\n        return original == reversed;\n    }\n}', 180, 140, '[{"input": "121", "output": "true"}]', '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 1000}', 0, 0, 1950582655286054914, NOW(), NOW(), 0, '简单');

-- 插入题目提交记录，让用户有不同的解题数量
-- 用户1 (亚克) - 总共解决了5题，本周解决了2题
INSERT INTO question_submit (id, language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete) VALUES
-- 总榜题目
(1, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 100}', 2, 1, 1950582655286054914, '2025-07-25 10:00:00', '2025-07-25 10:00:00', 0),
(2, 'java', 'class Solution { public int reverse(int x) { return 321; } }', '{"message": "Accepted", "memory": 256, "time": 120}', 2, 2, 1950582655286054914, '2025-07-26 11:00:00', '2025-07-26 11:00:00', 0),
(3, 'java', 'class Solution { public boolean isPalindrome(int x) { return true; } }', '{"message": "Accepted", "memory": 256, "time": 90}', 2, 3, 1950582655286054914, '2025-07-27 12:00:00', '2025-07-27 12:00:00', 0),
(4, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 110}', 2, 1, 1950582655286054914, '2025-07-28 13:00:00', '2025-07-28 13:00:00', 0),
(5, 'java', 'class Solution { public int reverse(int x) { return 321; } }', '{"message": "Accepted", "memory": 256, "time": 130}', 2, 2, 1950582655286054914, '2025-07-29 14:00:00', '2025-07-29 14:00:00', 0),
-- 本周题目 (最近7天)
(6, 'java', 'class Solution { public boolean isPalindrome(int x) { return true; } }', '{"message": "Accepted", "memory": 256, "time": 95}', 2, 3, 1950582655286054914, '2025-07-31 15:00:00', '2025-07-31 15:00:00', 0),
(7, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 105}', 2, 1, 1950582655286054914, '2025-08-01 16:00:00', '2025-08-01 16:00:00', 0),

-- 用户2 (test1) - 总共解决了3题，本周解决了1题
(8, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 150}', 2, 1, 1950582655286054915, '2025-07-20 10:00:00', '2025-07-20 10:00:00', 0),
(9, 'java', 'class Solution { public int reverse(int x) { return 321; } }', '{"message": "Accepted", "memory": 256, "time": 140}', 2, 2, 1950582655286054915, '2025-07-22 11:00:00', '2025-07-22 11:00:00', 0),
(10, 'java', 'class Solution { public boolean isPalindrome(int x) { return true; } }', '{"message": "Accepted", "memory": 256, "time": 160}', 2, 3, 1950582655286054915, '2025-07-24 12:00:00', '2025-07-24 12:00:00', 0),
-- 本周题目
(11, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 145}', 2, 1, 1950582655286054915, '2025-08-01 10:00:00', '2025-08-01 10:00:00', 0),

-- 用户3 (test2) - 总共解决了4题，本周解决了3题
(12, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 120}', 2, 1, 1950582655286054916, '2025-07-15 10:00:00', '2025-07-15 10:00:00', 0),
(13, 'java', 'class Solution { public int reverse(int x) { return 321; } }', '{"message": "Accepted", "memory": 256, "time": 135}', 2, 2, 1950582655286054916, '2025-07-18 11:00:00', '2025-07-18 11:00:00', 0),
-- 本周题目
(14, 'java', 'class Solution { public boolean isPalindrome(int x) { return true; } }', '{"message": "Accepted", "memory": 256, "time": 125}', 2, 3, 1950582655286054916, '2025-07-30 12:00:00', '2025-07-30 12:00:00', 0),
(15, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 115}', 2, 1, 1950582655286054916, '2025-07-31 13:00:00', '2025-07-31 13:00:00', 0),
(16, 'java', 'class Solution { public int reverse(int x) { return 321; } }', '{"message": "Accepted", "memory": 256, "time": 140}', 2, 2, 1950582655286054916, '2025-08-01 14:00:00', '2025-08-01 14:00:00', 0),

-- 用户4 (test3) - 总共解决了2题，本周解决了0题
(17, 'java', 'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0,1}; } }', '{"message": "Accepted", "memory": 256, "time": 180}', 2, 1, 1950582655286054917, '2025-07-10 10:00:00', '2025-07-10 10:00:00', 0),
(18, 'java', 'class Solution { public int reverse(int x) { return 321; } }', '{"message": "Accepted", "memory": 256, "time": 170}', 2, 2, 1950582655286054917, '2025-07-12 11:00:00', '2025-07-12 11:00:00', 0);

-- 验证数据插入
SELECT '=== 用户统计 ===' as info;
SELECT u.userName, 
       COUNT(DISTINCT qs.questionId) as total_solved,
       COUNT(DISTINCT CASE WHEN qs.createTime >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN qs.questionId END) as weekly_solved
FROM user u 
LEFT JOIN question_submit qs ON u.id = qs.userId AND qs.status = 2 AND qs.isDelete = 0
WHERE u.isDelete = 0
GROUP BY u.id, u.userName
ORDER BY total_solved DESC;

SELECT '=== 题目提交记录统计 ===' as info;
SELECT COUNT(*) as total_submissions FROM question_submit WHERE isDelete = 0;
SELECT COUNT(*) as accepted_submissions FROM question_submit WHERE status = 2 AND isDelete = 0;