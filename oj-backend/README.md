# SpringBoot 项目初始模板

> 作者：[程序员亚克](http://www.isyyk.top)

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

只需 1 分钟即可完成内容网站的后端！！！大家还可以在此基础上快速开发自己的项目。

## 📋 目录

- [项目简介](#项目简介)
- [环境要求](#环境要求)
- [模板特点](#模板特点)
- [业务功能](#业务功能)
- [项目结构](#项目结构)
- [快速上手](#快速上手)
- [API 文档](#api-文档)
- [部署指南](#部署指南)
- [常见问题](#常见问题)
- [性能优化](#性能优化)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

## 🚀 项目简介

这是一个基于 Spring Boot 2.7.2 的在线判题系统（OJ - Online Judge）后端项目，集成了主流的企业级开发框架和工具，提供了完整的用户管理、题目管理、代码判题、文件上传等功能。

### ✨ 主要特性

- 🎯 **开箱即用**：整合了常用的开发框架和工具
- 🔐 **安全可靠**：完善的权限管理和安全机制
- 📊 **高性能**：Redis缓存、ES搜索引擎优化
- 🧪 **测试完备**：完整的单元测试覆盖
- 📚 **文档齐全**：详细的API文档和使用说明

## 🔧 环境要求

### 基础环境

- **JDK**: 1.8 或更高版本
- **Maven**: 3.6.0 或更高版本
- **MySQL**: 5.7 或更高版本
- **Redis**: 5.0 或更高版本
- **Elasticsearch**: 7.x 版本

### 推荐配置

- **内存**: 最少 2GB，推荐 4GB 或更高
- **磁盘**: 最少 10GB 可用空间
- **操作系统**: Windows 10+ / macOS 10.14+ / Ubuntu 18.04+

## 🏗️ 模板特点

### 主流框架 & 特性

- **Spring Boot 2.7.2** - 快速构建应用
- **Spring MVC** - Web层框架
- **MyBatis + MyBatis Plus** - 数据访问层（开启分页）
- **Spring Boot 调试工具和项目处理器** - 开发调试
- **Spring AOP 切面编程** - 横切关注点处理
- **Spring Scheduler 定时任务** - 定时任务调度
- **Spring 事务注解** - 事务管理

### 数据存储

- **MySQL 数据库** - 主数据存储
- **Redis 内存数据库** - 缓存和会话存储
- **Elasticsearch 搜索引擎** - 全文搜索
- **腾讯云 COS 对象存储** - 文件存储

### 工具类

- **Easy Excel** - 表格处理
- **Hutool** - 工具库
- **Gson** - JSON解析库
- **Apache Commons Lang3** - 工具类
- **Lombok** - 注解简化代码

### 业务特性

- **Spring Session Redis** - 分布式登录
- **全局请求响应拦截器** - 记录日志
- **全局异常处理器** - 统一异常处理
- **自定义错误码** - 错误码管理
- **封装通用响应类** - 统一响应格式
- **Swagger + Knife4j** - 接口文档
- **自定义权限注解 + 全局校验** - 权限控制
- **全局跨域处理** - CORS配置
- **长整数丢失精度解决** - 精度问题处理
- **多环境配置** - 环境隔离

## 📦 业务功能

### 用户管理
- ✅ 用户注册、登录、注销
- ✅ 用户信息更新、检索
- ✅ 权限管理和角色控制
- ✅ 微信开放平台登录集成

### 内容管理
- ✅ 帖子创建、删除、编辑、更新
- ✅ 数据库检索、ES灵活检索
- ✅ 帖子点赞、取消点赞
- ✅ 帖子收藏、取消收藏、检索已收藏帖子

### 文件管理
- ✅ 分业务文件上传
- ✅ 腾讯云COS对象存储集成
- ✅ 文件类型验证和安全检查

### 定时任务
- ✅ 帖子全量同步ES
- ✅ 帖子增量同步ES

### 微信集成
- ✅ 微信公众号订阅
- ✅ 收发消息处理
- ✅ 菜单设置

### 单元测试

- **JUnit5** - 单元测试框架
- **示例单元测试类** - 完整的测试示例

### 架构设计

- **合理分层** - Controller/Service/Mapper分层
- **设计模式** - 策略模式、工厂模式等
- **代码规范** - 统一的编码规范

## 📁 项目结构

```
yykoj-backend-master/
├── doc/                    # 文档目录
│   └── swagger.png        # API文档截图
├── sql/                   # 数据库脚本
│   ├── create_table.sql   # 建表语句
│   └── post_es_mapping.json # ES映射配置
├── src/
│   ├── main/
│   │   ├── java/com/yyk/oj/
│   │   │   ├── annotation/     # 自定义注解
│   │   │   ├── aop/           # 切面编程
│   │   │   ├── common/        # 通用类
│   │   │   ├── config/        # 配置类
│   │   │   ├── constant/      # 常量定义
│   │   │   ├── controller/    # 控制器层
│   │   │   ├── esdao/         # ES数据访问
│   │   │   ├── exception/     # 异常处理
│   │   │   ├── job/           # 定时任务
│   │   │   ├── judge/         # 代码判题
│   │   │   ├── manager/       # 管理器
│   │   │   ├── mapper/        # 数据访问层
│   │   │   ├── model/         # 数据模型
│   │   │   ├── service/       # 业务逻辑层
│   │   │   ├── utils/         # 工具类
│   │   │   └── wxmp/          # 微信相关
│   │   └── resources/         # 资源文件
│   └── test/                 # 测试代码
├── Dockerfile              # Docker配置
├── pom.xml                 # Maven配置
└── README.md              # 项目说明
```

## 🚀 快速上手

> 所有需要修改的地方亚克都标记了 `todo`，便于大家找到修改的位置~

### 1. 克隆项目

```bash
git clone https://github.com/your-username/yykoj-backend-master.git
cd yykoj-backend-master
```

### 2. 环境准备

确保已安装并配置好以下环境：
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- Elasticsearch 7.x

### 3. MySQL 数据库配置

1）修改 `application.yml` 的数据库配置为你自己的：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456
```

2）执行 `sql/create_table.sql` 中的数据库语句，自动创建库表

3）启动项目，访问 `http://localhost:8101/api/doc.html` 即可打开接口文档，不需要写前端就能在线调试接口了~

![API文档](doc/swagger.png)

### 4. Redis 分布式登录配置

1）修改 `application.yml` 的 Redis 配置为你自己的：

```yml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456
    lettuce:
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 8
        min-idle: 0
```

2）修改 `application.yml` 中的 session 存储方式：

```yml
spring:
  session:
    store-type: redis
    timeout: 86400
```

3）移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：

```java
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

修改后：

```java
@SpringBootApplication
```

### 5. Elasticsearch 搜索引擎配置

1）修改 `application.yml` 的 Elasticsearch 配置为你自己的：

```yml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
    connection-timeout: 5000
    read-timeout: 30000
```

2）复制 `sql/post_es_mapping.json` 文件中的内容，通过调用 Elasticsearch 的接口或者 Kibana Dev Tools 来创建索引（相当于数据库建表）

```bash
# 使用 curl 创建索引
curl -X PUT "localhost:9200/post_v1" -H 'Content-Type: application/json' -d @sql/post_es_mapping.json
```

或者使用 Kibana Dev Tools：

```
PUT post_v1
{
 参数见 sql/post_es_mapping.json 文件
}
```



### 6. 腾讯云 COS 配置（可选）

如果需要使用文件上传功能，请配置腾讯云 COS：

```yml
cos:
  client:
    accessKey: your-access-key
    secretKey: your-secret-key
    region: ap-beijing
    bucket: your-bucket-name
```

### 7. 微信开放平台配置（可选）

如果需要微信登录功能，请配置微信开放平台：

```yml
wx:
  open:
    appId: your-app-id
    appSecret: your-app-secret
```

### 8. 启动项目

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动项目
mvn spring-boot:run
```

或者直接运行 `MainApplication.java` 的 main 方法。

## 📚 API 文档

### 接口文档地址

- **开发环境**: http://localhost:8101/api/doc.html
- **测试环境**: http://your-test-domain/api/doc.html
- **生产环境**: http://your-prod-domain/api/doc.html

### 主要接口

#### 用户相关接口
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/get/current` - 获取当前用户信息
- `POST /api/user/logout` - 用户退出
- `POST /api/user/update` - 更新用户信息

#### 帖子相关接口
- `POST /api/post/add` - 创建帖子
- `GET /api/post/get` - 获取帖子详情
- `GET /api/post/list/page` - 分页获取帖子列表
- `POST /api/post/update` - 更新帖子
- `DELETE /api/post/delete` - 删除帖子

#### 文件上传接口
- `POST /api/file/upload` - 文件上传

### 接口认证

大部分接口需要登录认证，请在请求头中添加：

```
Authorization: Bearer your-session-id
```

## 🚀 部署指南

### 开发环境部署

1. 确保所有依赖服务（MySQL、Redis、ES）已启动
2. 修改 `application.yml` 中的配置
3. 运行 `mvn spring-boot:run` 启动项目

### 生产环境部署

#### 方式一：JAR 包部署

```bash
# 打包
mvn clean package -Dmaven.test.skip=true

# 运行
java -jar target/yykoj-backend-1.0.0.jar --spring.profiles.active=prod
```

#### 方式二：Docker 部署

```bash
# 构建镜像
docker build -t yykoj-backend .

# 运行容器
docker run -d -p 8101:8101 --name yykoj-backend yykoj-backend
```

#### 方式三：Docker Compose 部署

创建 `docker-compose.yml` 文件：

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8101:8101"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
      - redis
      - elasticsearch
  
  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: yyk_oj
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
  
  redis:
    image: redis:5.0
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
  
  elasticsearch:
    image: elasticsearch:7.17.0
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - es_data:/usr/share/elasticsearch/data

volumes:
  mysql_data:
  redis_data:
  es_data:
```

运行：

```bash
docker-compose up -d
```

## ❓ 常见问题

### Q1: 启动时提示数据库连接失败

**A**: 请检查以下几点：
1. MySQL 服务是否启动
2. 数据库连接配置是否正确
3. 数据库用户权限是否足够
4. 防火墙是否阻止了连接

### Q2: Redis 连接失败

**A**: 请检查以下几点：
1. Redis 服务是否启动
2. Redis 配置是否正确
3. Redis 密码是否正确
4. 网络连接是否正常

### Q3: Elasticsearch 连接失败

**A**: 请检查以下几点：
1. Elasticsearch 服务是否启动
2. ES 配置是否正确
3. 内存是否足够（ES 需要至少 1GB 内存）
4. 端口是否被占用

### Q4: 文件上传失败

**A**: 请检查以下几点：
1. 腾讯云 COS 配置是否正确
2. 网络连接是否正常
3. 文件大小是否超限
4. 文件类型是否支持

### Q5: 微信登录失败

**A**: 请检查以下几点：
1. 微信开放平台配置是否正确
2. 应用是否已通过审核
3. 回调地址是否正确
4. 网络连接是否正常

## ⚡ 性能优化

### 数据库优化

1. **索引优化**
   - 为常用查询字段添加索引
   - 避免在索引字段上使用函数
   - 定期分析慢查询日志

2. **连接池配置**
   ```yml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 20
         minimum-idle: 5
         connection-timeout: 30000
   ```

### Redis 优化

1. **内存配置**
   ```yml
   spring:
     redis:
       lettuce:
         pool:
           max-active: 20
           max-idle: 10
           min-idle: 5
   ```

2. **缓存策略**
   - 合理设置缓存过期时间
   - 使用缓存预热
   - 避免缓存雪崩

### Elasticsearch 优化

1. **索引优化**
   - 合理设置分片数
   - 定期清理无用索引
   - 使用批量操作

2. **查询优化**
   - 避免深度分页
   - 使用合适的查询类型
   - 合理使用聚合查询

### JVM 优化

```bash
java -Xms2g -Xmx4g -XX:+UseG1GC -jar your-app.jar
```

## 🤝 贡献指南

我们欢迎所有形式的贡献，包括但不限于：

### 如何贡献

1. **Fork 项目**
   ```bash
   git clone https://github.com/your-username/yykoj-backend-master.git
   ```

2. **创建特性分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **提交更改**
   ```bash
   git commit -m "feat: add your feature"
   ```

4. **推送到分支**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **创建 Pull Request**

### 代码规范

- 遵循 Java 编码规范
- 使用有意义的变量和方法名
- 添加必要的注释
- 编写单元测试
- 确保代码通过所有测试

### 提交规范

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。


## 📞 联系我们

- **作者**: 程序员亚克
- **邮箱**: yyk020906@163.com
- **网站**: [http://www.isyyk.top](http://www.isyyk.top)
- **GitHub**: [https://github.com/your-username](https://github.com/your-username)

---

如果这个项目对你有帮助，请给我们一个 ⭐️ Star！