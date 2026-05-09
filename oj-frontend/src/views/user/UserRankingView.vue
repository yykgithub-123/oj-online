<template>
  <div class="ranking-container">
    <div class="ranking-header">
      <h1>用户排行榜</h1>
      <p>展示用户解题统计和排名信息</p>
    </div>

    <!-- 排行榜切换标签 -->
    <div class="ranking-tabs">
      <a-tabs v-model:active-key="activeTab" @change="handleTabChange">
        <a-tab-pane key="total" title="总排行榜">
          <div class="ranking-content">
            <a-table 
              :columns="totalRankingColumns" 
              :data="totalRankingData" 
              :loading="loading"
              :pagination="{ pageSize: 20 }"
            >
              <template #rank="{ record, rowIndex }">
                <div class="rank-cell">
                  <span v-if="rowIndex < 3" class="medal">
                    {{ rowIndex === 0 ? '🥇' : rowIndex === 1 ? '🥈' : '🥉' }}
                  </span>
                  <span class="rank-number">{{ rowIndex + 1 }}</span>
                </div>
              </template>
              
              <template #user="{ record }">
                <div class="user-info">
                  <a-avatar size="small">{{ record.userName.charAt(0) }}</a-avatar>
                  <span class="username">{{ record.userName }}</span>
                </div>
              </template>
              
              <template #stats="{ record }">
                <div class="stats-cell">
                  <div class="stat-item">
                    <span class="stat-label">解题数:</span>
                    <span class="stat-value">{{ record.solvedCount }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">正确率:</span>
                    <span class="stat-value">{{ (record.acceptanceRate * 100).toFixed(1) }}%</span>
                  </div>
                </div>
              </template>
            </a-table>
          </div>
        </a-tab-pane>
        
        <a-tab-pane key="weekly" title="周排行榜">
          <div class="ranking-content">
            <a-table 
              :columns="weeklyRankingColumns" 
              :data="weeklyRankingData" 
              :loading="loading"
              :pagination="{ pageSize: 20 }"
            >
              <template #rank="{ record, rowIndex }">
                <div class="rank-cell">
                  <span v-if="rowIndex < 3" class="medal">
                    {{ rowIndex === 0 ? '🥇' : rowIndex === 1 ? '🥈' : '🥉' }}
                  </span>
                  <span class="rank-number">{{ rowIndex + 1 }}</span>
                </div>
              </template>
              
              <template #user="{ record }">
                <div class="user-info">
                  <a-avatar size="small">{{ record.userName.charAt(0) }}</a-avatar>
                  <span class="username">{{ record.userName }}</span>
                </div>
              </template>
              
              <template #weeklyStats="{ record }">
                <div class="stats-cell">
                  <div class="stat-item">
                    <span class="stat-label">本周解题:</span>
                    <span class="stat-value">{{ record.weeklySolved }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">本周提交:</span>
                    <span class="stat-value">{{ record.weeklySubmissions }}</span>
                  </div>
                </div>
              </template>
            </a-table>
          </div>
        </a-tab-pane>
        
        <a-tab-pane key="personal" title="个人统计">
          <div class="personal-stats" v-if="userStats">
            <div class="stats-cards">
              <div class="stat-card">
                <div class="stat-icon">🏆</div>
                <div class="stat-info">
                  <div class="stat-title">当前排名</div>
                  <div class="stat-number">{{ userStats.rank || 'N/A' }}</div>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon">✅</div>
                <div class="stat-info">
                  <div class="stat-title">解题总数</div>
                  <div class="stat-number">{{ userStats.solvedCount || 0 }}</div>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon">📊</div>
                <div class="stat-info">
                  <div class="stat-title">正确率</div>
                  <div class="stat-number">{{ ((userStats.acceptanceRate || 0) * 100).toFixed(1) }}%</div>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon">📈</div>
                <div class="stat-info">
                  <div class="stat-title">周排名</div>
                  <div class="stat-number">{{ userStats.weeklyRank || 'N/A' }}</div>
                </div>
              </div>
            </div>
            
            <div class="difficulty-stats">
              <h3>难度分布</h3>
              <div class="difficulty-bars">
                <div class="difficulty-item">
                  <span class="difficulty-label easy">简单</span>
                  <div class="difficulty-bar">
                    <div class="difficulty-progress easy" :style="{ width: getProgressWidth(userStats.easySolved, userStats.easyTotal) }"></div>
                  </div>
                  <span class="difficulty-count">{{ userStats.easySolved || 0 }}/{{ userStats.easyTotal || 0 }}</span>
                </div>
                
                <div class="difficulty-item">
                  <span class="difficulty-label medium">中等</span>
                  <div class="difficulty-bar">
                    <div class="difficulty-progress medium" :style="{ width: getProgressWidth(userStats.mediumSolved, userStats.mediumTotal) }"></div>
                  </div>
                  <span class="difficulty-count">{{ userStats.mediumSolved || 0 }}/{{ userStats.mediumTotal || 0 }}</span>
                </div>
                
                <div class="difficulty-item">
                  <span class="difficulty-label hard">困难</span>
                  <div class="difficulty-bar">
                    <div class="difficulty-progress hard" :style="{ width: getProgressWidth(userStats.hardSolved, userStats.hardTotal) }"></div>
                  </div>
                  <span class="difficulty-count">{{ userStats.hardSolved || 0 }}/{{ userStats.hardTotal || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="no-stats">
            <a-empty description="请先登录查看个人统计" />
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import { getUserRankingList, getWeeklyUserRankingList, getUserStats } from '@/api/userController';

const store = useStore();
const activeTab = ref('total');
const loading = ref(false);

// 排行榜数据
const totalRankingData = ref([]);
const weeklyRankingData = ref([]);
const userStats = ref(null);

// 总排行榜列配置
const totalRankingColumns = [
  {
    title: '排名',
    dataIndex: 'rank',
    slotName: 'rank',
    width: 80,
    align: 'center',
  },
  {
    title: '用户',
    dataIndex: 'user',
    slotName: 'user',
    width: 200,
    align: 'left',
  },
  {
    title: '统计信息',
    dataIndex: 'stats',
    slotName: 'stats',
    align: 'right',
    minWidth: 150,
  },
  {
    title: '最近活跃',
    dataIndex: 'lastActiveTime',
    width: 150,
    align: 'center',
  },
];

// 周排行榜列配置
const weeklyRankingColumns = [
  {
    title: '排名',
    dataIndex: 'rank',
    slotName: 'rank',
    width: 80,
    align: 'center',
  },
  {
    title: '用户',
    dataIndex: 'user',
    slotName: 'user',
    width: 200,
    align: 'left',
  },
  {
    title: '本周统计',
    dataIndex: 'weeklyStats',
    slotName: 'weeklyStats',
    align: 'right',
    minWidth: 150,
  },
  {
    title: '排名变化',
    dataIndex: 'rankChange',
    width: 120,
    align: 'center',
  },
];

// 加载总排行榜数据
const loadTotalRanking = async () => {
  try {
    loading.value = true;
    const res = await getUserRankingList();
    console.log('总排行榜API响应:', res);
    
    // 处理axios响应结构：res.data 是后端返回的 {code: 0, data: Array, message: "ok"}
    const backendResponse = res.data;
    console.log('后端响应数据:', backendResponse);
    console.log('backendResponse.code:', backendResponse.code, typeof backendResponse.code);
    console.log('backendResponse.data:', backendResponse.data, Array.isArray(backendResponse.data));
    
    if (backendResponse && backendResponse.code === 0 && Array.isArray(backendResponse.data)) {
      // 数据映射：将后端字段映射到前端期望的字段
      const mappedData = backendResponse.data.map(item => ({
        ...item,
        solvedCount: item.totalSolved || 0,
        weeklySubmissions: item.totalSubmissions || 0,
        lastActiveTime: item.lastSubmissionTime || '暂无'
      }));
      totalRankingData.value = mappedData;
      console.log('映射后的总排行榜数据:', mappedData);
      console.log('总排行榜数据设置成功，数量:', mappedData.length);
    } else {
      console.error('总排行榜API返回错误:', backendResponse);
      console.error('条件检查失败 - code:', backendResponse?.code, 'data是数组:', Array.isArray(backendResponse?.data));
    }
  } catch (error) {
    console.error('排行榜API错误:', error);
  } finally {
    loading.value = false;
  }
};

// 加载周排行榜数据
const loadWeeklyRanking = async () => {
  try {
    loading.value = true;
    const res = await getWeeklyUserRankingList();
    console.log('周排行榜API响应:', res);
    
    // 处理axios响应结构
    const backendResponse = res.data;
    console.log('周排行榜后端响应数据:', backendResponse);
    
    if (backendResponse && backendResponse.code === 0) {
      // 数据映射：将后端字段映射到前端期望的字段
      const mappedData = (backendResponse.data || []).map(item => ({
        ...item,
        solvedCount: item.totalSolved || 0,
        weeklySubmissions: item.totalSubmissions || 0,
        rankChange: '新上榜' // 暂时设置为固定值
      }));
      weeklyRankingData.value = mappedData;
      console.log('映射后的周排行榜数据:', mappedData);
    } else {
      console.error('周排行榜API返回错误:', backendResponse);
    }
  } catch (error) {
    console.error('加载周排行榜失败:', error);
  } finally {
    loading.value = false;
  }
};

// 加载个人统计数据
const loadUserStats = async () => {
  const loginUser = store.state.user.loginUser;
  if (!loginUser?.id) {
    return;
  }
  
  try {
    loading.value = true;
    const res = await getUserStats(loginUser.id);
    console.log('个人统计API响应:', res);
    
    // 处理axios响应结构
    const backendResponse = res.data;
    console.log('个人统计后端响应数据:', backendResponse);
    
    if (backendResponse && backendResponse.code === 0) {
      // 数据映射：将后端字段映射到前端期望的字段
      const mappedData = {
        ...backendResponse.data,
        solvedCount: backendResponse.data.totalSolved || 0,
        easyTotal: 100, // 暂时设置固定值，后续可以从题目统计中获取
        mediumTotal: 100,
        hardTotal: 100
      };
      userStats.value = mappedData;
      console.log('映射后的个人统计数据:', mappedData);
    } else {
      console.error('个人统计API返回错误:', backendResponse);
    }
  } catch (error) {
    console.error('用户统计API错误:', error);
    // 即使出现错误，也不要阻止页面显示，可以显示默认数据
    userStats.value = {
      userId: loginUser.id,
      userName: loginUser.userName || '未知用户',
      solvedCount: 0,
      totalSubmissions: 0,
      acceptanceRate: 0,
      easyTotal: 100,
      mediumTotal: 100,
      hardTotal: 100
    };
  } finally {
    loading.value = false;
  }
};

// 标签切换处理
const handleTabChange = (key: string) => {
  activeTab.value = key;
  if (key === 'total') {
    loadTotalRanking();
  } else if (key === 'weekly') {
    loadWeeklyRanking();
  } else if (key === 'personal') {
    loadUserStats();
  }
};

// 计算进度条宽度
const getProgressWidth = (solved: number, total: number) => {
  if (!total) return '0%';
  return `${Math.min((solved / total) * 100, 100)}%`;
};

// 组件挂载时加载数据
onMounted(() => {
  loadTotalRanking();
});
</script>

<style scoped>
.ranking-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-6);
}

.ranking-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.ranking-header h1 {
  font-size: var(--text-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.ranking-header p {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

.ranking-tabs {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-default);
  overflow: hidden;
}

.ranking-content {
  padding: var(--space-6);
}

/* 表格样式优化 */
.ranking-content :deep(.arco-table) {
  border-radius: var(--radius-md);
  overflow: hidden;
}

.ranking-content :deep(.arco-table-th) {
  background-color: var(--color-gray-50);
  font-weight: 600;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-default);
}

.ranking-content :deep(.arco-table-td) {
  padding: var(--space-4) var(--space-3);
  vertical-align: middle;
}

.ranking-content :deep(.arco-table-tbody .arco-table-tr:hover) {
  background-color: var(--bg-hover);
}

/* 排名列样式 */
.ranking-content :deep(.arco-table-td:first-child) {
  text-align: center;
  font-weight: 600;
}

/* 用户列样式 */
.ranking-content :deep(.arco-table-td:nth-child(2)) {
  padding-left: var(--space-4);
}

/* 统计信息列样式 */
.ranking-content :deep(.arco-table-td:nth-child(3)) {
  text-align: right;
  padding-right: var(--space-4);
}

/* 最近活跃列样式 */
.ranking-content :deep(.arco-table-td:last-child) {
  text-align: center;
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.rank-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  width: 60px;
  text-align: center;
}

.medal {
  font-size: var(--text-lg);
  width: 20px;
  display: inline-block;
}

.rank-number {
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--text-base);
  min-width: 20px;
  text-align: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  min-width: 150px;
}

.username {
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  font-size: var(--text-sm);
}

.stats-cell {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  align-items: flex-end;
  text-align: right;
  min-width: 120px;
}

.stat-item {
  display: flex;
  gap: var(--space-2);
  align-items: center;
  justify-content: flex-end;
}

.stat-label {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  white-space: nowrap;
}

.stat-value {
  font-weight: 500;
  color: var(--text-primary);
  font-size: var(--text-base);
  min-width: 40px;
  text-align: right;
}

.personal-stats {
  padding: var(--space-6);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}

.stat-card {
  background: var(--color-primary-500);
  color: white;
  padding: var(--space-5);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.stat-icon {
  font-size: var(--text-2xl);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: var(--text-sm);
  opacity: 0.9;
  margin-bottom: var(--space-1);
}

.stat-number {
  font-size: var(--text-2xl);
  font-weight: 600;
}

.difficulty-stats h3 {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

.difficulty-bars {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.difficulty-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.difficulty-label {
  width: 60px;
  font-weight: 500;
  font-size: var(--text-sm);
}

.difficulty-label.easy { color: var(--color-success); }
.difficulty-label.medium { color: var(--color-warning); }
.difficulty-label.hard { color: var(--color-error); }

.difficulty-bar {
  flex: 1;
  height: 8px;
  background: var(--color-gray-100);
  border-radius: 4px;
  overflow: hidden;
}

.difficulty-progress {
  height: 100%;
  border-radius: 4px;
  transition: width var(--duration-normal);
}

.difficulty-progress.easy { background: var(--color-success); }
.difficulty-progress.medium { background: var(--color-warning); }
.difficulty-progress.hard { background: var(--color-error); }

.difficulty-count {
  width: 80px;
  text-align: right;
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.no-stats {
  padding: var(--space-12) var(--space-6);
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 500px) {
  .ranking-container {
    padding: var(--space-4);
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .difficulty-item {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
  }

  .difficulty-label {
    width: auto;
  }

  .difficulty-count {
    width: auto;
    text-align: left;
  }
}
</style>