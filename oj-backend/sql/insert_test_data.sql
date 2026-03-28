-- 插入测试数据
use yykoj;

-- 1. 插入测试用户数据
INSERT INTO user (userAccount, userPassword, userName, userAvatar, userProfile, userRole) VALUES
('testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '张三', 'https://img2.baidu.com/it/u=1978192862,2048448374&fm=253&fmt=auto&app=138&f=JPEG?w=504&h=500', '热爱编程的学生', 'user'),
('testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '李四', 'https://img1.baidu.com/it/u=1319916725,2965241492&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '算法爱好者', 'user'),
('testuser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '王五', 'https://img0.baidu.com/it/u=2028084904,3939052004&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '数据结构专家', 'user'),
('testuser4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '赵六', 'https://img2.baidu.com/it/u=2370931438,70387529&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '编程新手', 'user'),
('testuser5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '孙七', 'https://img1.baidu.com/it/u=3602773692,1512483864&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', 'ACM竞赛选手', 'user'),
('testuser6', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '周八', 'https://img0.baidu.com/it/u=1956849365,4188387259&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '后端开发工程师', 'user'),
('testuser7', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '吴九', 'https://img2.baidu.com/it/u=4058492800,2098622842&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '前端开发者', 'user'),
('testuser8', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '郑十', 'https://img1.baidu.com/it/u=2133967142,2432746710&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '全栈开发者', 'user'),
('testuser9', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '陈十一', 'https://img0.baidu.com/it/u=1732966997,2222092669&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '机器学习工程师', 'user'),
('testuser10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyYx6rUwFOaKvBqKk5OjgZUvrIK', '刘十二', 'https://img2.baidu.com/it/u=3967712259,2048448374&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '系统架构师', 'user');

-- 2. 插入测试题目数据
INSERT INTO question (title, content, tags, answer, submitNum, acceptedNum, judgeCase, judgeConfig, userId) VALUES
('两数之和', '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。', '["数组", "哈希表"]', 'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> map = new HashMap<>();\n        for (int i = 0; i < nums.length; i++) {\n            int complement = target - nums[i];\n            if (map.containsKey(complement)) {\n                return new int[] { map.get(complement), i };\n            }\n            map.put(nums[i], i);\n        }\n        return new int[0];\n    }\n}', 150, 120, '[{"input": "[2,7,11,15]\\n9", "output": "[0,1]"}, {"input": "[3,2,4]\\n6", "output": "[1,2]"}]', '{"timeLimit": 1000, "memoryLimit": 256}', 1),
('无重复字符的最长子串', '给定一个字符串 s ，请你找出其中不含有重复字符的最长子串的长度。', '["哈希表", "字符串", "滑动窗口"]', 'class Solution {\n    public int lengthOfLongestSubstring(String s) {\n        Set<Character> set = new HashSet<>();\n        int left = 0, maxLen = 0;\n        for (int right = 0; right < s.length(); right++) {\n            while (set.contains(s.charAt(right))) {\n                set.remove(s.charAt(left++));\n            }\n            set.add(s.charAt(right));\n            maxLen = Math.max(maxLen, right - left + 1);\n        }\n        return maxLen;\n    }\n}', 200, 160, '[{"input": "abcabcbb", "output": "3"}, {"input": "bbbbb", "output": "1"}]', '{"timeLimit": 1000, "memoryLimit": 256}', 1),
('最长回文子串', '给你一个字符串 s，找到 s 中最长的回文子串。', '["字符串", "动态规划"]', 'class Solution {\n    public String longestPalindrome(String s) {\n        if (s == null || s.length() < 2) return s;\n        int start = 0, maxLen = 1;\n        for (int i = 0; i < s.length(); i++) {\n            int len1 = expandAroundCenter(s, i, i);\n            int len2 = expandAroundCenter(s, i, i + 1);\n            int len = Math.max(len1, len2);\n            if (len > maxLen) {\n                maxLen = len;\n                start = i - (len - 1) / 2;\n            }\n        }\n        return s.substring(start, start + maxLen);\n    }\n}', 250, 200, '[{"input": "babad", "output": "bab"}, {"input": "cbbd", "output": "bb"}]', '{"timeLimit": 1000, "memoryLimit": 256}', 1),
('三数之和', '给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有和为 0 且不重复的三元组。', '["数组", "双指针", "排序"]', 'class Solution {\n    public List<List<Integer>> threeSum(int[] nums) {\n        List<List<Integer>> result = new ArrayList<>();\n        Arrays.sort(nums);\n        for (int i = 0; i < nums.length - 2; i++) {\n            if (i > 0 && nums[i] == nums[i-1]) continue;\n            int left = i + 1, right = nums.length - 1;\n            while (left < right) {\n                int sum = nums[i] + nums[left] + nums[right];\n                if (sum == 0) {\n                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));\n                    while (left < right && nums[left] == nums[left+1]) left++;\n                    while (left < right && nums[right] == nums[right-1]) right--;\n                    left++; right--;\n                } else if (sum < 0) left++;\n                else right--;\n            }\n        }\n        return result;\n    }\n}', 300, 240, '[{"input": "[-1,0,1,2,-1,-4]", "output": "[[-1,-1,2],[-1,0,1]]"}]', '{"timeLimit": 1000, "memoryLimit": 256}', 1),
('盛最多水的容器', '给定一个长度为 n 的整数数组 height 。有 n 条垂线，第 i 条线的两个端点是 (i, 0) 和 (i, height[i]) 。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。', '["贪心", "数组", "双指针"]', 'class Solution {\n    public int maxArea(int[] height) {\n        int left = 0, right = height.length - 1;\n        int maxArea = 0;\n        while (left < right) {\n            int area = Math.min(height[left], height[right]) * (right - left);\n            maxArea = Math.max(maxArea, area);\n            if (height[left] < height[right]) {\n                left++;\n            } else {\n                right--;\n            }\n        }\n        return maxArea;\n    }\n}', 180, 144, '[{"input": "[1,8,6,2,5,4,8,3,7]", "output": "49"}]', '{"timeLimit": 1000, "memoryLimit": 256}', 1);

-- 3. 插入题目提交数据（模拟不同用户的提交记录）
-- 用户1的提交记录（高手用户，通过率高）
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime) VALUES
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 15, "memory": 38.4}', 2, 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 25, "memory": 42.1}', 2, 2, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('java', 'class Solution { public String longestPalindrome(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 35, "memory": 45.2}', 2, 3, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('java', 'class Solution { public List<List<Integer>> threeSum(int[] nums) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 45, "memory": 48.3}', 2, 4, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('java', 'class Solution { public int maxArea(int[] height) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 20, "memory": 40.5}', 2, 5, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 用户2的提交记录（中等水平用户）
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 18, "memory": 39.2}', 2, 1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 错误代码 */ } }', '{"message": "Wrong Answer", "time": 0, "memory": 0}', 3, 2, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 30, "memory": 43.5}', 2, 2, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('java', 'class Solution { public String longestPalindrome(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 40, "memory": 46.8}', 2, 3, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('java', 'class Solution { public List<List<Integer>> threeSum(int[] nums) { /* 错误代码 */ } }', '{"message": "Time Limit Exceeded", "time": 1000, "memory": 50.0}', 3, 4, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- 用户3的提交记录（专家级用户）
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 12, "memory": 37.8}', 2, 1, 3, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 22, "memory": 41.2}', 2, 2, 3, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('java', 'class Solution { public String longestPalindrome(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 28, "memory": 44.1}', 2, 3, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('java', 'class Solution { public List<List<Integer>> threeSum(int[] nums) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 38, "memory": 47.5}', 2, 4, 3, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('java', 'class Solution { public int maxArea(int[] height) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 16, "memory": 39.8}', 2, 5, 3, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 用户4的提交记录（新手用户）
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 错误代码 */ } }', '{"message": "Compilation Error", "time": 0, "memory": 0}', 3, 1, 4, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 25, "memory": 40.2}', 2, 1, 4, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 错误代码 */ } }', '{"message": "Wrong Answer", "time": 0, "memory": 0}', 3, 2, 4, DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- 用户5的提交记录（ACM选手）
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 10, "memory": 36.5}', 2, 1, 5, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 18, "memory": 40.8}', 2, 2, 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('java', 'class Solution { public String longestPalindrome(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 25, "memory": 43.2}', 2, 3, 5, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('java', 'class Solution { public List<List<Integer>> threeSum(int[] nums) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 32, "memory": 46.8}', 2, 4, 5, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('java', 'class Solution { public int maxArea(int[] height) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 14, "memory": 38.9}', 2, 5, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 用户6的提交记录
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 20, "memory": 39.5}', 2, 1, 6, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 28, "memory": 42.8}', 2, 2, 6, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('java', 'class Solution { public String longestPalindrome(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 38, "memory": 45.5}', 2, 3, 6, DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- 用户7的提交记录
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 22, "memory": 40.1}', 2, 1, 7, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 32, "memory": 44.2}', 2, 2, 7, DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- 用户8的提交记录
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 19, "memory": 38.8}', 2, 1, 8, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 错误代码 */ } }', '{"message": "Runtime Error", "time": 0, "memory": 0}', 3, 2, 8, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('java', 'class Solution { public int lengthOfLongestSubstring(String s) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 35, "memory": 45.1}', 2, 2, 8, DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- 用户9的提交记录
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 17, "memory": 38.2}', 2, 1, 9, DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 用户10的提交记录
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 错误代码 */ } }', '{"message": "Wrong Answer", "time": 0, "memory": 0}', 3, 1, 10, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('java', 'class Solution { public int[] twoSum(int[] nums, int target) { /* 正确代码 */ } }', '{"message": "Accepted", "time": 30, "memory": 42.5}', 2, 1, 10, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 添加一些本周的提交数据（用于周榜统计）
INSERT INTO question_submit (language, code, judgeInfo, status, questionId, userId, createTime) VALUES
('java', 'class Solution { /* 本周提交1 */ }', '{"message": "Accepted", "time": 15, "memory": 38.4}', 2, 1, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('java', 'class Solution { /* 本周提交2 */ }', '{"message": "Accepted", "time": 20, "memory": 40.2}', 2, 2, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('java', 'class Solution { /* 本周提交3 */ }', '{"message": "Accepted", "time": 18, "memory": 39.1}', 2, 1, 2, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('java', 'class Solution { /* 本周提交4 */ }', '{"message": "Accepted", "time": 25, "memory": 41.5}', 2, 3, 3, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
('java', 'class Solution { /* 本周提交5 */ }', '{"message": "Accepted", "time": 22, "memory": 40.8}', 2, 2, 5, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
('java', 'class Solution { /* 本周提交6 */ }', '{"message": "Wrong Answer", "time": 0, "memory": 0}', 3, 4, 4, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('java', 'class Solution { /* 本周提交7 */ }', '{"message": "Accepted", "time": 28, "memory": 42.3}', 2, 1, 6, DATE_SUB(NOW(), INTERVAL 7 HOUR));

-- 更新题目的提交数和通过数统计
UPDATE question SET submitNum = (SELECT COUNT(*) FROM question_submit WHERE questionId = question.id);
UPDATE question SET acceptedNum = (SELECT COUNT(*) FROM question_submit WHERE questionId = question.id AND status = 2);

SELECT '数据插入完成！' as message;