# 打卡日历功能开发文档

## 一、开发任务概览

| 步骤 | 任务 | 文件 | 修改内容 |
|------|------|------|---------|
| 1 | 后端接口修复 | QuestionSubmitController.java | 移除 @Deprecated 注解 |
| 2 | 前端 API 方法 | questionSubmitController.ts | 新增获取打卡数据方法 |
| 3 | 前端逻辑修改 | QuestionsView.vue | 调用 API 替换随机数据 |
| 4 | 月份动态显示 | QuestionsView.vue | 修复硬编码月份 |
| 5 | 前端 API 导入 | QuestionsView.vue | 导入新增的 API 方法 |

## 二、详细开发步骤

### 步骤 1: 后端接口修复

**文件**: `oj-backend/src/main/java/com/yyk/oj/controller/QuestionSubmitController.java`

**修改内容**:
- 移除类上的 `@Deprecated` 注解（第 35 行）

**修改前**:
```java
@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {
```

**修改后**:
```java
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {
```

### 步骤 2: 前端 API 文件新增方法

**文件**: `oj-frontend/src/api/questionSubmitController.ts`

**需要检查是否存在该文件，如不存在需创建**

**新增内容**:
```typescript
/** getUserDailyActivity GET /api/question_submit/daily/activity */
export async function getUserDailyActivity(options?: { [key: string]: any }) {
  return request<API.BaseResponseListString_>('/api/question_submit/daily/activity', {
    method: 'GET',
    ...(options || {}),
  });
}
```

### 步骤 3: 前端 QuestionsView.vue 修改

**文件**: `oj-frontend/src/views/question/QuestionsView.vue`

#### 3.1 导入 API 方法

在第 306 行附近，添加导入:
```typescript
import { getUserDailyActivity } from "@/api/questionSubmitController";
```

#### 3.2 添加打卡数据状态

在第 345 行附近，添加:
```typescript
// 打卡日期数据（从后端获取）
const checkedDates = ref<string[]>([]);
```

#### 3.3 修改 generateCalendar 函数

替换第 347-372 行的函数为:
```typescript
// 生成日历数据
const generateCalendar = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const startDate = new Date(firstDay);
  startDate.setDate(startDate.getDate() - firstDay.getDay());

  const days = [];
  for (let i = 0; i < 42; i++) {
    const currentDate = new Date(startDate);
    currentDate.setDate(startDate.getDate() + i);

    const dateStr = currentDate.toISOString().split('T')[0];
    const isChecked = checkedDates.value.includes(dateStr);

    days.push({
      date: dateStr,
      day: currentDate.getDate(),
      isToday: currentDate.toDateString() === today.toDateString(),
      isChecked: isChecked, // 使用真实打卡数据
      isOtherMonth: currentDate.getMonth() !== month
    });
  }

  calendarDays.value = days;
};
```

#### 3.4 新增加载打卡数据函数

在 generateCalendar 函数后添加:
```typescript
// 加载打卡数据
const loadCheckInData = async () => {
  try {
    const res = await getUserDailyActivity();
    if (res.data && res.data.code === 0) {
      checkedDates.value = res.data.data || [];
    }
  } catch (error) {
    console.error("加载打卡数据失败:", error);
  }
};
```

#### 3.5 修改 onMounted

修改第 397-403 行的 onMounted:
```typescript
onMounted(async () => {
  await loadCheckInData(); // 先加载打卡数据
  generateCalendar();      // 再生成日历
  currentUserId.value = getCurrentUserId();
  loadRankingData();
  loadCurrentUserStats();
});
```

#### 3.6 修改月份显示

替换模板中第 184 行的硬编码月份:
```vue
<span class="month-info">{{ currentMonth }}</span>
```

添加计算属性或变量:
```typescript
// 当前月份显示
const currentMonth = ref(new Date().getMonth() + 1 + "月");
```

### 步骤 4: 样式优先级调整（可选）

如果今日同时打卡，确保今日样式和打卡样式的优先级正确：

当前样式（第 1077-1085 行）:
```css
.calendar-day.today {
  background: #4a90e2;
  color: white;
}

.calendar-day.checked {
  background: #52c41a;
  color: white;
}
```

如需要今日打卡时显示绿色，可调整 CSS 优先级。

## 三、测试验证

### 3.1 功能测试

| 测试项 | 测试方法 | 预期结果 |
|--------|---------|---------|
| API 调用 | 浏览器开发者工具查看网络请求 | 成功调用 `/api/question_submit/daily/activity` |
| 打卡显示 | 用户有成功提交记录的日期 | 显示绿色背景 |
| 今日显示 | 当天日期 | 显示蓝色背景 |
| 月份显示 | 查看日历标题 | 显示当前月份（如"3月"） |
| 数据正确 | 对比提交记录和日历显示 | 打卡日期与提交记录一致 |

### 3.2 边界测试

| 测试项 | 测试场景 | 预期结果 |
|--------|---------|---------|
| 无打卡记录 | 用户无任何成功提交 | 日历无绿色标记 |
| 今日打卡 | 今日有成功提交 | 今日显示绿色 |
| 跨月提交 | 有上月提交记录 | 上月日期显示绿色（如果可见） |

## 四、开发注意事项

1. **API 路径**: 注意后端接口路径是 `/api/question_submit/daily/activity`，前端需要加上 `/api` 前缀
2. **响应格式**: 后端返回的是 `{ code, data, message }` 结构，前端需要处理 `res.data.data`
3. **日期格式**: 后端返回 `yyyy-MM-dd` 格式，前端需要使用相同格式匹配
4. **错误处理**: API 调用失败时不应影响页面其他功能
5. **登录状态**: 未登录用户调用 API 可能返回 401，需要处理

## 五、开发依赖

- 前端项目需正常运行
- 后端服务需正常运行
- 用户需登录状态
- 数据库需有提交记录数据

---

# 打卡日历月份切换功能开发文档

**更新时间**: 2026-03-28 17:26

## 一、开发任务概览

| 步骤 | 任务 | 修改内容 |
|------|------|---------|
| 1 | 新增状态变量 | 添加 viewYear、viewMonth 变量 |
| 2 | 修改 generateCalendar 函数 | 使用 viewYear/viewMonth 生成日历 |
| 3 | 新增切换函数 | prevMonth()、nextMonth() |
| 4 | 修改模板 | 添加左右切换按钮 |
| 5 | 添加样式 | 按钮样式 |

## 二、详细开发步骤

### 步骤 1: 新增状态变量

**文件**: `oj-frontend/src/views/question/QuestionsView.vue`

在 `currentMonth` 变量附近添加:

```typescript
// 当前查看的年月
const viewYear = ref(new Date().getFullYear());
const viewMonth = ref(new Date().getMonth());
```

### 步骤 2: 修改 generateCalendar 函数

**修改前**（使用 new Date() 获取当前日期）:
```typescript
const generateCalendar = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();
  // ...
};
```

**修改后**（使用 viewYear/viewMonth）:
```typescript
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
```

### 步骤 3: 新增切换函数

在 `generateCalendar` 函数后添加:

```typescript
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
```

### 步骤 4: 修改模板

**修改前**:
```vue
<div class="card-header">
  <span class="card-title">打卡日历</span>
  <span class="month-info">{{ currentMonth }}</span>
</div>
```

**修改后**:
```vue
<div class="card-header">
  <span class="card-title">打卡日历</span>
  <div class="month-selector">
    <span class="month-btn" @click="prevMonth">&lt;</span>
    <span class="month-info">{{ currentMonth }}</span>
    <span class="month-btn" @click="nextMonth">&gt;</span>
  </div>
</div>
```

### 步骤 5: 添加样式

在 `.calendar-card` 相关样式后添加:

```css
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
}

.month-btn:hover {
  background: #f0f7ff;
  color: #4a90e2;
}
```

## 三、测试验证

| 测试项 | 测试方法 | 预期结果 |
|--------|---------|---------|
| 左侧按钮 | 点击左箭头 | 日历切换到上一月 |
| 右侧按钮 | 点击右箭头 | 日历切换到下一月 |
| 跨年处理 | 1月点左箭头 | 显示上年12月 |
| 跨年处理 | 12月点右箭头 | 显示下年1月 |
| 月份显示 | 切换后检查标题 | 显示正确的年月 |
| 打卡数据 | 切换月份后检查 | 打卡标记仍正确 |
| 今日标记 | 切换月份后回到当前月 | 今日标记正确 |