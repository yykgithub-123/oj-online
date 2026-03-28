// @ts-ignore
/* eslint-disable */
import request from '@/request';

/** getUserDailyActivity GET /api/question_submit/daily/activity */
export async function getUserDailyActivity(options?: { [key: string]: any }) {
  return request<any>('/question_submit/daily/activity', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getTodaySubmissionStats GET /api/question_submit/today/stats */
export async function getTodaySubmissionStats(options?: { [key: string]: any }) {
  return request<any>('/question_submit/today/stats', {
    method: 'GET',
    ...(options || {}),
  });
}