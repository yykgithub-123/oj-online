-- 安全添加sourceCode字段的脚本
-- 先检查字段是否存在，如果不存在则添加

-- 检查并添加sourceCode字段
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'yykoj' 
     AND TABLE_NAME = 'question' 
     AND COLUMN_NAME = 'sourceCode') = 0,
    'ALTER TABLE question ADD COLUMN sourceCode text COMMENT "原始代码"',
    'SELECT "sourceCode字段已存在"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 