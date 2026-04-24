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
                  <span v-if="isNewQuestion(question.createTime)" class="tag new">NEW</span>
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
import { computed, onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { IconGift } from '@arco-design/web-vue/es/icon';
import { getUserRankingList, getWeeklyUserRankingList, getUserStats } from "@/api/userController";
import { getUserDailyActivity } from "@/api/questionSubmitController";
import axios from "axios";

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

// 热门标签（从后端动态获取）
const popularTags = ref<string[]>([]);

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

const loadPopularTags = async () => {
  try {
    const res = await axios.get("/api/question/tags/popular", { params: { limit: 20 } });
    if (res.data.code === 0 && res.data.data) {
      popularTags.value = res.data.data;
    }
  } catch (e) {
    console.error("加载标签失败", e);
  }
};

onMounted(async () => {
  loadPopularTags();
  await loadCheckInData();
  generateCalendar();
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
  let hash = 0;
  for (let i = 0; i < tag.length; i++) {
    hash = tag.charCodeAt(i) + ((hash << 5) - hash);
  }
  return tagClasses[Math.abs(hash) % tagClasses.length];
};

// 判断题目是否为新题（7天内创建）
const isNewQuestion = (createTime: string) => {
  if (!createTime) return false;
  const created = new Date(createTime).getTime();
  const now = Date.now();
  return now - created < 7 * 24 * 60 * 60 * 1000;
};

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const router = useRouter();
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
  if (activeFilter.value === 'number') {
    return '输入题目编号';
  }
  return '搜索题目/标签/难度';
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
    hasOfficialSolution: hasOfficialSolution.value,
    current: 1,
  };
};

// 搜索处理
const handleSearch = () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) return;

  // 智能识别搜索内容
  if (popularTags.value.includes(keyword)) {
    // 是已知标签，添加标签筛选
    toggleTag(keyword);
  } else if (difficulties.value.some(d => d.value === keyword)) {
    // 是难度，设置难度筛选
    selectedDifficulty.value = keyword;
    updateSearchParams();
  } else if (activeFilter.value === 'number') {
    // 按题目编号搜索
    const questionId = parseInt(keyword);
    if (questionId && !isNaN(questionId)) {
      searchParams.value = {
        ...searchParams.value,
        id: questionId,
        current: 1,
      } as QuestionQueryRequest;
    } else {
      message.warning("请输入有效的题目编号");
    }
  } else {
    // 默认按标题搜索
    searchParams.value = {
      ...searchParams.value,
      title: keyword,
      current: 1,
    };
  }

  // 清空搜索关键词
  searchKeyword.value = "";
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
const hasActiveFilters = computed(() => {
  return selectedTags.value.length > 0 || selectedDifficulty.value !== "" || hasOfficialSolution.value !== null || searchKeyword.value !== "";
});

// 跳转到会员页面
const goToMembership = () => {
  router.push('/membership');
};
</script>

<style scoped>
#questionsView {
  background-color: var(--bg-page);
  min-height: 100vh;
  padding: var(--space-6) 0;
  width: 100%;
  font-family: var(--font-body);
  position: relative;
}

/* 背景纹理 */
#questionsView::before {
  content: '';
  position: fixed;
  inset: 0;
  background:
    radial-gradient(at 20% 10%, rgba(59, 130, 246, 0.05) 0px, transparent 50%),
    radial-gradient(at 80% 20%, rgba(6, 182, 212, 0.04) 0px, transparent 50%),
    radial-gradient(at 40% 80%, rgba(59, 130, 246, 0.03) 0px, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 页面入场动画 */
#questionsView {
  animation: fadeIn 0.5s var(--ease-out);
}

.main-container {
  max-width: var(--content-max-width);
  margin: 0 auto;
  display: flex;
  gap: var(--space-6);
  padding: 0 var(--space-6);
  position: relative;
  z-index: 1;
}

/* 左侧内容区域 */
.left-content {
  flex: 1;
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  animation: fadeInUp 0.6s var(--ease-out) calc(var(--stagger-delay) * 2) backwards;
}

/* 筛选区域 */
.filter-section {
  padding: var(--space-5) var(--space-6);
  border-bottom: 1px solid var(--border-default);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-card);
}

.filter-tabs {
  display: flex;
  gap: var(--space-2);
  background: var(--bg-subtle);
  padding: 4px;
  border-radius: var(--radius-md);
}

.filter-tab {
  color: var(--text-secondary);
  cursor: pointer;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  transition: all var(--duration-fast);
  font-size: var(--text-sm);
  font-weight: 500;
}

.filter-tab.active {
  background: var(--bg-card);
  color: var(--color-primary-600);
  font-weight: 600;
  box-shadow: var(--shadow-xs);
}

.filter-tab:hover:not(.active) {
  color: var(--color-primary-500);
}

.filter-search {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.search-input {
  flex: 1;
  max-width: 280px;
}

.search-input :deep(.arco-input) {
  border-radius: var(--radius-md);
}

.clear-filter-btn {
  white-space: nowrap;
  height: 36px;
  padding: 0 var(--space-4);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-secondary);
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  transition: all var(--duration-fast);
}

.clear-filter-btn:hover:not(:disabled) {
  color: var(--color-error);
  border-color: var(--color-error);
  background: var(--color-error-bg);
}

.clear-filter-btn:disabled {
  color: var(--text-disabled);
  border-color: var(--border-default);
  background: var(--bg-subtle);
  cursor: not-allowed;
}

/* 筛选选项区域 */
.filter-options {
  padding: var(--space-4) var(--space-6);
  border-bottom: 1px solid var(--border-default);
  background: var(--bg-subtle);
}

.tag-filters, .difficulty-filters, .official-filters {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.filter-tag, .filter-difficulty, .filter-official {
  padding: var(--space-1) var(--space-4);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--duration-fast);
  border: 1px solid var(--border-default);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-weight: 500;
}

.filter-tag:hover, .filter-difficulty:hover, .filter-official:hover {
  border-color: var(--color-primary-500);
  color: var(--color-primary-500);
}

.filter-tag.active, .filter-official.active {
  background: var(--color-primary-500);
  border-color: var(--color-primary-500);
  color: white;
}

.filter-difficulty.easy.active {
  background: var(--color-success);
  border-color: var(--color-success);
  color: white;
}

.filter-difficulty.medium.active {
  background: var(--color-warning);
  border-color: var(--color-warning);
  color: white;
}

.filter-difficulty.hard.active {
  background: var(--color-error);
  border-color: var(--color-error);
  color: white;
}

/* 题目列表 */
.question-list {
  background: var(--bg-card);
}

.list-header {
  display: grid;
  grid-template-columns: 80px 1fr 100px 100px 100px 120px;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-6);
  background: var(--bg-subtle);
  border-bottom: 1px solid var(--border-default);
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-sm);
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
  gap: var(--space-4);
  padding: var(--space-4) var(--space-6);
  border-bottom: 1px solid var(--border-default);
  transition: all var(--duration-fast);
  animation: fadeIn 0.4s var(--ease-out) backwards;
}

/* 题目列表交错动画 */
.question-item:nth-child(1) { animation-delay: calc(var(--stagger-delay) * 6); }
.question-item:nth-child(2) { animation-delay: calc(var(--stagger-delay) * 7); }
.question-item:nth-child(3) { animation-delay: calc(var(--stagger-delay) * 8); }
.question-item:nth-child(4) { animation-delay: calc(var(--stagger-delay) * 9); }
.question-item:nth-child(5) { animation-delay: calc(var(--stagger-delay) * 10); }
.question-item:nth-child(6) { animation-delay: calc(var(--stagger-delay) * 11); }
.question-item:nth-child(7) { animation-delay: calc(var(--stagger-delay) * 12); }
.question-item:nth-child(8) { animation-delay: calc(var(--stagger-delay) * 13); }
.question-item:nth-child(9) { animation-delay: calc(var(--stagger-delay) * 14); }
.question-item:nth-child(10) { animation-delay: calc(var(--stagger-delay) * 15); }

.question-item:hover {
  background: var(--color-primary-50);
}

.question-item:last-child {
  border-bottom: none;
}

.item-number {
  color: var(--text-tertiary);
  font-weight: 600;
  font-size: var(--text-sm);
}

.item-title {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.title-main {
  color: var(--text-primary);
  font-weight: 600;
  cursor: pointer;
  font-size: var(--text-base);
  transition: color var(--duration-fast);
}

.title-main:hover {
  color: var(--color-primary-500);
}

.title-tags {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
  align-items: center;
}

.tag {
  padding: 2px var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  border: 1px solid var(--color-primary-200);
  font-weight: 500;
}

.tag.new {
  background: var(--color-warning-bg);
  color: var(--color-warning-text);
  border-color: var(--color-warning-border);
  font-weight: 600;
}

.tag.algorithm { background: #fef3c7; color: #92400e; border-color: #fcd34d; }
.tag.data-structure { background: #dbeafe; color: #1e40af; border-color: #93c5fd; }
.tag.math { background: #f3e8ff; color: #7c3aed; border-color: #c4b5fd; }
.tag.string { background: #ecfdf5; color: #047857; border-color: #6ee7b7; }

.item-difficulty {
  display: flex;
  align-items: center;
}

.difficulty-badge {
  padding: 4px var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: 600;
}

.difficulty-badge.easy {
  background: var(--color-success-bg);
  color: var(--color-success-text);
  border: 1px solid var(--color-success-border);
}

.difficulty-badge.medium {
  background: var(--color-warning-bg);
  color: var(--color-warning-text);
  border: 1px solid var(--color-warning-border);
}

.difficulty-badge.hard {
  background: var(--color-error-bg);
  color: var(--color-error-text);
  border: 1px solid var(--color-error-border);
}

.difficulty-badge.unknown {
  background: var(--bg-subtle);
  color: var(--text-secondary);
  border: 1px solid var(--border-default);
}

.item-rate {
  color: var(--text-primary);
  font-weight: 600;
  font-size: var(--text-sm);
}

.item-count {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.item-action {
  display: flex;
  align-items: center;
}

.challenge-btn {
  background: var(--color-primary-500);
  border-color: var(--color-primary-500);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: 600;
  height: 32px;
  padding: 0 var(--space-4);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
  transition: all var(--duration-fast);
  position: relative;
  overflow: hidden;
}

.challenge-btn:hover {
  background: var(--color-primary-600);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.challenge-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 分页 */
.pagination-wrapper {
  padding: var(--space-5);
  text-align: center;
  border-top: 1px solid var(--border-default);
  background: var(--bg-card);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

/* 右侧边栏 */
.right-sidebar {
  width: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
  animation: fadeInUp 0.6s var(--ease-out) calc(var(--stagger-delay) * 3) backwards;
}

/* 日历卡片 */
.calendar-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  animation: scaleIn 0.5s var(--ease-out) calc(var(--stagger-delay) * 4) backwards;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.card-title {
  font-weight: 700;
  color: var(--text-primary);
  font-size: var(--text-base);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.card-title::before {
  content: '📅';
  font-size: var(--text-lg);
}

.month-info {
  color: var(--text-primary);
  font-weight: 600;
  font-size: var(--text-sm);
}

.month-selector {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.month-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  transition: all var(--duration-fast);
  user-select: none;
  font-size: var(--text-sm);
  font-weight: 600;
}

.month-btn:hover {
  background: var(--color-primary-50);
  color: var(--color-primary-500);
}

.calendar-grid {
  margin-bottom: var(--space-3);
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
  margin-bottom: var(--space-2);
}

.calendar-header span {
  text-align: center;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  padding: var(--space-1) 0;
  font-weight: 600;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.calendar-day {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--duration-fast);
  font-weight: 500;
}

.calendar-day:hover {
  background: var(--bg-subtle);
}

.calendar-day.today {
  background: var(--color-primary-500);
  color: white;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.calendar-day.checked {
  background: var(--color-success);
  color: white;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.calendar-day.other-month {
  color: var(--text-disabled);
}

.calendar-footer {
  text-align: center;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  padding-top: var(--space-2);
  border-top: 1px solid var(--border-default);
}

/* 排行榜卡片 */
.practice-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-sm);
  animation: scaleIn 0.5s var(--ease-out) calc(var(--stagger-delay) * 5) backwards;
}

.practice-actions {
  display: flex;
  gap: var(--space-2);
}

.practice-actions :deep(.arco-btn) {
  border-radius: var(--radius-md);
  font-weight: 600;
}

.practice-content {
  margin-top: var(--space-4);
}

.practice-status {
  text-align: center;
  margin-bottom: var(--space-4);
  padding: var(--space-4);
  background: var(--color-primary-50);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-primary-100);
}

.status-text {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.status-count {
  color: var(--color-primary-600);
  font-weight: 700;
  font-size: var(--text-2xl);
  margin-left: var(--space-2);
}

/* 排行榜样式 */
.ranking-list {
  max-height: 280px;
  overflow-y: auto;
  margin-top: var(--space-3);
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: var(--space-3);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-1);
  transition: all var(--duration-fast);
}

.ranking-item:hover {
  background: var(--bg-subtle);
}

.ranking-item.current-user {
  background: var(--color-primary-50);
  border: 1px solid var(--color-primary-200);
}

.rank-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--space-3);
  flex-shrink: 0;
}

.medal {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-xs);
  font-weight: 700;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.medal.gold { background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%); }
.medal.silver { background: linear-gradient(135deg, #9ca3af 0%, #6b7280 100%); }
.medal.bronze { background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); }

.rank-text {
  font-size: var(--text-base);
  color: var(--text-secondary);
  font-weight: 600;
}

.ranking-item .user-avatar {
  margin-right: var(--space-3);
  flex-shrink: 0;
}

.ranking-item .user-avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-default);
}

.ranking-item .user-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.ranking-item .user-name {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-score {
  flex-shrink: 0;
  text-align: right;
}

.score-number {
  font-size: var(--text-base);
  color: var(--color-primary-600);
  font-weight: 700;
}

/* 空状态样式 */
.empty-state {
  padding: var(--space-10) var(--space-5);
  text-align: center;
  color: var(--text-secondary);
}

.empty-text {
  font-size: var(--text-sm);
}

/* 客服按钮 */
.service-buttons {
  position: fixed;
  right: var(--space-6);
  bottom: 100px;
}

.service-btn {
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-accent-500) 100%);
  color: white;
  padding: var(--space-3) var(--space-5);
  border-radius: var(--radius-full);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4);
  transition: all var(--duration-fast);
}

.service-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
    padding: 0 var(--space-4);
  }

  .right-sidebar {
    width: 100%;
  }

  .question-item {
    grid-template-columns: 60px 1fr 80px 80px;
    gap: var(--space-3);
  }

  .list-header {
    grid-template-columns: 60px 1fr 80px 80px;
    gap: var(--space-3);
  }

  .item-count,
  .item-action {
    display: none;
  }

  .filter-section {
    flex-direction: column;
    gap: var(--space-3);
    align-items: stretch;
  }

  .filter-search {
    width: 100%;
  }
}
</style>
