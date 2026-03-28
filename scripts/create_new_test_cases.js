const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
        Header, Footer, AlignmentType, LevelFormat, HeadingLevel, BorderStyle,
        WidthType, ShadingType, PageNumber, PageBreak } = require('docx');
const fs = require('fs');

const border = { style: BorderStyle.SINGLE, size: 1, color: "CCCCCC" };
const borders = { top: border, bottom: border, left: border, right: border };

function createCell(text, options = {}) {
    const { bold = false, header = false, width = 2340, fontSize = 20 } = options;
    return new TableCell({
        borders,
        width: { size: width, type: WidthType.DXA },
        shading: header ? { fill: "D5E8F0", type: ShadingType.CLEAR } : undefined,
        margins: { top: 80, bottom: 80, left: 120, right: 120 },
        children: [new Paragraph({
            children: [new TextRun({ text, bold: bold || header, size: fontSize })]
        })]
    });
}

function createTestTable(testCases) {
    const colWidths = [700, 1800, 3200, 2200, 800, 660];
    const headers = ['ID', '测试项', '测试步骤', '预期结果', '优先级', '类型'];

    const rows = [
        new TableRow({
            children: headers.map((h, i) => createCell(h, { header: true, width: colWidths[i], fontSize: 18 }))
        })
    ];

    testCases.forEach((tc) => {
        rows.push(new TableRow({
            children: [
                createCell(tc.id, { width: colWidths[0], fontSize: 18 }),
                createCell(tc.name, { width: colWidths[1], fontSize: 18 }),
                createCell(tc.steps, { width: colWidths[2], fontSize: 18 }),
                createCell(tc.expected, { width: colWidths[3], fontSize: 18 }),
                createCell(tc.priority, { width: colWidths[4], fontSize: 18 }),
                createCell(tc.type, { width: colWidths[5], fontSize: 18 })
            ]
        }));
    });

    return new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: colWidths,
        rows
    });
}

function createSimpleTable(data, colWidths) {
    const rows = [
        new TableRow({
            children: data[0].map((h, i) => createCell(h, { header: true, width: colWidths[i] }))
        })
    ];
    data.slice(1).forEach(row => {
        rows.push(new TableRow({
            children: row.map((cell, i) => createCell(cell, { width: colWidths[i], bold: i === 0 && data[0].length > 2 }))
        }));
    });
    return new Table({
        width: { size: 9360, type: WidthType.DXA },
        columnWidths: colWidths,
        rows
    });
}

// ==================== 测试用例数据 ====================

const userModuleTests = [
    // Happy Path
    { id: 'UM-001', name: '用户注册-正常流程', steps: '1. 输入账号: testuser001\n2. 输入密码: Test@123456\n3. 确认密码: Test@123456\n4. 点击注册', expected: '返回用户ID，状态码200', priority: 'P0', type: '功能' },
    { id: 'UM-002', name: '用户登录-正常流程', steps: '1. 输入正确账号密码\n2. 点击登录', expected: '返回LoginUserVO，Session有效', priority: 'P0', type: '功能' },
    { id: 'UM-003', name: '用户注销-正常流程', steps: '1. 已登录状态\n2. 点击退出', expected: 'Session清除，返回true', priority: 'P0', type: '功能' },
    { id: 'UM-004', name: '管理员创建用户', steps: '1. 管理员登录\n2. 调用/user/add\n3. 填写用户信息', expected: '创建成功，默认密码12345678', priority: 'P1', type: '功能' },
    { id: 'UM-005', name: '更新个人信息', steps: '1. 用户登录\n2. 修改昵称/头像/简介\n3. 提交', expected: '更新成功，返回true', priority: 'P1', type: '功能' },

    // Error Path
    { id: 'UM-006', name: '注册-账号为空', steps: '1. 账号留空\n2. 填写密码\n3. 提交', expected: '返回400，提示"参数错误"', priority: 'P0', type: '异常' },
    { id: 'UM-007', name: '注册-密码不一致', steps: '1. 密码: Test@123456\n2. 确认: Test@654321\n3. 提交', expected: '返回400，提示"两次密码不一致"', priority: 'P0', type: '异常' },
    { id: 'UM-008', name: '注册-账号重复', steps: '1. 使用已存在账号注册', expected: '返回400，提示"账号已存在"', priority: 'P0', type: '异常' },
    { id: 'UM-009', name: '注册-密码长度不足', steps: '1. 密码: 123\n2. 确认: 123\n3. 提交', expected: '返回400，提示"密码长度不足"', priority: 'P1', type: '边界' },
    { id: 'UM-010', name: '登录-密码错误', steps: '1. 正确账号\n2. 错误密码\n3. 提交', expected: '返回401，提示"账号或密码错误"', priority: 'P0', type: '异常' },
    { id: 'UM-011', name: '登录-账号不存在', steps: '1. 不存在的账号\n2. 任意密码\n3. 提交', expected: '返回401，提示"账号或密码错误"', priority: 'P0', type: '异常' },

    // Security
    { id: 'UM-012', name: '权限-普通用户删除用户', steps: '1. 普通用户登录\n2. 调用/user/delete', expected: '返回403，提示"无权限"', priority: 'P0', type: '安全' },
    { id: 'UM-013', name: '权限-非管理员访问管理接口', steps: '1. 普通用户调用/user/add', expected: '返回403，AuthCheck拦截', priority: 'P0', type: '安全' },
    { id: 'UM-014', name: 'Session-未登录访问保护接口', steps: '1. 无Session调用/user/update/my', expected: '返回401，提示"未登录"', priority: 'P0', type: '安全' },

    // Boundary
    { id: 'UM-015', name: '分页-pageSize边界值', steps: '1. pageSize=20\n2. pageSize=21', expected: '20正常返回，21返回400', priority: 'P1', type: '边界' },
    { id: 'UM-016', name: '账号-特殊字符', steps: '1. 账号含SQL特殊字符: admin\'--', expected: '系统正常处理，无注入风险', priority: 'P1', type: '安全' },

    // Performance
    { id: 'UM-017', name: '登录-响应时间', steps: '1. 正常登录请求\n2. 测量响应时间', expected: 'p95 < 200ms', priority: 'P2', type: '性能' },
    { id: 'UM-018', name: '用户列表-大数据量', steps: '1. 查询1000用户分页', expected: '响应时间 < 500ms', priority: 'P2', type: '性能' },
];

const questionModuleTests = [
    // Happy Path
    { id: 'QM-001', name: '创建题目-正常流程', steps: '1. 登录\n2. 填写标题/内容/标签/难度/用例\n3. 提交', expected: '返回题目ID', priority: 'P0', type: '功能' },
    { id: 'QM-002', name: '删除题目-本人', steps: '1. 创建者登录\n2. 调用/question/delete', expected: '返回true，题目标记删除', priority: 'P0', type: '功能' },
    { id: 'QM-003', name: '编辑题目-本人', steps: '1. 创建者登录\n2. 修改题目信息\n3. 提交', expected: '返回true，题目更新成功', priority: 'P0', type: '功能' },
    { id: 'QM-004', name: '获取题目列表', steps: '1. 调用/question/list/page/vo\n2. current=1, pageSize=10', expected: '返回10条题目VO列表', priority: 'P0', type: '功能' },
    { id: 'QM-005', name: '获取题目脱敏信息', steps: '1. 调用/question/get/vo?id=xxx', expected: '返回题目VO，不含答案', priority: 'P1', type: '功能' },

    // Error Path
    { id: 'QM-006', name: '创建-标题为空', steps: '1. 标题留空\n2. 填写其他\n3. 提交', expected: '返回400，参数校验失败', priority: 'P0', type: '异常' },
    { id: 'QM-007', name: '删除-非本人', steps: '1. 非创建者登录\n2. 尝试删除', expected: '返回403，无权限', priority: 'P0', type: '安全' },
    { id: 'QM-008', name: '获取-题目不存在', steps: '1. 调用/question/get/vo?id=999999', expected: '返回404，题目不存在', priority: 'P1', type: '异常' },

    // Security
    { id: 'QM-009', name: '权限-管理员更新任意题目', steps: '1. 管理员登录\n2. 更新他人题目', expected: '更新成功', priority: 'P1', type: '安全' },
    { id: 'QM-010', name: '权限-普通用户获取完整题目', steps: '1. 普通用户调用/question/get', expected: '返回403，无权限查看答案', priority: 'P0', type: '安全' },
    { id: 'QM-011', name: 'XSS-题目内容注入', steps: '1. 题目内容含<script>alert(1)</script>', expected: '内容被转义存储', priority: 'P1', type: '安全' },

    // Boundary
    { id: 'QM-012', name: '爬虫限制-pageSize>20', steps: '1. pageSize=50', expected: '返回400，提示参数错误', priority: 'P1', type: '边界' },
    { id: 'QM-013', name: '判题用例-空数组', steps: '1. judgeCase=[]\n2. 创建题目', expected: '创建成功，无测试用例', priority: 'P2', type: '边界' },

    // Performance
    { id: 'QM-014', name: '列表查询-响应时间', steps: '1. 查询题目列表', expected: 'p95 < 100ms', priority: 'P2', type: '性能' },
    { id: 'QM-015', name: '高级搜索-复杂条件', steps: '1. 多标签+难度+时间范围搜索', expected: '响应时间 < 500ms', priority: 'P2', type: '性能' },
];

const submitModuleTests = [
    // Happy Path
    { id: 'SM-001', name: '提交代码-正常流程', steps: '1. 登录\n2. 选择题目\n3. 选择语言\n4. 输入代码\n5. 提交', expected: '返回提交ID，状态WAITING', priority: 'P0', type: '功能' },
    { id: 'SM-002', name: '获取提交列表', steps: '1. 调用/question_submit/list/page', expected: '返回分页提交记录', priority: 'P0', type: '功能' },
    { id: 'SM-003', name: '普通用户查看他人提交', steps: '1. 普通用户查看列表', expected: '返回脱敏信息，不含代码', priority: 'P0', type: '安全' },
    { id: 'SM-004', name: '管理员查看提交', steps: '1. 管理员查看列表', expected: '返回完整信息，含代码', priority: 'P1', type: '功能' },

    // Error Path
    { id: 'SM-005', name: '提交-未登录', steps: '1. 无Session提交', expected: '返回401，未登录', priority: 'P0', type: '安全' },
    { id: 'SM-006', name: '提交-题目不存在', steps: '1. questionId=999999', expected: '返回404，题目不存在', priority: 'P0', type: '异常' },
    { id: 'SM-007', name: '提交-代码为空', steps: '1. code=""', expected: '返回400，代码不能为空', priority: 'P0', type: '异常' },
    { id: 'SM-008', name: '提交-语言不支持', steps: '1. language="ruby"', expected: '根据配置返回错误或拒绝', priority: 'P1', type: '异常' },

    // Security
    { id: 'SM-009', name: '代码注入-危险命令', steps: '1. 代码含Runtime.exec', expected: '沙箱拦截，返回危险操作', priority: 'P0', type: '安全' },
    { id: 'SM-010', name: '代码注入-文件操作', steps: '1. 代码含FileInputStream', expected: '沙箱拦截，安全异常', priority: 'P0', type: '安全' },

    // Boundary
    { id: 'SM-011', name: '提交-超长代码', steps: '1. 代码长度超过限制', expected: '返回400，代码过长', priority: 'P1', type: '边界' },

    // Performance
    { id: 'SM-012', name: '提交统计-查询性能', steps: '1. 调用/question_submit/today/stats', expected: '响应时间 < 100ms', priority: 'P2', type: '性能' },
];

const judgeModuleTests = [
    // Happy Path
    { id: 'JM-001', name: '判题-Accepted', steps: '1. 提交正确代码\n2. 通过所有用例', expected: '状态SUCCEED，message=Accepted', priority: 'P0', type: '功能' },
    { id: 'JM-002', name: '判题-Wrong Answer', steps: '1. 提交错误代码\n2. 输出不匹配', expected: '状态FAILED，message=Wrong Answer', priority: 'P0', type: '功能' },
    { id: 'JM-003', name: '判题-Compile Error', steps: '1. 提交语法错误代码', expected: '状态FAILED，message=Compile Error', priority: 'P0', type: '功能' },
    { id: 'JM-004', name: '判题-Time Limit', steps: '1. 提交超时代码', expected: '状态FAILED，message=Time Limit Exceeded', priority: 'P0', type: '功能' },
    { id: 'JM-005', name: '判题-Memory Limit', steps: '1. 提交内存超限代码', expected: '状态FAILED，message=Memory Limit Exceeded', priority: 'P0', type: '功能' },
    { id: 'JM-006', name: '判题-Runtime Error', steps: '1. 提交空指针/越界代码', expected: '状态FAILED，message=Runtime Error', priority: 'P0', type: '功能' },
    { id: 'JM-007', name: '状态转换-完整流程', steps: '1. 提交代码\n2. 观察状态变化', expected: 'WAITING(0)→RUNNING(1)→SUCCEED(2)/FAILED(3)', priority: 'P0', type: '功能' },

    // Error Path
    { id: 'JM-008', name: '判题-重复执行', steps: '1. 对同一提交重复判题', expected: '返回400，题目正在判题中', priority: 'P1', type: '异常' },
    { id: 'JM-009', name: '判题-题目不存在', steps: '1. 题目已被删除\n2. 执行判题', expected: '返回404，题目不存在', priority: 'P1', type: '异常' },

    // Business Logic
    { id: 'JM-010', name: '通过数更新', steps: '1. 提交正确答案\n2. 检查题目acceptedNum', expected: 'acceptedNum + 1', priority: 'P0', type: '功能' },
    { id: 'JM-011', name: 'Java策略-特殊处理', steps: '1. 提交Java代码', expected: '使用JavaLanguageJudgeStrategy', priority: 'P1', type: '功能' },
    { id: 'JM-012', name: '沙箱异常处理', steps: '1. 沙箱返回异常状态', expected: '正确记录错误，状态FAILED', priority: 'P0', type: '异常' },

    // Performance
    { id: 'JM-013', name: '判题-响应时间', steps: '1. 提交代码\n2. 测量判题耗时', expected: '单用例 < 5s，总耗时 < 30s', priority: 'P1', type: '性能' },
];

const sandboxModuleTests = [
    // Happy Path
    { id: 'SB-001', name: '执行-简单计算', steps: '1. 提交计算代码\n2. 传入输入', expected: '正确返回计算结果', priority: 'P0', type: '功能' },
    { id: 'SB-002', name: '执行-带参数', steps: '1. 读取命令行参数\n2. 传入多个输入', expected: '正确解析并返回', priority: 'P0', type: '功能' },
    { id: 'SB-003', name: '执行-多用例', steps: '1. 传入多个测试用例\n2. 逐一执行', expected: '返回每个用例结果列表', priority: 'P0', type: '功能' },

    // Error Path
    { id: 'SB-004', name: '编译-语法错误', steps: '1. 提交语法错误代码', expected: '返回编译错误信息', priority: 'P0', type: '异常' },
    { id: 'SB-005', name: '执行-超时控制', steps: '1. 提交死循环代码\n2. 等待执行', expected: '5000ms后终止，返回超时', priority: 'P0', type: '边界' },

    // Security - Critical
    { id: 'SB-006', name: '安全-读取文件', steps: '1. 代码含FileInputStream\n2. 尝试读取系统文件', expected: 'SecurityManager拦截，抛出异常', priority: 'P0', type: '安全' },
    { id: 'SB-007', name: '安全-写入文件', steps: '1. 代码含FileOutputStream', expected: 'SecurityManager拦截', priority: 'P0', type: '安全' },
    { id: 'SB-008', name: '安全-执行外部程序', steps: '1. 代码含Runtime.exec', expected: 'SecurityManager拦截', priority: 'P0', type: '安全' },
    { id: 'SB-009', name: '安全-内存攻击', steps: '1. 创建大量对象耗尽内存', expected: '受-Xmx256m限制，OOM', priority: 'P0', type: '安全' },
    { id: 'SB-010', name: '安全-睡眠攻击', steps: '1. Thread.sleep(Long.MAX_VALUE)', expected: '超时机制中断', priority: 'P1', type: '安全' },
    { id: 'SB-011', name: '安全-网络访问', steps: '1. 代码尝试网络连接', expected: 'SecurityManager拦截', priority: 'P0', type: '安全' },

    // Isolation
    { id: 'SB-012', name: '隔离-文件目录', steps: '1. 多次执行\n2. 检查目录名', expected: '每次使用不同UUID目录', priority: 'P1', type: '功能' },
    { id: 'SB-013', name: '清理-临时文件', steps: '1. 执行完成\n2. 检查临时目录', expected: '临时文件已删除', priority: 'P1', type: '功能' },

    // Resource Limit
    { id: 'SB-014', name: '资源-内存限制', steps: '1. 验证-Xmx256m参数', expected: '进程内存 < 256MB', priority: 'P0', type: '边界' },
    { id: 'SB-015', name: '响应-格式验证', steps: '1. 执行任意代码\n2. 检查响应结构', expected: '含outputList/status/message/judgeInfo', priority: 'P1', type: '功能' },

    // Docker (Optional)
    { id: 'SB-016', name: 'Docker-容器隔离', steps: '1. 使用Docker执行\n2. 验证隔离效果', expected: '代码在容器中执行', priority: 'P1', type: '功能' },
];

// ==================== 创建文档 ====================

const doc = new Document({
    styles: {
        default: { document: { run: { font: "SimSun", size: 22 } } },
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
                margin: { top: 1440, right: 1080, bottom: 1440, left: 1080 }
            }
        },
        headers: {
            default: new Header({
                children: [new Paragraph({
                    children: [new TextRun({ text: "在线判题系统测试用例设计文档", size: 18, color: "666666" })],
                    alignment: AlignmentType.CENTER
                })]
            })
        },
        footers: {
            default: new Footer({
                children: [new Paragraph({
                    children: [new TextRun({ text: "第 ", size: 18 }), new TextRun({ children: [PageNumber.CURRENT], size: 18 }), new TextRun({ text: " 页", size: 18 })],
                    alignment: AlignmentType.CENTER
                })]
            })
        },
        children: [
            // ==================== 封面 ====================
            new Paragraph({
                heading: HeadingLevel.HEADING_1,
                alignment: AlignmentType.CENTER,
                spacing: { before: 2400 },
                children: [new TextRun({ text: "在线判题系统", bold: true, size: 44 })]
            }),
            new Paragraph({
                alignment: AlignmentType.CENTER,
                children: [new TextRun({ text: "测试用例设计文档", bold: true, size: 44 })]
            }),
            new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { before: 600 },
                children: [new TextRun({ text: "Online Judge System Test Case Design", italics: true, size: 24, color: "666666" })]
            }),
            new Paragraph({ children: [] }),
            new Paragraph({ children: [] }),

            // 文档信息表
            createSimpleTable([
                ['文档版本', 'V1.0'],
                ['编制日期', '2026-03-26'],
                ['测试类型', '功能测试 | 安全测试 | 性能测试 | 边界测试'],
                ['测试框架', 'JUnit 5 + Mockito'],
                ['覆盖目标', '>85%']
            ], [2340, 7020]),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 1. 测试计划 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("1 测试计划")] }),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("1.1 测试范围")] }),
            new Paragraph({
                children: [new TextRun("本测试计划覆盖在线判题系统五大核心模块，采用分层测试策略：")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun({ text: "用户模块 (UserController): ", bold: true }), new TextRun("注册、登录、权限管理、用户信息管理")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun({ text: "题目模块 (QuestionController): ", bold: true }), new TextRun("题目CRUD、标签管理、高级搜索")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun({ text: "代码提交模块 (QuestionSubmitController): ", bold: true }), new TextRun("代码提交、记录查询、统计分析")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun({ text: "判题模块 (JudgeService): ", bold: true }), new TextRun("判题流程、策略模式、状态管理")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun({ text: "代码沙箱模块 (CodeSandbox): ", bold: true }), new TextRun("代码执行、安全隔离、资源控制")]
            }),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("1.2 测试类型分布")] }),
            createSimpleTable([
                ['测试类型', '说明', '用例数量', '占比'],
                ['功能测试', '验证业务功能正确性', '42', '38%'],
                ['异常测试', '验证错误处理能力', '18', '16%'],
                ['安全测试', '验证安全防护机制', '22', '20%'],
                ['边界测试', '验证边界条件处理', '12', '11%'],
                ['性能测试', '验证响应时间要求', '16', '15%'],
                ['合计', '-', '110', '100%']
            ], [2340, 4000, 1560, 1460]),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("1.3 风险评估")] }),
            createSimpleTable([
                ['风险', '概率', '影响', '优先级', '测试策略'],
                ['安全漏洞', '中', '高', 'P0', '全面安全测试，沙箱隔离验证'],
                ['数据泄露', '低', '高', 'P0', '权限测试，脱敏验证'],
                ['性能瓶颈', '中', '中', 'P1', '负载测试，响应时间监控'],
                ['判题错误', '中', '高', 'P0', '策略模式测试，状态验证'],
                ['代码注入', '高', '高', 'P0', '安全沙箱测试，恶意代码验证']
            ], [1800, 1200, 1200, 1200, 3960]),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 2. 用户模块测试 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("2 用户模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("2.1 模块概述")] }),
            new Paragraph({
                children: [new TextRun("用户模块负责用户认证与授权，采用Session会话管理，支持RBAC权限控制。核心类：UserController、UserService、User实体。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("2.2 测试用例列表")] }),
            createTestTable(userModuleTests),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 3. 题目模块测试 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("3 题目模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("3.1 模块概述")] }),
            new Paragraph({
                children: [new TextRun("题目模块管理题目的全生命周期，包含题目内容、判题用例、难度等级等。支持权限控制：创建者/管理员可编辑，普通用户仅可查看脱敏信息。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("3.2 测试用例列表")] }),
            createTestTable(questionModuleTests),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 4. 代码提交模块测试 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("4 代码提交模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("4.1 模块概述")] }),
            new Paragraph({
                children: [new TextRun("代码提交模块处理用户代码的提交与存储，记录执行结果、时间、内存消耗。普通用户只能查看脱敏信息，管理员可查看完整代码。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("4.2 测试用例列表")] }),
            createTestTable(submitModuleTests),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 5. 判题模块测试 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("5 判题模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("5.1 模块概述")] }),
            new Paragraph({
                children: [new TextRun("判题模块是核心业务逻辑，采用策略模式（JudgeStrategy）支持多语言判题。状态枚举：WAITING(0)、RUNNING(1)、SUCCEED(2)、FAILED(3)。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("5.2 判题结果枚举")] }),
            createSimpleTable([
                ['枚举值', '含义', '状态码'],
                ['Accepted', '答案正确', '2 (SUCCEED)'],
                ['Wrong Answer', '答案错误', '3 (FAILED)'],
                ['Compile Error', '编译错误', '3 (FAILED)'],
                ['Time Limit Exceeded', '时间超限', '3 (FAILED)'],
                ['Memory Limit Exceeded', '内存超限', '3 (FAILED)'],
                ['Runtime Error', '运行时错误', '3 (FAILED)']
            ], [3500, 3500, 2360]),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("5.3 测试用例列表")] }),
            createTestTable(judgeModuleTests),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 6. 代码沙箱模块测试 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("6 代码沙箱模块测试用例")] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("6.1 模块概述")] }),
            new Paragraph({
                children: [new TextRun("代码沙箱是系统安全核心，采用模板方法模式（JavaCodeSandboxTemplate）实现代码执行流程。安全机制：SecurityManager、文件隔离、资源限制。")]
            }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("6.2 安全机制说明")] }),
            createSimpleTable([
                ['安全机制', '实现方式', '防护目标'],
                ['文件系统隔离', 'UUID目录 + 自动清理', '防止文件残留、目录遍历'],
                ['SecurityManager', '自定义DenySecurityManager', '阻止文件IO、网络、进程创建'],
                ['内存限制', 'JVM参数 -Xmx256m', '防止内存耗尽攻击'],
                ['超时控制', 'Thread.sleep + destroy()', '防止死循环阻塞'],
                ['Docker隔离', '容器化执行(可选)', '更强隔离，防止逃逸']
            ], [2340, 3500, 3520]),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("6.3 测试用例列表")] }),
            createTestTable(sandboxModuleTests),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 7. 测试统计与质量门禁 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("7 测试统计与质量门禁")] }),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("7.1 用例统计")] }),
            createSimpleTable([
                ['模块', '用例数', 'P0', 'P1', 'P2', '功能', '异常', '安全', '边界', '性能'],
                ['用户模块', '18', '10', '5', '3', '5', '4', '4', '2', '3'],
                ['题目模块', '15', '7', '5', '3', '5', '3', '3', '2', '2'],
                ['代码提交', '12', '8', '2', '2', '4', '3', '3', '1', '1'],
                ['判题模块', '13', '9', '3', '1', '9', '2', '0', '0', '2'],
                ['代码沙箱', '16', '11', '5', '0', '5', '1', '6', '2', '2'],
                ['合计', '74', '45', '20', '9', '28', '13', '16', '7', '10']
            ], [1400, 900, 700, 700, 700, 900, 900, 900, 900, 900]),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("7.2 优先级定义")] }),
            createSimpleTable([
                ['优先级', '定义', '测试要求'],
                ['P0', '核心功能、安全相关', '必须100%通过，不可跳过'],
                ['P1', '重要功能、主要异常', '必须通过，可临时标记'],
                ['P2', '次要功能、性能优化', '建议通过，可延后修复']
            ], [1560, 3500, 4300]),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("7.3 质量门禁")] }),
            new Paragraph({
                children: [new TextRun({ text: "发布前必须满足以下条件：", bold: true })]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("P0用例100%通过（0失败）")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("代码覆盖率 > 85%")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("无Critical/High级别缺陷")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("安全测试全部通过")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("性能测试满足SLA（API响应p95 < 200ms）")]
            }),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("7.4 缺陷严重级别")] }),
            createSimpleTable([
                ['级别', '定义', '示例', '处理时限'],
                ['Critical', '安全漏洞、数据丢失、系统崩溃', 'SQL注入、代码沙箱逃逸', '立即修复'],
                ['High', '核心功能失效、严重性能问题', '登录失败、判题错误', '24小时内'],
                ['Medium', '功能部分失效，有替代方案', '分页异常、搜索不准', '迭代内修复'],
                ['Low', 'UI问题、边缘场景', '提示文案错误', '低优先级排期']
            ], [1560, 2500, 2700, 2600]),

            new Paragraph({ children: [new PageBreak()] }),

            // ==================== 8. 附录 ====================
            new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun("8 附录")] }),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("8.1 测试环境")] }),
            createSimpleTable([
                ['项目', '配置'],
                ['操作系统', 'Windows 10 / Linux (CentOS 7+)'],
                ['JDK', 'JDK 8 / JDK 11'],
                ['数据库', 'MySQL 8.0'],
                ['后端框架', 'Spring Boot 2.7 + MyBatis-Plus'],
                ['测试框架', 'JUnit 5 + Mockito'],
                ['代码沙箱', 'Java原生 / Docker容器']
            ], [2340, 7020]),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("8.2 测试数据准备")] }),
            new Paragraph({
                children: [new TextRun("测试前需准备以下数据：")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("测试用户：admin(管理员)、user1(普通用户)、user2(普通用户)")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("测试题目：至少3道不同难度的题目")]
            }),
            new Paragraph({
                numbering: { reference: "bullets", level: 0 },
                children: [new TextRun("测试代码：正确代码、错误代码、恶意代码样本")]
            }),

            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("8.3 自动化测试示例")] }),
            new Paragraph({
                children: [new TextRun({ text: "JUnit 5 单元测试示例：", bold: true })]
            }),
            new Paragraph({
                shading: { fill: "F5F5F5", type: ShadingType.CLEAR },
                children: [new TextRun({ text: "@Test\n@DisplayName(\"UM-001: 用户注册正常流程\")\nvoid testUserRegister_success() {\n    UserRegisterRequest request = new UserRegisterRequest();\n    request.setUserAccount(\"testuser001\");\n    request.setUserPassword(\"Test@123456\");\n    request.setCheckPassword(\"Test@123456\");\n\n    Long userId = userService.userRegister(\n        request.getUserAccount(),\n        request.getUserPassword(),\n        request.getCheckPassword()\n    );\n\n    assertNotNull(userId);\n    assertTrue(userId > 0);\n}", font: "Consolas", size: 18 })]
            }),

            new Paragraph({ children: [] }),
            new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun("8.4 变更记录")] }),
            createSimpleTable([
                ['版本', '日期', '作者', '变更内容'],
                ['V1.0', '2026-03-26', 'TestMaster', '初始版本，完成五大模块测试用例设计']
            ], [1560, 2000, 2000, 3800]),
        ]
    }]
});

Packer.toBuffer(doc).then(buffer => {
    fs.writeFileSync("E:/Project/JAVA/ideaProjects/oj/新测试用例.docx", buffer);
    console.log("文档已生成: E:/Project/JAVA/ideaProjects/oj/新测试用例.docx");
});