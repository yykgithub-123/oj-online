# 打卡日历功能代码分析文档

## 一、现有代码结构分析

### 1. 前端代码分析

#### 1.1 打卡日历组件位置
- **文件路径**: `oj-frontend/src/views/question/QuestionsView.vue`
- **代码位置**: 第 181-208 行（模板部分），第 347-372 行（逻辑部分）

#### 1.2 日历组件结构
```vue
<!-- 模板部分 -->
<div class="calendar-card">
  <div class="card-header">
    <span class="card-title">打卡日历</span>
    <span class="month-info">8月</span>  <!-- 硬编码月份，动态显示存在问题 -->
  </div>
  <div class="calendar-grid">
    <div class="calendar-header">
      <span>日</span><span>一</span><span>二</span><span>三</span><span>四</span><span>五</span><span>六</span>
    </div>
    <div class="calendar-body">
      <div v-for="day in calendarDays" :key="day.date" class="calendar-day"
        :class="{ 'today': day.isToday, 'checked': day.isChecked, 'other-month': day.isOtherMonth }">
        {{ day.day }}
      </div>
    </div>
  </div>
</div>
```

#### 1.3 核心问题：打卡数据来源
```javascript
// generateCalendar 函数（第 347-372 行）
const generateCalendar = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();
  // ... 日历生成逻辑

  days.push({
    date: currentDate.toISOString().split('T')[0],
    day: currentDate.getDate(),
    isToday: currentDate.toDateString() === today.toDateString(),
    isChecked: Math.random() > 0.8,  // 问题所在：使用随机数据
    isOtherMonth: currentDate.getMonth() !== month
  });
};
```

**问题**: 打卡状态 `isChecked` 使用 `Math.random() > 0.8` 随机生成，未从后端获取真实数据。

#### 1.4 前端 API 文件
- **文件路径**: `oj-frontend/src/api/userController.ts`
- **问题**: 缺少调用打卡数据 API 的方法

### 2. 后端代码分析

#### 2.1 提交记录实体
- **文件路径**: `oj-backend/src/main/java/com/yyk/oj/model/entity/QuestionSubmit.java`
- **关键字段**:
  - `userId`: 提交用户 ID
  - `status`: 判题状态（0-待判题、1-判题中、2-成功、3-失败）
  - `createTime`: 创建时间

#### 2.2 提交状态枚举
- **文件路径**: `oj-backend/src/main/java/com/yyk/oj/model/enums/QuestionSubmitStatusEnum.java`
- 状态值:
  - `WAITING(0)`: 待判题
  - `JUDGING(1)`: 判题中
  - `SUCCEED(2)`: 成功
  - `FAILED(3)`: 失败

#### 2.3 已有 API 接口
- **Controller**: `oj-backend/src/main/java/com/yyk/oj/controller/QuestionSubmitController.java`
- **接口**: `GET /question_submit/daily/activity`
- **问题**: Controller 被标记为 `@Deprecated`

```java
@GetMapping("/daily/activity")
public BaseResponse<List<String>> getUserDailyActivity(HttpServletRequest request) {
    User loginUser = userService.getLoginUser(request);
    List<String> dailyActivity = questionSubmitService.getUserDailyActivity(loginUser.getId());
    return ResultUtils.success(dailyActivity);
}
```

#### 2.4 Service 实现
- **文件路径**: `oj-backend/src/main/java/com/yyk/oj/service/impl/QuestionSubmitServiceImpl.java`
- **方法**: `getUserDailyActivity(Long userId)`（第 278-296 行）

```java
@Override
public List<String> getUserDailyActivity(Long userId) {
    QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("DISTINCT DATE_FORMAT(createTime, '%Y-%m-%d') as activityDate");
    queryWrapper.eq("userId", userId);
    queryWrapper.eq("status", QuestionSubmitStatusEnum.SUCCEED.getValue()); // 只统计成功的
    queryWrapper.eq("isDelete", false);

    List<Map<String, Object>> maps = this.listMaps(queryWrapper);
    return maps.stream()
            .map(map -> (String) map.get("activityDate"))
            .collect(Collectors.toList());
}
```

## 二、问题汇总

| 问题编号 | 问题描述 | 影响范围 |
|---------|---------|---------|
| P1 | 前端打卡状态使用随机数据 | 显示错误 |
| P2 | 前端未调用后端 API | 数据不真实 |
| P3 | 前端月份显示硬编码为"8月" | 动态显示错误 |
| P4 | Controller 标记为 @Deprecated | 可能影响调用 |
| P5 | 前端缺少 API 调用方法 | 无法获取数据 |

## 三、技术栈确认

### 前端
- Vue 3 + TypeScript
- Arco Design Vue UI 框架
- Vuex 状态管理
- Axios HTTP 请求

### 后端
- Spring Boot
- MyBatis Plus ORM
- MySQL 数据库

## 四、数据流分析

### 当前状态（错误）
```
前端 generateCalendar() → Math.random() → 显示随机打卡状态
```

### 期望状态（正确）
```
前端 onMounted() → API 请求 → 后端 getUserDailyActivity() → 查询数据库 → 返回日期列表 → 前端匹配日期显示打卡状态
```

## 五、修复方案概述

1. **后端**: 移除 Controller 的 `@Deprecated` 注解
2. **前端 API**: 新增调用 `/question_submit/daily/activity` 的方法
3. **前端逻辑**: 修改 `generateCalendar` 函数，调用 API 获取真实打卡数据
4. **前端 UI**: 修复月份动态显示问题