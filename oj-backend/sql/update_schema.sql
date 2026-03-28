-- 数据库更新脚本
-- 为Question表添加difficulty字段
ALTER TABLE question ADD COLUMN difficulty varchar(32) DEFAULT '中等' COMMENT '难度：简单/中等/困难';

-- 为Question表添加sourceCode字段
ALTER TABLE question ADD COLUMN sourceCode text COMMENT '原始代码';

-- 为QuestionSubmit表添加性能指标字段
ALTER TABLE question_submit ADD COLUMN executionTime int DEFAULT 0 COMMENT '执行时间（毫秒）';
ALTER TABLE question_submit ADD COLUMN memoryUsage int DEFAULT 0 COMMENT '内存使用量（KB）';
ALTER TABLE question_submit ADD COLUMN codeLength int DEFAULT 0 COMMENT '代码长度';

-- 添加索引以提高查询性能
CREATE INDEX idx_question_difficulty ON question(difficulty);
CREATE INDEX idx_question_submit_status ON question_submit(status);
CREATE INDEX idx_question_submit_create_time ON question_submit(createTime); 