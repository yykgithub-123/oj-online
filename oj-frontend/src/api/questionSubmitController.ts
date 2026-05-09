// @ts-ignore
/* eslint-disable */
import request from '@/request';

/** doQuestionSubmit POST /api/question_submit/do */
export async function doQuestionSubmit(body: any, options?: { [key: string]: any }) {
  return request<any>('/question_submit/do', {
    method: 'POST',
    data: body,
    ...(options || {}),
  });
}

/** listQuestionSubmitByPage POST /api/question_submit/list/page */
export async function listQuestionSubmitByPage(body: any, options?: { [key: string]: any }) {
  return request<any>('/question_submit/list/page', {
    method: 'POST',
    data: body,
    ...(options || {}),
  });
}

/** getSubmitStats GET /api/question_submit/stats */
export async function getSubmitStats(options?: { [key: string]: any }) {
  return request<any>('/question_submit/stats', {
    method: 'GET',
    ...(options || {}),
  });
}

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