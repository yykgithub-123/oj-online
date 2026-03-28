<template>
  <div id="questionsView">
    <div class="main-container">
      <!-- 左侧主要内容 -->
      <div class="left-content">
        <!-- 筛选条件 -->
        <div class="filter-section">
          <div class="filter-tabs">
            <span 
              class="filter-tab" 
              :class="{ active: activeFilter === 'tags' }"
              @click="setActiveFilter('tags')"
            >
              标签
            </span>
            <span 
              class="filter-tab" 
              :class="{ active: activeFilter === 'difficulty' }"
              @click="setActiveFilter('difficulty')"
            >
              难度
            </span>
            <span 
              class="filter-tab" 
              :class="{ active: activeFilter === 'official' }"
              @click="setActiveFilter('official')"
            >
              官方题解
            </span>
            <span 
              class="filter-tab" 
              :class="{ active: activeFilter === 'number' }"
              @click="setActiveFilter('number')"
            >
              题目编号
            </span>
          </div>
          <div class="filter-search">
            <a-input 
              v-model="searchKeyword" 
              :placeholder="getSearchPlaceholder()"
              class="search-input"
              @press-enter="handleSearch"
            >
              <template #suffix>
                <a-icon type="search" />
              </template>
            </a-input>
            <a-button 
              type="outline" 
              size="small" 
              class="clear-filter-btn"
              @click="clearAllFilters"
              :disabled="!hasActiveFilters"
            >
              <a-icon type="clear" />
              清除筛选
            </a-button>
          </div>
        </div>

        <!-- 筛选选项区域 -->
        <div class="filter-options" v-if="activeFilter !== 'number'">
          <!-- 标签筛选 -->
          <div v-if="activeFilter === 'tags'" class="tag-filters">
            <span 
              v-for="tag in popularTags" 
              :key="tag"
              class="filter-tag"
              :class="{ active: selectedTags.includes(tag) }"
              @click="toggleTag(tag)"
            >
              {{ tag }}
            </span>
          </div>
          
          <!-- 难度筛选 -->
          <div v-if="activeFilter === 'difficulty'" class="difficulty-filters">
            <span 
              v-for="diff in difficulties" 
              :key="diff.value"
              class="filter-difficulty"
              :class="{ active: selectedDifficulty === diff.value, [diff.class]: true }"
              @click="selectDifficulty(diff.value)"
            >
              {{ diff.label }}
            </span>
          </div>
          
          <!-- 官方题解筛选 -->
          <div v-if="activeFilter === 'official'" class="official-filters">
            <span 
              class="filter-official"
              :class="{ active: hasOfficialSolution === true }"
              @click="setOfficialSolution(true)"
            >
              有官方题解
            </span>
            <span 
              class="filter-official"
              :class="{ active: hasOfficialSolution === false }"
              @click="setOfficialSolution(false)"
            >
              无官方题解
            </span>
          </div>
        </div>

        <!-- 题目列表 -->
        <div class="question-list">
          <div class="list-header">
            <div class="header-item">编号</div>
            <div class="header-item">题目</div>
            <div class="header-item">难度</div>
            <div class="header-item">通过率</div>
            <div class="header-item">挑战人数</div>
            <div class="header-item">操作</div>
          </div>
          
          <div class="list-body">
            <div 
              v-for="question in dataList" 
              :key="question.id" 
              class="question-item"
            >
              <div class="item-number">{{ question.id }}</div>
              <div class="item-title">
                <div class="title-main">{{ question.title }}</div>
                <div class="title-tags">
                  <span class="tag new">NEW</span>
                  <span 
                    v-for="tag in parseTags(question.tags).slice(0, 3)" 
                    :key="tag"
                    class="tag"
                    :class="getTagClass(tag)"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>
              <div class="item-difficulty">
                <span 
                  class="difficulty-badge" 
                  :class="getDifficultyClass(question.difficulty)"
                >
                  {{ getDifficultyText(question.difficulty) }}
                </span>
              </div>
              <div class="item-rate">{{ calculateRate(question.acceptedNum, question.submitNum) }}%</div>
              <div class="item-count">{{ question.submitNum || 0 }}</div>
              <div class="item-action">
                <a-button 
                  type="primary" 
                  size="small" 
                  @click="toQuestionPage(question)"
                  class="challenge-btn"
                >
                  开始挑战
                </a-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <a-pagination
            :current="searchParams.current"
            :page-size="searchParams.pageSize"
            :total="Number(total)"
            :show-total="true"
            @change="onPageChange"
            size="small"
          />
        </div>
      </div>

      <!-- 右侧边栏 -->
      <div class="right-sidebar">
        <!-- 打卡日历 -->
        <div class="calendar-card">
          <div class="card-header">
            <span class="card-title">打卡日历</span>
            <div class="month-selector">
              <span class="month-btn" @click="prevMonth">&lt;</span>
              <span class="month-info">{{ currentMonth }}</span>
              <span class="month-btn" @click="nextMonth">&gt;</span>
            </div>
          </div>
          <div class="calendar-grid">
            <div class="calendar-header">
              <span>日</span><span>一</span><span>二</span><span>三</span><span>四</span><span>五</span><span>六</span>
            </div>
            <div class="calendar-body">
              <div 
                v-for="day in calendarDays" 
                :key="day.date"
                class="calendar-day"
                :class="{ 
                  'today': day.isToday, 
                  'checked': day.isChecked,
                  'other-month': day.isOtherMonth 
                }"
              >
                {{ day.day }}
              </div>
            </div>
          </div>
          <div class="calendar-footer">
            <span class="check-info">打卡提醒</span>
          </div>
        </div>

        <!-- 排行榜 -->
        <div class="practice-card">
          <div class="card-header">
            <span class="card-title">排行榜</span>
            <div class="practice-actions">
              <a-button 
                size="small" 
                :type="rankingType === 'week' ? 'primary' : 'text'"
                @click="switchRankingType('week')"
              >
                本周
              </a-button>
              <a-button 
                size="small" 
                :type="rankingType === 'total' ? 'primary' : 'text'"
                @click="switchRankingType('total')"
              >
                总榜
              </a-button>
            </div>
          </div>
          <div class="practice-content">
            <div class="practice-status">
              <span class="status-text">我已完成</span>
              <span class="status-count">{{ currentUserStats?.totalSolved || '--' }}</span>
            </div>
            
            <!-- 排行榜列表 -->
            <div class="ranking-list" v-loading="rankingLoading">
              <div 
                v-for="(user, index) in rankingList" 
                :key="user.userId"
                class="ranking-item"
                :class="{ 'current-user': user.userId === currentUserId }"
              >
                <!-- 排名 -->
                <div class="rank-number">
                  <span v-if="index < 3" class="medal" :class="getMedalClass(index)">
                    {{ index + 1 }}
                  </span>
                  <span v-else class="rank-text">{{ index + 1 }}</span>
                </div>
                
                <!-- 用户头像 -->
                <div class="user-avatar">
                  <img 
                    :src="user.userAvatar || '/yake.webp'" 
                    :alt="user.userName"
                    @error="handleAvatarError"
                  />
                </div>
                
                <!-- 用户信息 -->
                <div class="user-info">
                  <div class="user-name">{{ user.userName }}</div>
                </div>
                
                <!-- 题目数量 -->
                <div class="user-score">
                  <span class="score-number">
                    {{ rankingType === 'week' ? (user.weeklySolved || 0) : (user.totalSolved || 0) }}题
                  </span>
                </div>
              </div>
              
              <!-- 空状态 -->
              <div v-if="!rankingLoading && rankingList.length === 0" class="empty-state">
                <div class="empty-text">暂无排行榜数据</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 客服按钮 -->
        <div class="service-buttons">
          <div class="service-btn" @click="goToMembership">
            <icon-gift />
            <span>开通会员</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute, useRouter } from "vue-router";
import { useStore } from "vuex";
import { IconGift } from '@arco-design/web-vue/es/icon';
import { getUserRankingList, getWeeklyUserRankingList, getUserStats } from "@/api/userController";
import { getUserDailyActivity } from "@/api/questionSubmitController";

const loading = ref(false);
const searchKeyword = ref("");
const dataList = ref([]);
const total = ref(0);
const activeFilter = ref("tags");
const selectedTags = ref([]);
const selectedDifficulty = ref("");
const hasOfficialSolution = ref(null);

// 排行榜相关
const rankingType = ref("total");
const rankingList = ref([]);
const rankingLoading = ref(false);
const currentUserStats = ref(null);
const currentUserId = ref(null);

const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  pageSize: 10,
  current: 1,
});

// 热门标签
const popularTags = ref([
  "数组", "哈希表", "字符串", "动态规划", "数学", "深度优先搜索",
  "贪心", "广度优先搜索", "二分查找", "回溯", "栈", "堆"
]);

// 难度选项
const difficulties = ref([
  { label: "简单", value: "简单", class: "easy" },
  { label: "中等", value: "中等", class: "medium" },
  { label: "困难", value: "困难", class: "hard" }
]);

// 日历数据
const calendarDays = ref([]);
// 打卡日期数据（从后端获取）
const checkedDates = ref<string[]>([]);
// 当前月份显示
const currentMonth = ref((new Date().getMonth() + 1) + "月");
// 当前查看的年月
const viewYear = ref(new Date().getFullYear());
const viewMonth = ref(new Date().getMonth());

// 格式化日期为 yyyy-MM-dd（使用本地时区）
const formatDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 生成日历数据
const generateCalendar = () => {
  const today = new Date();
  const year = viewYear.value;
  const month = viewMonth.value;
  const firstDay = new Date(year, month, 1);
  const startDate = new Date(firstDay);
  startDate.setDate(startDate.getDate() - firstDay.getDay());

  const days = [];
  for (let i = 0; i < 42; i++) {
    const currentDate = new Date(startDate);
    currentDate.setDate(startDate.getDate() + i);

    const dateStr = formatDate(currentDate);
    const isChecked = checkedDates.value.includes(dateStr);
    const isToday = formatDate(today) === dateStr;

    days.push({
      date: dateStr,
      day: currentDate.getDate(),
      isToday: isToday,
      isChecked: isChecked,
      isOtherMonth: currentDate.getMonth() !== month
    });
  }

  calendarDays.value = days;
  // 更新月份显示
  currentMonth.value = `${year}年${month + 1}月`;
};

// 切换到上一月
const prevMonth = () => {
  if (viewMonth.value === 0) {
    viewMonth.value = 11;
    viewYear.value--;
  } else {
    viewMonth.value--;
  }
  generateCalendar();
};

// 切换到下一月
const nextMonth = () => {
  if (viewMonth.value === 11) {
    viewMonth.value = 0;
    viewYear.value++;
  } else {
    viewMonth.value++;
  }
  generateCalendar();
};

// 加载打卡数据（仅登录用户）
const loadCheckInData = async () => {
  // 先检查用户是否登录
  const loginUser = store.state.user?.loginUser;
  if (!loginUser || !loginUser.id) {
    console.log("用户未登录，跳过加载打卡数据");
    return;
  }
  try {
    const res = await getUserDailyActivity();
    // 处理axios响应结构
    const backendResponse = res.data;
    console.log("打卡数据响应:", backendResponse);
    if (backendResponse && backendResponse.code === 0) {
      checkedDates.value = backendResponse.data || [];
      console.log("打卡日期列表:", checkedDates.value);
    } else {
      console.error('打卡数据API错误:', backendResponse);
    }
  } catch (error) {
    console.error("加载打卡数据失败:", error);
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
      searchParams.value
    );
    if (res.code === 0) {
      dataList.value = res.data.records || [];
      total.value = res.data.total || 0;
    } else {
      message.error("加载失败，" + res.message);
    }
  } catch (error) {
    message.error("网络错误，请稍后重试");
  } finally {
    loading.value = false;
  }
};

watchEffect(() => {
  loadData();
});

onMounted(async () => {
  // 读取 GlobalHeader 传递的搜索参数
  const searchQuery = route.query.search as string;
  if (searchQuery && searchQuery.trim()) {
    searchParams.value.title = searchQuery.trim();
    searchKeyword.value = searchQuery.trim();
    // 清空 URL query 参数，避免刷新后重复搜索
    router.replace({ path: '/questions' });
  }

  await loadCheckInData(); // 先加载打卡数据
  generateCalendar();      // 再生成日历
  currentUserId.value = getCurrentUserId();
  loadRankingData();
  loadCurrentUserStats();
});

// 解析标签
const parseTags = (tags: any) => {
  if (!tags) return [];
  
  // 如果已经是数组，直接返回
  if (Array.isArray(tags)) {
    return tags;
  }
  
  // 如果是字符串，尝试解析
  if (typeof tags === 'string') {
    try {
      // 尝试解析JSON格式
      const parsed = JSON.parse(tags);
      if (Array.isArray(parsed)) {
        return parsed;
      }
      // 如果不是数组，尝试按逗号分割
      return tags.split(',').map(tag => tag.trim()).filter(tag => tag);
    } catch (e) {
      // JSON解析失败，尝试按逗号分割
      try {
        return tags.split(',').map(tag => tag.trim()).filter(tag => tag);
      } catch (e2) {
        console.error('标签解析失败:', tags, e2);
        return [];
      }
    }
  }
  
  // 其他类型，返回空数组
  return [];
};

// 计算通过率
const calculateRate = (accepted: number, submit: number) => {
  if (!submit || submit === 0) return 0;
  return Math.round((accepted / submit) * 100);
};

// 获取难度样式类
const getDifficultyClass = (difficulty: string) => {
  switch (difficulty) {
    case '简单':
      return 'easy';
    case '中等':
      return 'medium';
    case '困难':
      return 'hard';
    default:
      return 'unknown';
  }
};

// 获取难度文本
const getDifficultyText = (difficulty: string) => {
  const difficultyMap = {
    '简单': '简单',
    '中等': '中等', 
    '困难': '困难'
  };
  return difficultyMap[difficulty] || '未知';
};

// 获取标签样式类
const getTagClass = (tag: string) => {
  const tagClasses = ['algorithm', 'data-structure', 'math', 'string'];
  return tagClasses[Math.floor(Math.random() * tagClasses.length)];
};

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const router = useRouter();
const route = useRoute();
const store = useStore();

// 跳转到做题页面
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

// 获取当前用户ID
const getCurrentUserId = () => {
  const loginUser = store.state.user?.loginUser;
  return loginUser?.id;
};

// 加载排行榜数据
const loadRankingData = async () => {
  rankingLoading.value = true;
  try {
    let res;
    if (rankingType.value === 'week') {
      res = await getWeeklyUserRankingList();
    } else {
      res = await getUserRankingList();
    }
    
    // 处理axios响应结构
    const backendResponse = res.data;
    
    if (backendResponse && backendResponse.code === 0) {
      rankingList.value = backendResponse.data || [];
    } else {
      console.error('排行榜API错误:', backendResponse);
      message.error("加载排行榜失败，" + (backendResponse?.message || '未知错误'));
    }
  } catch (error) {
    console.error("排行榜网络错误:", error);
    message.error("加载排行榜失败，" + (error.message || '网络错误'));
  } finally {
    rankingLoading.value = false;
  }
};

// 加载当前用户统计
const loadCurrentUserStats = async () => {
  const userId = getCurrentUserId();
  if (!userId) return;
  
  try {
    const res = await getUserStats(userId);
    
    // 处理axios响应结构
    const backendResponse = res.data;
    
    if (backendResponse && backendResponse.code === 0) {
      currentUserStats.value = backendResponse.data;
    } else {
      console.error('用户统计API错误:', backendResponse);
    }
  } catch (error) {
    console.error("加载用户统计失败:", error);
  }
};

// 切换排行榜类型
const switchRankingType = (type: string) => {
  rankingType.value = type;
  loadRankingData();
};

// 处理头像加载错误
const handleAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  // 如果加载失败，使用默认头像
  if (target.src !== '/yake.webp') {
    target.src = '/yake.webp';
  }
};

// 获取奖牌样式
const getMedalClass = (index: number) => {
  switch (index) {
    case 0:
      return 'gold';
    case 1:
      return 'silver';
    case 2:
      return 'bronze';
    default:
      return '';
  }
};

// 设置活跃的筛选类型
const setActiveFilter = (filterType: string) => {
  activeFilter.value = filterType;
  // 清空搜索关键词
  searchKeyword.value = "";
};

// 获取搜索框占位符
const getSearchPlaceholder = () => {
  switch (activeFilter.value) {
    case 'tags':
      return '搜索标签';
    case 'difficulty':
      return '选择难度';
    case 'official':
      return '筛选官方题解';
    case 'number':
      return '输入题目编号';
    default:
      return '搜索题目';
  }
};

// 切换标签选择
const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag);
  if (index > -1) {
    selectedTags.value.splice(index, 1);
  } else {
    selectedTags.value.push(tag);
  }
  updateSearchParams();
};

// 选择难度
const selectDifficulty = (difficulty: string) => {
  selectedDifficulty.value = selectedDifficulty.value === difficulty ? "" : difficulty;
  updateSearchParams();
};

// 设置官方题解筛选
const setOfficialSolution = (hasOfficial: boolean) => {
  hasOfficialSolution.value = hasOfficialSolution.value === hasOfficial ? null : hasOfficial;
  updateSearchParams();
};

// 更新搜索参数
const updateSearchParams = () => {
  searchParams.value = {
    ...searchParams.value,
    tags: selectedTags.value,
    difficulty: selectedDifficulty.value || undefined,
    current: 1,
  };
};

// 搜索处理
const handleSearch = () => {
  switch (activeFilter.value) {
    case 'number':
      // 按题目编号搜索
      const questionId = parseInt(searchKeyword.value);
      if (questionId && !isNaN(questionId)) {
        // 设置搜索参数为题目ID
        searchParams.value = {
          ...searchParams.value,
          id: questionId,
          current: 1,
        } as QuestionQueryRequest;
        // 清空搜索关键词
        searchKeyword.value = "";
      } else if (searchKeyword.value.trim()) {
        message.warning("请输入有效的题目编号");
      }
      break;
    case 'tags':
      // 按标签名搜索
      if (searchKeyword.value.trim()) {
        toggleTag(searchKeyword.value.trim());
        searchKeyword.value = "";
      }
      break;
    default:
      // 按标题搜索
      searchParams.value = {
        ...searchParams.value,
        title: searchKeyword.value,
        current: 1,
      };
      break;
  }
};

// 清除所有筛选条件
const clearAllFilters = () => {
  selectedTags.value = [];
  selectedDifficulty.value = "";
  hasOfficialSolution.value = null;
  searchKeyword.value = "";
  activeFilter.value = "tags"; // 默认回到标签筛选
  // 重置所有搜索参数
  searchParams.value = {
    title: "",
    tags: [],
    pageSize: 10,
    current: 1,
  };
};

// 判断是否有激活的筛选条件
const hasActiveFilters = () => {
  return selectedTags.value.length > 0 || selectedDifficulty.value !== "" || hasOfficialSolution.value !== null || searchKeyword.value !== "";
};

// 跳转到会员页面
const goToMembership = () => {
  router.push('/membership');
};
</script>

<style scoped>
#questionsView {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;
  /* 恢复适中的页面宽度 */
  width: 100%;
}

.main-container {
  /* 恢复最大宽度限制，保持适中的页面宽度 */
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  padding: 0 20px;
}

/* 左侧内容区域 */
.left-content {
  flex: 1;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 筛选区域 */
.filter-section {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-tabs {
  display: flex;
  gap: 24px;
}

.filter-tab {
  color: #666;
  cursor: pointer;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.filter-tab.active {
  color: #4a90e2;
  border-bottom-color: #4a90e2;
  font-weight: 500;
}

.filter-tab:hover {
  color: #4a90e2;
}

.filter-search {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.clear-filter-btn {
  white-space: nowrap;
  height: 32px;
  padding: 0 12px;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
  border: 1px solid #d9d9d9;
  background: #fff;
  transition: all 0.2s;
}

.clear-filter-btn:hover:not(:disabled) {
  color: #ff4d4f;
  border-color: #ff4d4f;
  background: #fff2f0;
}

.clear-filter-btn:disabled {
  color: #bfbfbf;
  border-color: #f0f0f0;
  background: #fafafa;
  cursor: not-allowed;
}

/* 筛选选项区域 */
.filter-options {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: #fafbfc;
}

.tag-filters, .difficulty-filters, .official-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-tag, .filter-difficulty, .filter-official {
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #d9d9d9;
  background: white;
  color: #666;
}

.filter-tag:hover, .filter-difficulty:hover, .filter-official:hover {
  border-color: #4a90e2;
  color: #4a90e2;
}

.filter-tag.active, .filter-difficulty.active, .filter-official.active {
  background: #4a90e2;
  border-color: #4a90e2;
  color: white;
}

.filter-difficulty.easy.active {
  background: #52c41a;
  border-color: #52c41a;
}

.filter-difficulty.medium.active {
  background: #fa8c16;
  border-color: #fa8c16;
}

.filter-difficulty.hard.active {
  background: #ff4d4f;
  border-color: #ff4d4f;
}

/* 题目列表 */
.question-list {
  background: white;
}

.list-header {
  display: grid;
  grid-template-columns: 80px 1fr 100px 100px 100px 120px;
  gap: 16px;
  padding: 16px 20px;
  background: #fafbfc;
  border-bottom: 1px solid #e8e8e8;
  font-weight: 500;
  color: #333;
}

.header-item {
  display: flex;
  align-items: center;
}

.list-body {
  max-height: 600px;
  overflow-y: auto;
}

.question-item {
  display: grid;
  grid-template-columns: 80px 1fr 100px 100px 100px 120px;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.question-item:hover {
  background: #f8f9fa;
}

.item-number {
  color: #666;
  font-weight: 500;
}

.item-title {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.title-main {
  color: #333;
  font-weight: 500;
  cursor: pointer;
}

.title-main:hover {
  color: #4a90e2;
}

.title-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
  margin-top: 6px;
}

.tag {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #1890ff;
  font-weight: 400;
  display: inline-block;
  margin-right: 6px;
  margin-bottom: 4px;
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  transition: all 0.2s ease;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag.new {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  border: 1px solid #ff6b35;
  font-weight: 500;
}

/* 统一所有标签样式为浅蓝色 */
.tag.algorithm,
.tag.data-structure,
.tag.math,
.tag.string {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.item-difficulty {
  display: flex;
  align-items: center;
}

.difficulty-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.difficulty-badge.easy {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.difficulty-badge.medium {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.difficulty-badge.hard {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffb3b3;
}

.difficulty-badge.unknown {
  background: #f5f5f5;
  color: #999;
  border: 1px solid #d9d9d9;
}

.item-rate {
  color: #666;
  font-weight: 500;
}

.item-count {
  color: #666;
}

.item-action {
  display: flex;
  align-items: center;
}

.challenge-btn {
  background: #4a90e2;
  border-color: #4a90e2;
  border-radius: 4px;
  font-size: 12px;
  height: 28px;
  padding: 0 12px;
}

/* 分页 */
.pagination-wrapper {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
}

/* 右侧边栏 */
.right-sidebar {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 日历卡片 */
.calendar-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-weight: 600;
  color: #333;
}

.month-info {
  color: #666;
  font-size: 14px;
}

.month-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.month-btn {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 4px;
  color: #666;
  transition: all 0.2s;
  user-select: none;
}

.month-btn:hover {
  background: #f0f7ff;
  color: #4a90e2;
}

.calendar-grid {
  margin-bottom: 12px;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  margin-bottom: 8px;
}

.calendar-header span {
  text-align: center;
  font-size: 12px;
  color: #666;
  padding: 4px 0;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.calendar-day:hover {
  background: #f0f7ff;
}

.calendar-day.today {
  background: #4a90e2;
  color: white;
}

.calendar-day.checked {
  background: #52c41a;
  color: white;
}

.calendar-day.other-month {
  color: #ccc;
}

.calendar-footer {
  text-align: center;
  font-size: 12px;
  color: #666;
}

/* 练习房卡片 */
.practice-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.practice-actions {
  display: flex;
  gap: 8px;
}

.practice-content {
  margin-top: 16px;
}

.practice-status {
  text-align: center;
  margin-bottom: 16px;
}

.status-text {
  color: #666;
  font-size: 14px;
}

.status-count {
  color: #333;
  font-weight: 600;
  font-size: 18px;
  margin-left: 8px;
}

/* 排行榜样式 */
.ranking-list {
  max-height: 300px;
  overflow-y: auto;
  margin-top: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
  min-height: 48px;
}

.ranking-item:hover {
  background: #f8f9fa;
}

.ranking-item.current-user {
  background: #e6f7ff;
  border-radius: 4px;
  margin: 2px 0;
}

.rank-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.medal {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.medal.gold {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #b8860b;
}

.medal.silver {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  color: #696969;
}

.medal.bronze {
  background: linear-gradient(135deg, #cd7f32, #daa520);
  color: #8b4513;
}

.rank-text {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ranking-item .user-avatar {
  margin-right: 12px;
  flex-shrink: 0;
}

.ranking-item .user-avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}



.ranking-item .user-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.ranking-item .user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-score {
  flex-shrink: 0;
  width: 60px;
  text-align: right;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.score-number {
  font-size: 14px;
  color: #4a90e2;
  font-weight: 600;
  line-height: 1;
}

/* 空状态样式 */
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #999;
}

.empty-text {
  font-size: 14px;
  line-height: 1.5;
}

/* 客服按钮 */
.service-buttons {
  position: fixed;
  right: 20px;
  bottom: 100px;
}

.service-btn {
  background: #4a90e2;
  color: white;
  padding: 12px 16px;
  border-radius: 25px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.3);
  transition: all 0.3s;
}

.service-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(74, 144, 226, 0.4);
}

/* 响应式设计 */
@media (max-width: 500px) {
  .main-container {
    flex-direction: column;
    padding: 0 16px;
  }
  
  .right-sidebar {
    width: 100%;
  }
  
  .question-item {
    grid-template-columns: 60px 1fr 80px 80px;
    gap: 12px;
  }
  
  .list-header {
    grid-template-columns: 60px 1fr 80px 80px;
    gap: 12px;
  }
  
  .item-count,
  .item-action {
    display: none;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .filter-search {
    width: 100%;
  }
}
</style>
