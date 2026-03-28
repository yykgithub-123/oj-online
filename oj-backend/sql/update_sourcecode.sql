-- 更新题目sourceCode字段的脚本

-- 1. 两数之和
UPDATE question SET sourceCode = 'import java.util.HashMap;
import java.util.Map;

class Main {
    public static void main(String[] args) {
        // 解析 `nums` 数组和 `target`
        int target = Integer.parseInt(args[0]);
        int[] nums = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            nums[i] = Integer.parseInt(args[i + 1]);
        }

        // 调用 twoSum 方法
        int[] result = twoSum(nums, target);

        // 打印结果
        System.out.print("[");
        for (int i = 0; i < result.length; i++) {
            System.out.print(result[i]);
            if (i < result.length - 1) {
                System.out.print(",");
            }
        }
        System.out.print("]");
    }

    public static int[] twoSum(int[] nums, int target) {
        // 用户需要补全的代码
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }
}' WHERE id = 1;

-- 2. 最长回文子串
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        String s = args[0];
        String result = longestPalindrome(s);
        System.out.println(result);
    }

    public static String longestPalindrome(String s) {
        // 用户需要补全的代码
        if (s == null || s.length() < 2) {
            return s;
        }
        
        int start = 0, maxLength = 1;
        for (int i = 0; i < s.length(); i++) {
            int len1 = expandAroundCenter(s, i, i);
            int len2 = expandAroundCenter(s, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > maxLength) {
                start = i - (len - 1) / 2;
                maxLength = len;
            }
        }
        return s.substring(start, start + maxLength);
    }
    
    private static int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }
}' WHERE id = 3;

-- 3. 三数之和
UPDATE question SET sourceCode = 'import java.util.*;

class Main {
    public static void main(String[] args) {
        // 解析输入数组
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        List<List<Integer>> result = threeSum(nums);
        
        // 打印结果
        System.out.print("[");
        for (int i = 0; i < result.size(); i++) {
            System.out.print("[");
            List<Integer> triplet = result.get(i);
            for (int j = 0; j < triplet.size(); j++) {
                System.out.print(triplet.get(j));
                if (j < triplet.size() - 1) System.out.print(",");
            }
            System.out.print("]");
            if (i < result.size() - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static List<List<Integer>> threeSum(int[] nums) {
        // 用户需要补全的代码
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 3) {
            return result;
        }
        
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++;
                    right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return result;
    }
}' WHERE id = 4;

-- 4. 盛最多水的容器
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        // 解析输入数组
        int[] height = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            height[i] = Integer.parseInt(args[i]);
        }
        
        int result = maxArea(height);
        System.out.println(result);
    }

    public static int maxArea(int[] height) {
        // 用户需要补全的代码
        int maxArea = 0;
        int left = 0, right = height.length - 1;
        
        while (left < right) {
            int area = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, area);
            
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return maxArea;
    }
}' WHERE id = 5;

-- 5. 有效的括号
UPDATE question SET sourceCode = 'import java.util.Stack;

class Main {
    public static void main(String[] args) {
        String s = args[0];
        boolean result = isValid(s);
        System.out.println(result);
    }

    public static boolean isValid(String s) {
        // 用户需要补全的代码
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == "(" || c == "{" || c == "[") {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((c == ")" && top != "(") || 
                    (c == "}" && top != "{") || 
                    (c == "]" && top != "[")) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}' WHERE id = 6;

-- 6. 合并两个有序链表
UPDATE question SET sourceCode = 'class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Main {
    public static void main(String[] args) {
        // 这里需要根据输入构建链表
        // 示例：假设输入是两个有序数组
        ListNode l1 = buildList(new int[]{1,2,4});
        ListNode l2 = buildList(new int[]{1,3,4});
        
        ListNode result = mergeTwoLists(l1, l2);
        printList(result);
    }
    
    private static ListNode buildList(int[] arr) {
        if (arr.length == 0) return null;
        ListNode head = new ListNode(arr[0]);
        ListNode current = head;
        for (int i = 1; i < arr.length; i++) {
            current.next = new ListNode(arr[i]);
            current = current.next;
        }
        return head;
    }
    
    private static void printList(ListNode head) {
        System.out.print("[");
        while (head != null) {
            System.out.print(head.val);
            if (head.next != null) System.out.print(",");
            head = head.next;
        }
        System.out.print("]");
    }

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        // 用户需要补全的代码
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }
        
        current.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }
}' WHERE id = 7;

-- 7. 删除排序数组中的重复项
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        // 解析输入数组
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        int result = removeDuplicates(nums);
        System.out.println(result);
        
        // 打印修改后的数组
        System.out.print("[");
        for (int i = 0; i < result; i++) {
            System.out.print(nums[i]);
            if (i < result - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static int removeDuplicates(int[] nums) {
        // 用户需要补全的代码
        if (nums.length == 0) return 0;
        
        int k = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                nums[k] = nums[i];
                k++;
            }
        }
        return k;
    }
}' WHERE id = 8;

-- 8. 旋转数组
UPDATE question SET sourceCode = 'import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        // 解析输入：最后一个参数是k，其他是数组元素
        int k = Integer.parseInt(args[args.length - 1]);
        int[] nums = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        rotate(nums, k);
        
        // 打印结果
        System.out.print("[");
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i]);
            if (i < nums.length - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static void rotate(int[] nums, int k) {
        // 用户需要补全的代码
        int n = nums.length;
        k = k % n;
        
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }
    
    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }
}' WHERE id = 9;

-- 9. 最大子序和
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        // 解析输入数组
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        int result = maxSubArray(nums);
        System.out.println(result);
    }

    public static int maxSubArray(int[] nums) {
        // 用户需要补全的代码
        int maxSum = nums[0];
        int currentSum = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }
        
        return maxSum;
    }
}' WHERE id = 18;

-- 10. 最小栈
UPDATE question SET sourceCode = 'import java.util.Stack;

class MinStack {
    private Stack<Integer> stack;
    private Stack<Integer> minStack;
    
    public MinStack() {
        stack = new Stack<>();
        minStack = new Stack<>();
    }
    
    public void push(int val) {
        // 用户需要补全的代码
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        }
    }
    
    public void pop() {
        // 用户需要补全的代码
        if (stack.pop().equals(minStack.peek())) {
            minStack.pop();
        }
    }
    
    public int top() {
        // 用户需要补全的代码
        return stack.peek();
    }
    
    public int getMin() {
        // 用户需要补全的代码
        return minStack.peek();
    }
}

class Main {
    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println(minStack.getMin()); // 返回 -3
        minStack.pop();
        System.out.println(minStack.top());    // 返回 0
        System.out.println(minStack.getMin()); // 返回 -2
    }
}' WHERE id = 19; 