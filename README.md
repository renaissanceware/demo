# 在Render上Docker部署Spring Boot应用

本指南将帮助您在Render平台上使用Docker部署Spring Boot应用。

## 项目结构

```
./
├── Dockerfile          # Docker构建文件
├── pom.xml            # Maven依赖配置
├── render.yaml        # Render部署配置
└── src/
    └── main/
        └── java/com/example/demo/
            └── DemoApplication.java  # 应用主入口
```

## 已创建的文件说明

### 1. Dockerfile

使用多阶段构建优化Docker镜像大小：

- **构建阶段**：使用Maven和Java 17构建项目
- **运行阶段**：使用轻量级的Java 17 JRE运行应用

### 2. render.yaml

Render平台的部署配置文件：

- 定义了Web服务的部署方式
- 配置了Docker上下文和Dockerfile路径
- 包含环境变量设置（如Spring配置、数据库连接）
- 可选的数据库配置

## 部署步骤

### 1. 准备GitHub仓库

确保您的项目已推送到GitHub仓库：

```bash
git add .
git commit -m "准备部署到Render"
git push origin main
```

### 2. 在Render上创建账户

1. 访问 [Render官网](https://render.com/) 并创建账户
2. 使用GitHub账户登录以方便仓库集成

### 3. 部署应用

#### 方法一：通过render.yaml部署（推荐）

1. 在Render控制台中，点击"New +"按钮
2. 选择"Blueprint"
3. 点击"Connect Repository"并选择您的GitHub仓库
4. 选择仓库后，Render会自动识别render.yaml文件
5. 点击"Apply"开始部署

#### 方法二：手动创建Docker服务

1. 在Render控制台中，点击"New +"按钮
2. 选择"Web Service"
3. 点击"Connect Repository"并选择您的GitHub仓库
4. 在"Environment"部分选择"Docker"
5. 配置以下选项：
   - **Name**：应用名称
   - **Region**：选择合适的区域（如Oregon）
   - **Branch**：选择要部署的分支（如main）
   - **Dockerfile Path**：`./Dockerfile`
   - **Docker Context**：`.`
6. 点击"Advanced"配置环境变量
7. 点击"Create Web Service"开始部署

### 4. 配置环境变量

根据您的应用需求，配置必要的环境变量：

| 环境变量 | 描述 | 示例值 |
|---------|------|-------|
| SPRING_PROFILES_ACTIVE | Spring配置文件 | prod |
| SPRING_DATASOURCE_URL | 数据库连接URL | jdbc:mysql://localhost:3306/demo |
| SPRING_DATASOURCE_USERNAME | 数据库用户名 | root |
| SPRING_DATASOURCE_PASSWORD | 数据库密码 | password |
| SPRING_JPA_HIBERNATE_DDL_AUTO | Hibernate DDL策略 | update |

### 5. 数据库配置（可选）

如果您需要在Render上部署数据库：

1. 在Render控制台中，点击"New +"按钮
2. 选择"Database"
3. 选择数据库类型（如PostgreSQL或MySQL）
4. 配置数据库名称、用户名和密码
5. 点击"Create Database"
6. 创建后，复制数据库连接信息到应用的环境变量中

## 验证部署

部署完成后：

1. 访问Render提供的应用URL（如`https://your-app-name.onrender.com`）
2. 检查应用是否正常运行
3. 如果配置了Actuator，可以访问`/actuator/health`检查应用健康状态

## 常见问题

### 1. 部署失败

- 检查Dockerfile是否正确
- 查看部署日志以获取详细错误信息
- 确保环境变量配置正确

### 2. 数据库连接问题

- 确保数据库已正确创建
- 检查数据库连接URL、用户名和密码
- 确保数据库服务与应用服务在同一区域

### 3. 应用端口问题

- Spring Boot默认使用8080端口，确保Render配置中暴露了正确的端口
- 可以通过环境变量`SERVER_PORT`自定义端口

## 维护和更新

- 推送代码更改到GitHub仓库后，Render会自动重新部署（如果启用了autoDeploy）
- 可以在Render控制台中手动触发部署
- 监控应用日志以诊断问题

## 参考链接

- [Render Docker部署文档](https://render.com/docs/docker)
- [Spring Boot Docker文档](https://spring.io/guides/gs/spring-boot-docker/)
- [Render Blueprint文档](https://render.com/docs/blueprints)