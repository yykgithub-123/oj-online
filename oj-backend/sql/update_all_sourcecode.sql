USE yykoj;

-- 1. 两数之和 (已更新)
-- 2. 无重复字符的最长子串
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        String s = args[0];
        int result = lengthOfLongestSubstring(s);
        System.out.println(result);
    }

    public static int lengthOfLongestSubstring(String s) {
        // 用户需要补全的代码
        int[] chars = new int[128];
        int left = 0, right = 0;
        int maxLength = 0;
        
        while (right < s.length()) {
            char c = s.charAt(right);
            chars[c]++;
            
            while (chars[c] > 1) {
                chars[s.charAt(left)]--;
                left++;
            }
            
            maxLength = Math.max(maxLength, right - left + 1);
            right++;
        }
        
        return maxLength;
    }
}' WHERE id = 2;

-- 3. 最长回文子串
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

-- 4. 三数之和
UPDATE question SET sourceCode = 'import java.util.*;

class Main {
    public static void main(String[] args) {
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        List<List<Integer>> result = threeSum(nums);
        
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

-- 5. 盛最多水的容器
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
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

-- 6. 有效的括号
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

-- 7. 合并两个有序链表
UPDATE question SET sourceCode = 'class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Main {
    public static void main(String[] args) {
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

-- 8. 删除排序数组中的重复项
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        int result = removeDuplicates(nums);
        System.out.println(result);
        
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

-- 9. 旋转数组
UPDATE question SET sourceCode = 'import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[args.length - 1]);
        int[] nums = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        rotate(nums, k);
        
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

-- 10. 最小路径和
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        // 示例：[[1,3,1],[1,5,1],[4,2,1]]
        int[][] grid = {{1,3,1}, {1,5,1}, {4,2,1}};
        
        int result = minPathSum(grid);
        System.out.println(result);
    }

    public static int minPathSum(int[][] grid) {
        // 用户需要补全的代码
        int m = grid.length;
        int n = grid[0].length;
        
        for (int i = 1; i < m; i++) {
            grid[i][0] += grid[i-1][0];
        }
        
        for (int j = 1; j < n; j++) {
            grid[0][j] += grid[0][j-1];
        }
        
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                grid[i][j] += Math.min(grid[i-1][j], grid[i][j-1]);
            }
        }
        
        return grid[m-1][n-1];
    }
}' WHERE id = 10;

-- 11. 爬楼梯
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int result = climbStairs(n);
        System.out.println(result);
    }

    public static int climbStairs(int n) {
        // 用户需要补全的代码
        if (n <= 2) return n;
        
        int prev1 = 1, prev2 = 2;
        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
            prev1 = prev2;
            prev2 = current;
        }
        
        return prev2;
    }
}' WHERE id = 11;

-- 12. 二叉树的最大深度
UPDATE question SET sourceCode = 'class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Main {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        
        int result = maxDepth(root);
        System.out.println(result);
    }

    public static int maxDepth(TreeNode root) {
        // 用户需要补全的代码
        if (root == null) return 0;
        
        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);
        
        return Math.max(leftDepth, rightDepth) + 1;
    }
}' WHERE id = 12;

-- 13. 反转链表
UPDATE question SET sourceCode = 'class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Main {
    public static void main(String[] args) {
        ListNode head = buildList(new int[]{1,2,3,4,5});
        
        ListNode result = reverseList(head);
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

    public static ListNode reverseList(ListNode head) {
        // 用户需要补全的代码
        ListNode prev = null;
        ListNode current = head;
        
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        
        return prev;
    }
}' WHERE id = 13;

-- 14. 验证二叉搜索树
UPDATE question SET sourceCode = 'class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Main {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);
        
        boolean result = isValidBST(root);
        System.out.println(result);
    }

    public static boolean isValidBST(TreeNode root) {
        // 用户需要补全的代码
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private static boolean isValidBST(TreeNode node, long min, long max) {
        if (node == null) return true;
        
        if (node.val <= min || node.val >= max) return false;
        
        return isValidBST(node.left, min, node.val) && 
               isValidBST(node.right, node.val, max);
    }
}' WHERE id = 14;

-- 15. 二叉树的层序遍历
UPDATE question SET sourceCode = 'import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Main {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        
        List<List<Integer>> result = levelOrder(root);
        
        System.out.print("[");
        for (int i = 0; i < result.size(); i++) {
            System.out.print("[");
            List<Integer> level = result.get(i);
            for (int j = 0; j < level.size(); j++) {
                System.out.print(level.get(j));
                if (j < level.size() - 1) System.out.print(",");
            }
            System.out.print("]");
            if (i < result.size() - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static List<List<Integer>> levelOrder(TreeNode root) {
        // 用户需要补全的代码
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            
            result.add(level);
        }
        
        return result;
    }
}' WHERE id = 15;

-- 16. 单词搜索
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        char[][] board = {{"A","B","C","E"}, {"S","F","C","S"}, {"A","D","E","E"}};
        String word = "ABCCED";
        
        boolean result = exist(board, word);
        System.out.println(result);
    }

    public static boolean exist(char[][] board, String word) {
        // 用户需要补全的代码
        int m = board.length;
        int n = board[0].length;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (dfs(board, word, i, j, 0)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean dfs(char[][] board, String word, int i, int j, int index) {
        if (index == word.length()) return true;
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || 
            board[i][j] != word.charAt(index)) return false;
        
        char temp = board[i][j];
        board[i][j] = "#";
        
        boolean result = dfs(board, word, i+1, j, index+1) ||
                        dfs(board, word, i-1, j, index+1) ||
                        dfs(board, word, i, j+1, index+1) ||
                        dfs(board, word, i, j-1, index+1);
        
        board[i][j] = temp;
        return result;
    }
}' WHERE id = 16;

-- 17. 旋转链表
UPDATE question SET sourceCode = 'class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Main {
    public static void main(String[] args) {
        ListNode head = buildList(new int[]{1,2,3,4,5});
        int k = 2;
        
        ListNode result = rotateRight(head, k);
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

    public static ListNode rotateRight(ListNode head, int k) {
        // 用户需要补全的代码
        if (head == null || head.next == null || k == 0) return head;
        
        int length = 1;
        ListNode current = head;
        while (current.next != null) {
            current = current.next;
            length++;
        }
        
        k = k % length;
        if (k == 0) return head;
        
        current.next = head;
        
        for (int i = 0; i < length - k; i++) {
            current = current.next;
        }
        
        head = current.next;
        current.next = null;
        
        return head;
    }
}' WHERE id = 17;

-- 18. 最大子序和
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
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

-- 19. 最小栈
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

-- 20. 设计哈希映射
UPDATE question SET sourceCode = 'class MyHashMap {
    private static final int SIZE = 10000;
    private Node[] buckets;
    
    private static class Node {
        int key;
        int value;
        Node next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public MyHashMap() {
        buckets = new Node[SIZE];
    }
    
    public void put(int key, int value) {
        // 用户需要补全的代码
        int index = hash(key);
        Node current = buckets[index];
        
        while (current != null) {
            if (current.key == key) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        
        Node newNode = new Node(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
    }
    
    public int get(int key) {
        // 用户需要补全的代码
        int index = hash(key);
        Node current = buckets[index];
        
        while (current != null) {
            if (current.key == key) {
                return current.value;
            }
            current = current.next;
        }
        return -1;
    }
    
    public void remove(int key) {
        // 用户需要补全的代码
        int index = hash(key);
        Node current = buckets[index];
        Node prev = null;
        
        while (current != null) {
            if (current.key == key) {
                if (prev == null) {
                    buckets[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                return;
            }
            prev = current;
            current = current.next;
        }
    }
    
    private int hash(int key) {
        return key % SIZE;
    }
}

class Main {
    public static void main(String[] args) {
        MyHashMap map = new MyHashMap();
        map.put(1, 1);
        map.put(2, 2);
        System.out.println(map.get(1)); // 返回 1
        System.out.println(map.get(3)); // 返回 -1
        map.put(2, 1);
        System.out.println(map.get(2)); // 返回 1
        map.remove(2);
        System.out.println(map.get(2)); // 返回 -1
    }
}' WHERE id = 20;

-- 21. 打乱数组
UPDATE question SET sourceCode = 'import java.util.Random;

class Solution {
    private int[] original;
    private int[] shuffled;
    private Random random;
    
    public Solution(int[] nums) {
        this.original = nums.clone();
        this.shuffled = nums.clone();
        this.random = new Random();
    }
    
    public int[] reset() {
        // 用户需要补全的代码
        shuffled = original.clone();
        return shuffled;
    }
    
    public int[] shuffle() {
        // 用户需要补全的代码
        for (int i = shuffled.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = shuffled[i];
            shuffled[i] = shuffled[j];
            shuffled[j] = temp;
        }
        return shuffled;
    }
}

class Main {
    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        Solution solution = new Solution(nums);
        
        System.out.print("Shuffle: ");
        int[] shuffled = solution.shuffle();
        for (int num : shuffled) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        System.out.print("Reset: ");
        int[] reset = solution.reset();
        for (int num : reset) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}' WHERE id = 21;

-- 22. 寻找重复数
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        int result = findDuplicate(nums);
        System.out.println(result);
    }

    public static int findDuplicate(int[] nums) {
        // 用户需要补全的代码
        int slow = nums[0];
        int fast = nums[0];
        
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);
        
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        
        return slow;
    }
}' WHERE id = 22;

-- 23. 单词拆分
UPDATE question SET sourceCode = 'import java.util.*;

class Main {
    public static void main(String[] args) {
        String s = "leetcode";
        List<String> wordDict = Arrays.asList("leet", "code");
        
        boolean result = wordBreak(s, wordDict);
        System.out.println(result);
    }

    public static boolean wordBreak(String s, List<String> wordDict) {
        // 用户需要补全的代码
        Set<String> wordSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        
        return dp[s.length()];
    }
}' WHERE id = 23;

-- 24. 合并区间
UPDATE question SET sourceCode = 'import java.util.*;

class Main {
    public static void main(String[] args) {
        int[][] intervals = {{1,3}, {2,6}, {8,10}, {15,18}};
        
        int[][] result = merge(intervals);
        
        System.out.print("[");
        for (int i = 0; i < result.length; i++) {
            System.out.print("[" + result[i][0] + "," + result[i][1] + "]");
            if (i < result.length - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static int[][] merge(int[][] intervals) {
        // 用户需要补全的代码
        if (intervals.length <= 1) {
            return intervals;
        }
        
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];
        
        for (int i = 1; i < intervals.length; i++) {
            if (current[1] >= intervals[i][0]) {
                current[1] = Math.max(current[1], intervals[i][1]);
            } else {
                result.add(current);
                current = intervals[i];
            }
        }
        result.add(current);
        
        return result.toArray(new int[result.size()][]);
    }
}' WHERE id = 24;

-- 25. 最小覆盖子串
UPDATE question SET sourceCode = 'import java.util.*;

class Main {
    public static void main(String[] args) {
        String s = "ADOBECODEBANC";
        String t = "ABC";
        
        String result = minWindow(s, t);
        System.out.println(result);
    }

    public static String minWindow(String s, String t) {
        // 用户需要补全的代码
        if (s.length() == 0 || t.length() == 0) {
            return "";
        }
        
        Map<Character, Integer> targetCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
        }
        
        int left = 0, right = 0;
        int minLeft = 0, minLen = Integer.MAX_VALUE;
        int required = targetCount.size();
        int formed = 0;
        
        Map<Character, Integer> windowCount = new HashMap<>();
        
        while (right < s.length()) {
            char c = s.charAt(right);
            windowCount.put(c, windowCount.getOrDefault(c, 0) + 1);
            
            if (targetCount.containsKey(c) && 
                windowCount.get(c).equals(targetCount.get(c))) {
                formed++;
            }
            
            while (left <= right && formed == required) {
                c = s.charAt(left);
                
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minLeft = left;
                }
                
                windowCount.put(c, windowCount.get(c) - 1);
                if (targetCount.containsKey(c) && 
                    windowCount.get(c) < targetCount.get(c)) {
                    formed--;
                }
                left++;
            }
            right++;
        }
        
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minLeft, minLeft + minLen);
    }
}' WHERE id = 25;

-- 26. 全排列
UPDATE question SET sourceCode = 'import java.util.*;

class Main {
    public static void main(String[] args) {
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        List<List<Integer>> result = permute(nums);
        
        System.out.print("[");
        for (int i = 0; i < result.size(); i++) {
            System.out.print("[");
            List<Integer> perm = result.get(i);
            for (int j = 0; j < perm.size(); j++) {
                System.out.print(perm.get(j));
                if (j < perm.size() - 1) System.out.print(",");
            }
            System.out.print("]");
            if (i < result.size() - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static List<List<Integer>> permute(int[] nums) {
        // 用户需要补全的代码
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums);
        return result;
    }
    
    private static void backtrack(List<List<Integer>> result, List<Integer> temp, int[] nums) {
        if (temp.size() == nums.length) {
            result.add(new ArrayList<>(temp));
            return;
        }
        
        for (int i = 0; i < nums.length; i++) {
            if (temp.contains(nums[i])) continue;
            temp.add(nums[i]);
            backtrack(result, temp, nums);
            temp.remove(temp.size() - 1);
        }
    }
}' WHERE id = 26;

-- 27. 组队
UPDATE question SET sourceCode = 'import java.util.*;

class Main {
    public static void main(String[] args) {
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        List<List<Integer>> result = teamCombinations(nums);
        
        System.out.print("[");
        for (int i = 0; i < result.size(); i++) {
            System.out.print("[");
            List<Integer> team = result.get(i);
            for (int j = 0; j < team.size(); j++) {
                System.out.print(team.get(j));
                if (j < team.size() - 1) System.out.print(",");
            }
            System.out.print("]");
            if (i < result.size() - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static List<List<Integer>> teamCombinations(int[] nums) {
        // 用户需要补全的代码
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }
    
    private static void backtrack(List<List<Integer>> result, List<Integer> temp, int[] nums, int start) {
        if (temp.size() > 0) {
            result.add(new ArrayList<>(temp));
        }
        
        for (int i = start; i < nums.length; i++) {
            temp.add(nums[i]);
            backtrack(result, temp, nums, i + 1);
            temp.remove(temp.size() - 1);
        }
    }
}' WHERE id = 27;

-- 28. 二叉树的直径
UPDATE question SET sourceCode = 'class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Main {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        
        int result = diameterOfBinaryTree(root);
        System.out.println(result);
    }

    public static int diameterOfBinaryTree(TreeNode root) {
        // 用户需要补全的代码
        int[] maxDiameter = {0};
        maxDepth(root, maxDiameter);
        return maxDiameter[0];
    }
    
    private static int maxDepth(TreeNode node, int[] maxDiameter) {
        if (node == null) return 0;
        
        int leftDepth = maxDepth(node.left, maxDiameter);
        int rightDepth = maxDepth(node.right, maxDiameter);
        
        maxDiameter[0] = Math.max(maxDiameter[0], leftDepth + rightDepth);
        
        return Math.max(leftDepth, rightDepth) + 1;
    }
}' WHERE id = 28;

-- 29. 路径总和
UPDATE question SET sourceCode = 'class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Main {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(4);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(11);
        root.left.left.left = new TreeNode(7);
        root.left.left.right = new TreeNode(2);
        root.right.left = new TreeNode(13);
        root.right.right = new TreeNode(4);
        root.right.right.right = new TreeNode(1);
        
        int targetSum = 22;
        boolean result = hasPathSum(root, targetSum);
        System.out.println(result);
    }

    public static boolean hasPathSum(TreeNode root, int targetSum) {
        // 用户需要补全的代码
        if (root == null) return false;
        
        if (root.left == null && root.right == null) {
            return targetSum == root.val;
        }
        
        return hasPathSum(root.left, targetSum - root.val) || 
               hasPathSum(root.right, targetSum - root.val);
    }
}' WHERE id = 29;

-- 30. 寻找峰值
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        int result = findPeakElement(nums);
        System.out.println(result);
    }

    public static int findPeakElement(int[] nums) {
        // 用户需要补全的代码
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] > nums[mid + 1]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        
        return left;
    }
}' WHERE id = 30;

-- 31. 加一
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        int[] digits = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            digits[i] = Integer.parseInt(args[i]);
        }
        
        int[] result = plusOne(digits);
        
        System.out.print("[");
        for (int i = 0; i < result.length; i++) {
            System.out.print(result[i]);
            if (i < result.length - 1) System.out.print(",");
        }
        System.out.print("]");
    }

    public static int[] plusOne(int[] digits) {
        // 用户需要补全的代码
        int n = digits.length;
        
        for (int i = n - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;
                return digits;
            }
            digits[i] = 0;
        }
        
        int[] newDigits = new int[n + 1];
        newDigits[0] = 1;
        return newDigits;
    }
}' WHERE id = 31;

-- 32. 分发糖果
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        int[] ratings = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            ratings[i] = Integer.parseInt(args[i]);
        }
        
        int result = candy(ratings);
        System.out.println(result);
    }

    public static int candy(int[] ratings) {
        // 用户需要补全的代码
        int n = ratings.length;
        int[] candies = new int[n];
        Arrays.fill(candies, 1);
        
        // 从左到右遍历
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                candies[i] = candies[i - 1] + 1;
            }
        }
        
        // 从右到左遍历
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                candies[i] = Math.max(candies[i], candies[i + 1] + 1);
            }
        }
        
        int total = 0;
        for (int candy : candies) {
            total += candy;
        }
        
        return total;
    }
}' WHERE id = 32;

-- 33. 寻找最小值
UPDATE question SET sourceCode = 'class Main {
    public static void main(String[] args) {
        int[] nums = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            nums[i] = Integer.parseInt(args[i]);
        }
        
        int result = findMin(nums);
        System.out.println(result);
    }

    public static int findMin(int[] nums) {
        // 用户需要补全的代码
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return nums[left];
    }
}' WHERE id = 33; 