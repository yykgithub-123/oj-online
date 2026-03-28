import myAxios from '../request';

/**
 * 获取题目统计信息
 */
export const getQuestionStats = async () => {
  return await myAxios.get('/api/question/stats');
};

/**
 * 获取用户完成统计
 */
export const getUserCompletionStats = async () => {
  return await myAxios.get('/api/user/completion/stats');
};

/**
 * 获取今日提交统计
 */
export const getTodaySubmissionStats = async () => {
  return await myAxios.get('/api/submission/today/stats');
};

/**
 * 获取系统状态
 */
export const getSystemStatus = async () => {
  return await myAxios.get('/api/system/status');
};

/**
 * 获取活跃用户列表
 */
export const getActiveUsers = async (params: {
  days?: number;
  pageSize?: number;
  current?: number;
}) => {
  return await myAxios.get('/api/user/active', { params });
};

/**
 * 高级搜索题目
 */
export const advancedSearchQuestions = async (data: any) => {
  return await myAxios.post('/api/question/search/advanced', data);
};

/**
 * 批量删除题目
 */
export const batchDeleteQuestions = async (ids: number[]) => {
  return await myAxios.post('/api/question/batch/delete', { ids });
};

/**
 * 更新题目难度
 */
export const updateQuestionDifficulty = async (id: number, difficulty: string) => {
  return await myAxios.post(`/api/question/${id}/difficulty`, { difficulty });
};

/**
 * 获取题目预览信息
 */
export const getQuestionPreview = async (id: number) => {
  return await myAxios.get(`/api/question/${id}/preview`);
};

/**
 * 获取所有标签
 */
export const getAllTags = async () => {
  return await myAxios.get('/api/tags');
};

/**
 * 添加新标签
 */
export const addTag = async (data: { name: string; description: string }) => {
  return await myAxios.post('/api/tags', data);
}; 