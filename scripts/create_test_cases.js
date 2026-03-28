const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
        Header, Footer, AlignmentType, LevelFormat, HeadingLevel, BorderStyle,
        WidthType, ShadingType, PageNumber, PageBreak } = require('docx');
const fs = require('fs');

const border = { style: BorderStyle.SINGLE, size: 1, color: "CCCCCC" };
const borders = { top: border, bottom: border, left: border, right: border };

function createCell(text, options = {}) {
    const { bold = false, header = false, width = 2340 } = options;
    return new TableCell({
        borders,
        width: { size: width, type: WidthType.DXA },
        shading: header ? { fill: "E7E6E6", type: ShadingType.CLEAR } : undefined,
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new Paragraph({
            children: [new TextRun({ text, bold: bold || header, size: 20 })]
        })]
    });
}

function createTestTable(testCases) {
    const colWidths = [800, 2000, 3000, 2000, 1560];
    const headers = ['编号', '测试项', '测试步骤', '预期结果', '优先级'];

    const rows = [
        new TableRow({
            children: headers.map((h, i) => createCell(h, { header: true, width: colWidths[i] }))
        })
    ];

    testCases.forEach((tc, idx) => {
        rows.push(new TableRow({
            children: [
                createCell(`TC-${String(idx + 1).padStart(3, '0')}`, { width: colWidths[0] }),
                createCell(tc.name, { width: colWidths[1] }),
                createCell(tc.steps, { width: colWidths[2] }),
                createCell(tc.expected, { width: colWidths[3] }),
                createCell(tc.priority, { width: colWidths[4] })
            ]
        }));
    });

    return new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: colWidths,
        rows
    });
}

const userTestCases = [
    { name: '用户注册-正常流程', steps: '1. 输入用户账号：testuser001\n2. 输入密码：Test@123456\n3. 输入确认密码：Test@123456\n4. 点击注册按钮', expected: '注册成功，返回用户ID，页面跳转至登录页', priority: '高' },
    { name: '用户注册-账号为空', steps: '1. 用户账号留空\n2. 输入密码：Test@123456\n3. 点击注册按钮', expected: '注册失败，提示"参数错误"', priority: '高' },
    { name: '用户注册-密码不一致', steps: '1. 输入用户账号：testuser002\n2. 输入密码：Test@123456\n3. 输入确认密码：Test@654321\n4. 点击注册按钮', expected: '注册失败，提示"两次输入的密码不一致"', priority: '高' },
    { name: '用户注册-账号重复', steps: '1. 使用已存在的账号注册\n2. 输入相同的用户账号\n3. 点击注册按钮', expected: '注册失败，提示"账号已存在"', priority: '高' },
    { name: '用户注册-密码长度不足', steps: '1. 输入用户账号：testuser003\n2. 输入密码：123\n3. 输入确认密码：123\n4. 点击注册按钮', expected: '注册失败，提示"密码长度不符合要求"', priority: '中' },
    { name: '用户登录-正常流程', steps: '1. 输入正确的用户账号\n2. 输入正确的密码\n3. 点击登录按钮', expected: '登录成功，返回用户信息，跳转至首页', priority: '高' },
    { name: '用户登录-密码错误', steps: '1. 输入正确的用户账号\n2. 输入错误的密码\n3. 点击登录按钮', expected: '登录失败，提示"账号或密码错误"', priority: '高' },
    { name: '用户登录-账号不存在', steps: '1. 输入不存在的用户账号\n2. 输入任意密码\n3. 点击登录按钮', expected: '登录失败，提示"账号或密码错误"', priority: '高' },
    { name: '用户注销', steps: '1. 用户已登录状态\n2. 点击退出登录按钮', expected: '注销成功，清除Session，跳转至登录页', priority: '高' },
    { name: '获取当前登录用户', steps: '1. 用户已登录状态\n2. 调用 /user/get/login 接口', expected: '返回当前登录用户的脱敏信息', priority: '中' },
    { name: '管理员创建用户', steps: '1. 管理员登录\n2. 调用 /user/add 接口\n3. 填写新用户信息', expected: '创建成功，返回新用户ID，默认密码12345678', priority: '中' },
    { name: '管理员删除用户', steps: '1. 管理员登录\n2. 调用 /user/delete 接口\n3. 传入要删除的用户ID', expected: '删除成功，返回true', priority: '中' },
    { name: '普通用户尝试删除用户', steps: '1. 普通用户登录\n2. 尝试调用 /user/delete 接口', expected: '删除失败，提示"无权限"', priority: '高' },
    { name: '更新个人信息', steps: '1. 用户登录\n2. 调用 /user/update/my 接口\n3. 修改昵称、头像、简介', expected: '更新成功，返回true', priority: '中' },
    { name: '分页获取用户列表', steps: '1. 管理员登录\n2. 调用 /user/list/page 接口\n3. 指定页码和每页数量', expected: '返回分页用户列表', priority: '中' },
    { name: '获取用户排行榜', steps: '1. 调用 /user/ranking 接口\n2. 指定返回数量limit=10', expected: '返回前10名用户的统计数据列表', priority: '中' },
    { name: '获取用户统计信息', steps: '1. 调用 /user/stats/{userId} 接口', expected: '返回该用户的提交数、通过数、排名等统计信息', priority: '中' }
];

const questionTestCases = [
    { name: '创建题目-正常流程', steps: '1. 用户登录\n2. 调用 /question/add 接口\n3. 填写标题、内容、标签、难度、判题用例等', expected: '创建成功，返回题目ID', priority: '高' },
    { name: '创建题目-标题为空', steps: '1. 用户登录\n2. 标题留空\n3. 填写其他必填项\n4. 提交创建', expected: '创建失败，提示"参数错误"', priority: '高' },
    { name: '创建题目-难度非法', steps: '1. 填写难度为"超难"\n2. 提交创建', expected: '创建失败，提示难度参数非法', priority: '中' },
    { name: '删除题目-本人删除', steps: '1. 题目创建者登录\n2. 调用 /question/delete 接口', expected: '删除成功，返回true', priority: '高' },
    { name: '删除题目-非本人删除', steps: '1. 非题目创建者登录\n2. 尝试删除他人题目', expected: '删除失败，提示"无权限"', priority: '高' },
    { name: '管理员删除任意题目', steps: '1. 管理员登录\n2. 删除任意题目', expected: '删除成功', priority: '中' },
    { name: '更新题目-管理员', steps: '1. 管理员登录\n2. 调用 /question/update 接口\n3. 修改题目信息', expected: '更新成功', priority: '中' },
    { name: '编辑题目-本人编辑', steps: '1. 题目创建者登录\n2. 调用 /question/edit 接口\n3. 修改题目内容', expected: '编辑成功', priority: '高' },
    { name: '根据ID获取题目-管理员', steps: '1. 管理员登录\n2. 调用 /question/get?id=xxx', expected: '返回题目完整信息（含答案、测试用例）', priority: '中' },
    { name: '根据ID获取题目-普通用户', steps: '1. 普通用户登录\n2. 尝试获取非本人创建的题目', expected: '获取失败，提示"无权限"', priority: '高' },
    { name: '获取题目脱敏信息', steps: '1. 调用 /question/get/vo?id=xxx', expected: '返回题目脱敏信息（不含答案、测试用例）', priority: '中' },
    { name: '分页获取题目列表', steps: '1. 调用 /question/list/page/vo 接口\n2. 指定页码current=1，pageSize=10', expected: '返回第1页10条题目列表', priority: '高' },
    { name: '分页获取-限制爬虫', steps: '1. 指定pageSize=50（超过20）', expected: '请求失败，提示"参数错误"', priority: '中' },
    { name: '获取所有标签', steps: '1. 调用 /question/tags 接口', expected: '返回所有标签列表', priority: '低' },
    { name: '获取热门标签', steps: '1. 调用 /question/tags/popular?limit=5', expected: '返回使用量前5的标签', priority: '低' },
    { name: '高级搜索题目', steps: '1. 调用 /question/search/advanced 接口\n2. 指定标题关键词、难度、标签、时间范围', expected: '返回符合条件的题目列表', priority: '中' },
    { name: '批量删除题目', steps: '1. 管理员登录\n2. 调用 /question/batch/delete 接口\n3. 传入多个题目ID', expected: '删除成功，返回删除数量', priority: '中' },
    { name: '获取题目统计信息', steps: '1. 调用 /question/stats 接口', expected: '返回总题目数、总提交数、通过数、成功率', priority: '低' }
];

const submitTestCases = [
    { name: '提交代码-正常流程', steps: '1. 用户登录\n2. 调用 /question/question_submit/do 接口\n3. 传入题目ID、语言、代码', expected: '提交成功，返回提交记录ID', priority: '高' },
    { name: '提交代码-未登录', steps: '1. 不登录直接调用提交接口', expected: '提交失败，提示"未登录"', priority: '高' },
    { name: '提交代码-题目ID非法', steps: '1. 传入不存在的题目ID\n2. 提交代码', expected: '提交失败，提示"题目不存在"', priority: '高' },
    { name: '提交代码-语言不支持', steps: '1. 传入language="python"\n2. 提交代码', expected: '根据系统配置判断是否支持，不支持则提示错误', priority: '中' },
    { name: '提交代码-代码为空', steps: '1. 传入空代码字符串\n2. 提交', expected: '提交失败，提示"代码不能为空"', priority: '高' },
    { name: '分页获取提交列表', steps: '1. 调用 /question/question_submit/list/page 接口\n2. 指定页码和每页数量', expected: '返回分页提交记录列表', priority: '高' },
    { name: '普通用户查看提交记录', steps: '1. 普通用户登录\n2. 查看提交列表', expected: '返回脱敏后的提交信息（不包含他人代码详情）', priority: '高' },
    { name: '管理员查看提交记录', steps: '1. 管理员登录\n2. 查看提交列表', expected: '返回完整的提交信息（包含代码详情）', priority: '中' },
    { name: '获取今日提交统计', steps: '1. 调用 /question_submit/today/stats 接口', expected: '返回今日提交数、通过数、活跃用户数', priority: '低' },
    { name: '获取用户每日活跃情况', steps: '1. 用户登录\n2. 调用 /question_submit/daily/activity 接口', expected: '返回用户每日提交活跃数据', priority: '低' },
    { name: '提交代码-Java语言', steps: '1. 选择语言为Java\n2. 提交正确Java代码', expected: '提交成功，进入判题流程', priority: '高' },
    { name: '提交代码-超长代码', steps: '1. 提交超过限定长度的代码', expected: '根据系统限制判断，超长则拒绝', priority: '中' }
];

const judgeTestCases = [
    { name: '判题-答案正确', steps: '1. 提交正确代码\n2. 代码通过所有测试用例\n3. 系统执行判题', expected: '判题结果为Accepted，状态为成功', priority: '高' },
    { name: '判题-答案错误', steps: '1. 提交错误代码\n2. 代码输出与预期不符', expected: '判题结果为Wrong Answer', priority: '高' },
    { name: '判题-编译错误', steps: '1. 提交语法错误的代码\n2. 无法通过编译', expected: '判题结果为Compile Error', priority: '高' },
    { name: '判题-超时', steps: '1. 提交耗时超过时间限制的代码', expected: '判题结果为Time Limit Exceeded', priority: '高' },
    { name: '判题-内存溢出', steps: '1. 提交占用内存超过限制的代码', expected: '判题结果为Memory Limit Exceeded', priority: '高' },
    { name: '判题-运行时错误', steps: '1. 提交会导致运行时异常的代码（如空指针、数组越界）', expected: '判题结果为Runtime Error', priority: '高' },
    { name: '判题-重复判题', steps: '1. 对同一提交记录重复调用判题接口', expected: '第二次调用失败，提示"题目正在判题中"', priority: '中' },
    { name: '判题-状态转换', steps: '1. 观察判题过程状态变化\n2. 等待-判题中-成功/失败', expected: '状态按顺序正确转换：0-1-2/3', priority: '高' },
    { name: '判题-题目不存在', steps: '1. 提交记录关联的题目被删除\n2. 执行判题', expected: '判题失败，提示"题目不存在"', priority: '中' },
    { name: '判题-更新通过数', steps: '1. 提交正确答案\n2. 检查题目通过数是否+1', expected: '题目的acceptedNum字段增加1', priority: '高' },
    { name: '判题策略-Java特殊处理', steps: '1. 提交Java代码\n2. 验证JavaLanguageJudgeStrategy被调用', expected: '使用Java特定判题策略处理', priority: '中' },
    { name: '判题-沙箱异常处理', steps: '1. 沙箱执行返回异常状态\n2. 系统处理异常结果', expected: '正确记录错误信息，状态设为失败', priority: '高' }
];

const sandboxTestCases = [
    { name: '沙箱执行-简单计算', steps: '1. 提交简单计算代码\n2. 传入输入参数', expected: '正确执行并返回计算结果', priority: '高' },
    { name: '沙箱执行-带参数执行', steps: '1. 提交读取命令行参数的代码\n2. 传入多个输入参数', expected: '正确解析参数并返回结果', priority: '高' },
    { name: '沙箱执行-编译错误', steps: '1. 提交语法错误的Java代码', expected: '返回编译错误信息，状态为失败', priority: '高' },
    { name: '沙箱执行-超时控制', steps: '1. 提交死循环代码\n2. 等待执行', expected: '超过5000ms后终止进程，返回超时错误', priority: '高' },
    { name: '沙箱安全-读取文件', steps: '1. 提交尝试读取文件的代码\n2. 使用FileInputStream读取系统文件', expected: '被安全管理器阻止，抛出安全异常', priority: '高' },
    { name: '沙箱安全-写入文件', steps: '1. 提交尝试写入文件的代码\n2. 使用FileOutputStream写入文件', expected: '被安全管理器阻止，抛出安全异常', priority: '高' },
    { name: '沙箱安全-执行外部程序', steps: '1. 提交尝试执行Runtime.exec的代码\n2. 尝试启动外部进程', expected: '被安全管理器阻止，抛出安全异常', priority: '高' },
    { name: '沙箱安全-内存攻击', steps: '1. 提交创建大量对象的代码\n2. 尝试耗尽内存', expected: '受-Xmx256m限制，抛出OutOfMemoryError', priority: '高' },
    { name: '沙箱安全-睡眠攻击', steps: '1. 提交Thread.sleep(Long.MAX_VALUE)代码', expected: '被超时控制机制中断', priority: '中' },
    { name: '沙箱-文件隔离', steps: '1. 每次执行使用独立目录\n2. 验证UUID生成的目录名', expected: '每次执行使用不同目录，互不影响', priority: '中' },
    { name: '沙箱-文件清理', steps: '1. 执行代码后\n2. 检查临时文件是否被删除', expected: '临时代码文件被正确清理', priority: '中' },
    { name: '沙箱-内存限制', steps: '1. 验证JVM启动参数-Xmx256m\n2. 检查内存限制是否生效', expected: '进程内存被限制在256MB以内', priority: '高' },
    { name: '沙箱-Docker隔离执行', steps: '1. 使用Docker容器执行代码\n2. 验证容器隔离效果', expected: '代码在容器中安全执行', priority: '高' },
    { name: '沙箱-响应格式', steps: '1. 执行任意代码\n2. 检查返回的ExecuteCodeResponse格式', expected: '包含outputList、status、message、judgeInfo', priority: '中' },
    { name: '沙箱-多输入用例', steps: '1. 传入多个测试用例输入\n2. 逐一执行并收集结果', expected: '返回每个用例的执行结果列表', priority: '高' }
];

const doc = new Document({
    styles: {
        default: { document: { run: { font: "SimSun", size: 24 } } },
        paragraphStyles: [
            { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
                run: { size: 32, bold: true, font: "SimHei" },
                paragraph: { spacing: { before: 360, after: 240 }, outlineLevel: 0 } },
            { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
                run: { size: 28, bold: true, font: "SimHei" },
                paragraph: { spacing: { before: 240, after: 180 }, outlineLevel: 1 } },
            { id: "Heading3", name: "Heading 3", basedOn: "Normal", next: "Normal", quickFormat: true,
                run: { size: 24, bold: true, font: "SimHei" },
                paragraph: { spacing: { before: 180, after: 120 }, outlineLevel: 2 } }
        ]
    },
    numbering: {
        config: [
            { reference: "bullets",
                levels: [{ level: 0, format: LevelFormat.BULLET, text: "\u2022", alignment: AlignmentType.LEFT,
                    style: { paragraph: { indent: { left: 720, hanging: 360 } } } }] }
        ]
    },
    sections: [{
        properties: {
            page: {
                size: { width: 12240, height: 15840 },
                margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
            }
        },
        headers: {
            default: new Header({
                children: [new Paragraph({
                    children: [new TextRun({ text: "在线判题系统测试用例文档", size: 20, color: "666666" })],
                    alignment: AlignmentType.CENTER
                })]
            })
        },
        footers: {
            default: new Footer({
                children: [new Paragraph({
                    children: [new TextRun("第 "), new TextRun({ children: [PageNumber.CURRENT] }), new TextRun(" 页")],
                    alignment: AlignmentType.CENTER
                })]
            })
        },
        children: [
            new Paragraph({
                heading: HeadingLevel.HEADING_1,
                alignment: AlignmentType.CENTER,
                children: [new TextRun({ text: "在线判题系统测试用例设计", bold: true, size: 36 })]
            }),
            new Paragraph({
                alignment: AlignmentType.CENTER,
                children: [new TextRun({ text: "Online Judge System Test Case Design", italics: true, size: 24 })]
            }),
            new Paragraph({ children: [] }),

            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("1 引言")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("1.1 编写目的")] }),
            new Paragraph({
                children: [new TextRun("本文档旨在为在线判题（Online Judge）系统提供全面、系统的测试用例设计，覆盖用户模块、题目模块、代码提交模块、判题模块及代码沙箱模块的核心功能，确保系统功能的正确性和稳定性。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("1.2 测试范围")] }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("用户模块：用户注册、登录、注销、权限管理、用户统计")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("题目模块：题目增删改查、标签管理、高级搜索")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("代码提交模块：代码提交、提交记录查询、统计分析")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("判题模块：判题流程、判题策略、结果判定")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("代码沙箱模块：代码执行、安全隔离、资源控制")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("1.3 测试环境")] }),
            new Paragraph({
                children: [new TextRun("操作系统：Windows 10 / Linux\n后端框架：Spring Boot + MyBatis-Plus\n数据库：MySQL 8.0\nJDK版本：JDK 8+\n代码沙箱：Java原生执行 / Docker容器隔离")]
            }),

            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("2 用户模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("2.1 功能概述")] }),
            new Paragraph({
                children: [new TextRun("用户模块负责系统的用户管理功能，包括用户注册、登录、注销、权限校验、用户信息管理等核心功能。该模块采用Session会话管理机制，支持管理员和普通用户两种角色，实现了基于角色的访问控制（RBAC）。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("2.2 测试用例列表")] }),
            createTestTable(userTestCases),

            new Paragraph({ children: [new PageBreak()] }),
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("3 题目模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("3.1 功能概述")] }),
            new Paragraph({
                children: [new TextRun("题目模块是OJ系统的核心功能模块之一，负责题目的全生命周期管理。模块支持题目的创建、编辑、删除、查询等操作，包含题目内容、标签分类、难度等级、判题用例、判题配置等关键信息的管理。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("3.2 测试用例列表")] }),
            createTestTable(questionTestCases),

            new Paragraph({ children: [new PageBreak()] }),
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("4 代码提交模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("4.1 功能概述")] }),
            new Paragraph({
                children: [new TextRun("代码提交模块负责用户代码的提交、存储和查询功能。用户可选择编程语言，提交代码进行判题。模块记录提交时间、代码内容、判题结果、执行时间、内存消耗等信息，并提供统计分析功能。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("4.2 测试用例列表")] }),
            createTestTable(submitTestCases),

            new Paragraph({ children: [new PageBreak()] }),
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("5 判题模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("5.1 功能概述")] }),
            new Paragraph({
                children: [new TextRun("判题模块是OJ系统的核心业务逻辑模块，负责调用代码沙箱执行用户提交的代码，并根据执行结果进行判定。模块采用策略模式设计，支持不同编程语言的差异化判题策略，能够处理Accepted、Wrong Answer、Time Limit Exceeded、Memory Limit Exceeded、Compile Error、Runtime Error等多种判题结果。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("5.2 判题状态枚举")] }),
            new Paragraph({
                children: [new TextRun("系统定义了以下判题状态：")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("WAITING(0)：等待判题")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("RUNNING(1)：判题中")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("SUCCEED(2)：判题成功")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("FAILED(3)：判题失败")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("5.3 测试用例列表")] }),
            createTestTable(judgeTestCases),

            new Paragraph({ children: [new PageBreak()] }),
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("6 代码沙箱模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("6.1 功能概述")] }),
            new Paragraph({
                children: [new TextRun("代码沙箱模块是系统安全运行的核心组件，负责在隔离环境中安全执行用户提交的代码。模块实现了代码文件保存、编译、执行、结果收集、文件清理的完整流程，并通过安全管理器和资源限制机制防止恶意代码对系统造成危害。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("6.2 安全机制")] }),
            new Paragraph({
                children: [new TextRun("系统实现了多层安全防护机制：")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("文件系统隔离：每次执行使用独立的UUID目录，执行完成后自动清理")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("安全管理器：自定义SecurityManager阻止文件读写、网络访问、进程创建等危险操作")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("资源限制：通过JVM参数限制内存使用(-Xmx256m)，超时机制限制执行时间(5000ms)")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("Docker容器隔离（可选）：在容器中执行代码，实现更强的隔离效果")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("6.3 测试用例列表")] }),
            createTestTable(sandboxTestCases),

            new Paragraph({ children: [new PageBreak()] }),
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("7 测试用例统计")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("7.1 用例数量统计")] }),
            new Table({
                width: { size: 9360, type: WidthType.DXA },
                columnWidths: [3120, 3120, 3120],
                rows: [
                    new TableRow({
                        children: [
                            createCell("模块名称", { header: true, width: 3120 }),
                            createCell("测试用例数量", { header: true, width: 3120 }),
                            createCell("优先级-高", { header: true, width: 3120 })
                        ]
                    }),
                    new TableRow({ children: [createCell("用户模块", { width: 3120 }), createCell("17", { width: 3120 }), createCell("9", { width: 3120 })] }),
                    new TableRow({ children: [createCell("题目模块", { width: 3120 }), createCell("18", { width: 3120 }), createCell("7", { width: 3120 })] }),
                    new TableRow({ children: [createCell("代码提交模块", { width: 3120 }), createCell("12", { width: 3120 }), createCell("7", { width: 3120 })] }),
                    new TableRow({ children: [createCell("判题模块", { width: 3120 }), createCell("12", { width: 3120 }), createCell("8", { width: 3120 })] }),
                    new TableRow({ children: [createCell("代码沙箱模块", { width: 3120 }), createCell("15", { width: 3120 }), createCell("10", { width: 3120 })] }),
                    new TableRow({
                        children: [
                            createCell("合计", { bold: true, width: 3120 }),
                            createCell("74", { bold: true, width: 3120 }),
                            createCell("41", { bold: true, width: 3120 })
                        ]
                    })
                ]
            }),
            new Paragraph({ children: [] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("7.2 优先级分布")] }),
            new Paragraph({
                children: [new TextRun("本次测试用例设计共包含74个测试用例，其中高优先级用例41个，占比55.4%；中优先级用例26个，占比35.1%；低优先级用例7个，占比9.5%。高优先级用例主要覆盖核心业务流程和安全相关功能。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("7.3 测试覆盖率分析")] }),
            new Paragraph({
                children: [new TextRun("测试用例设计遵循以下原则：")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("正向测试：验证功能在正常输入下的正确行为")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("逆向测试：验证系统对非法输入的处理能力")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("边界测试：验证系统在边界条件下的表现")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("安全测试：验证系统的安全防护机制")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("权限测试：验证权限控制机制的完整性")]
            })
        ]
    }]
});

Packer.toBuffer(doc).then(buffer => {
    fs.writeFileSync("E:/Project/JAVA/ideaProjects/oj/在线判题系统测试用例.docx", buffer);
    console.log("文档已生成: E:/Project/JAVA/ideaProjects/oj/在线判题系统测试用例.docx");
});