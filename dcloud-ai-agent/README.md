# DCloud AI Agent

**AI 智能体中心 API 服务**

基于 FastAPI 和 LangChain 构建的智能体服务平台，为智能云盘系统提供多种 AI 能力支持，包括智能对话、文档处理和网盘文件查询等功能。

## 项目概述

本项目是一个企业级 AI 智能体服务平台，采用模块化架构设计，集成了阿里云通义千问大模型（Qwen），提供以下核心能力：

- **智能聊天助手**：支持流式对话、对话历史管理、智能摘要生成
- **文档处理助手**：支持 HTML/PDF 文档解析、内容提取与智能总结
- **网盘查询助手**：基于自然语言的数据库查询，支持文件搜索、统计分析

## 技术栈

| 类别 | 技术 |
|------|------|
| Web 框架 | FastAPI |
| AI 框架 | LangChain |
| 大语言模型 | 阿里云通义千问 (Qwen-Plus) |
| 数据库 | MySQL (网盘数据)、Redis (缓存/会话) |
| 向量数据库 | Milvus (可选) |
| 容器化 | Docker |
| Python 版本 | 3.12.6 |

## 项目结构

```
dcloud-ai-agent/
├── app/                    # 应用入口
│   └── main.py             # FastAPI 应用主文件
├── agents/                 # 智能体模块
│   ├── chat_agent.py       # 聊天智能体
│   ├── doc_agent.py        # 文档智能体
│   └── pan_agent.py        # 网盘查询智能体
├── core/                   # 核心模块
│   ├── auth.py             # JWT 认证
│   ├── config.py           # 配置管理
│   ├── exceptions.py       # 异常处理
│   └── llm.py              # LLM 初始化
├── models/                 # 数据模型
│   ├── chat_schemas.py     # 聊天请求/响应模型
│   ├── doc_schemas.py      # 文档请求/响应模型
│   ├── pan_schemas.py      # 网盘请求/响应模型
│   └── json_response.py    # 统一响应格式
├── routers/                # API 路由
│   ├── chat.py             # 聊天 API
│   ├── doc.py              # 文档 API
│   └── pan.py              # 网盘 API
├── services/               # 业务服务层
│   ├── chat_service.py     # 聊天服务
│   └── doc_service.py      # 文档服务
├── tools/                  # LangChain 工具
│   ├── chat_tools.py       # 聊天工具（搜索等）
│   └── document_tools.py   # 文档处理工具
├── tests/                  # 测试文件
├── Dockerfile              # Docker 构建文件
└── requirements.txt        # Python 依赖
```

## 快速开始

### 环境要求

- Python 3.12+
- MySQL 5.7+
- Redis 6.0+
- Homebrew (macOS)

### Python 3.12 安装 (macOS)

如果你的系统 Python 版本低于 3.12，需要先安装 Python 3.12：

```bash
# 使用 Homebrew 安装 Python 3.12
brew install python@3.12

# 配置 python 命令指向 Python 3.12（添加到 ~/.zshrc）
echo 'export PATH="/opt/homebrew/opt/python@3.12/libexec/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 验证版本
python --version  # 应显示 Python 3.12.x
```

### 安装步骤

1. **克隆项目**

```bash
git clone <repository-url>
cd dcloud-ai-agent
```

2. **创建虚拟环境**

```bash
# 创建虚拟环境
python -m venv venv

# 激活虚拟环境
source venv/bin/activate  # Linux/macOS
# 或
venv\Scripts\activate     # Windows
```

3. **安装依赖**

```bash
# 先升级 pip（可选）
pip install --upgrade pip

# 安装项目依赖
pip install -r requirements.txt
```

4. **配置环境变量**（可选）

如需覆盖默认配置，创建 `.env` 文件：

```env
# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_DB=0
REDIS_PASSWORD=your_password

# MySQL 配置
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_DATABASE=dcloud_aipan

# LLM 配置
LLM_MODEL_NAME=qwen-plus
LLM_API_KEY=your_api_key
LLM_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1

# JWT 配置
JWT_SECRET_KEY=your_secret_key
```

5. **启动服务**

```bash
# 确保已激活虚拟环境
source venv/bin/activate

# 开发模式（推荐，支持热重载）
python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

# 或直接运行
python -m app.main
```

启动成功后，访问 http://localhost:8000 可以看到欢迎页面。

### Docker 部署

```bash
# 构建镜像
docker build -t dcloud-ai-agent .

# 运行容器
docker run -d -p 8000:8000 --name dcloud-ai-agent dcloud-ai-agent
```

## 架构说明

### 与 Spring Boot 后端的配合关系

本项目是智能云盘系统的 **AI 智能体服务**，与 Spring Boot 主后端 (`dcloud-aipan`) 是**微服务架构**的协作关系：

```
┌────────────────────────────────────────────────────────────────────────────┐
│                              前端 (Vue/React)                               │
└──────────────────────────────────┬─────────────────────────────────────────┘
                                   │
         ┌─────────────────────────┴─────────────────────────┐
         │                                                   │
         ▼                                                   ▼
┌─────────────────────────────┐               ┌─────────────────────────────┐
│    Spring Boot 后端          │               │    Python AI Agent          │
│    (dcloud-aipan)           │               │    (本项目)                  │
│    端口: 9090               │               │    端口: 8000               │
│                             │               │                             │
│  ○ 用户模块                  │               │  ○ 智能聊天助手              │
│    /api/account/v1/*        │               │    /api/chat/*              │
│                             │               │                             │
│  ○ 文件模块                  │               │  ○ 文档处理助手              │
│    /api/file/v1/*           │               │    /api/document/*          │
│                             │               │                             │
│  ○ 分享模块                  │               │  ○ 网盘查询助手              │
│    /api/share/v1/*          │               │    /api/pan/*               │
│                             │               │                             │
│  ○ 回收站模块                │               │                             │
│    /api/recycle/v1/*        │               │                             │
└──────────┬──────────────────┘               └──────────────┬──────────────┘
           │                                                 │
           │     ┌─────────────────────────────────────┐     │
           │     │         共享资源                      │     │
           └────►│                                     │◄────┘
                 │  ┌─────────┐    ┌─────────────────┐ │
                 │  │  MySQL  │    │  Redis 缓存      │ │
                 │  │ 云盘数据  │    │  会话/Token     │ │
                 │  └─────────┘    └─────────────────┘ │
                 │                                     │
                 │  ┌─────────────────────────────────┐│
                 │  │     MinIO (S3 兼容对象存储)       ││
                 │  │     文件/图片/视频存储            ││
                 │  └─────────────────────────────────┘│
                 └─────────────────────────────────────┘
                                   │
                                   ▼ (AI Agent 独有)
                 ┌─────────────────────────────────────┐
                 │         阿里云通义千问               │
                 │         (Qwen-Plus 大语言模型)       │
                 └─────────────────────────────────────┘
```

### 服务分工详解

| 服务 | 技术栈 | 端口 | 核心职责 |
|------|--------|------|----------|
| **Spring Boot 后端** | Java 21 + Spring Boot 3.2 + MyBatis-Plus | 9090 | 用户认证、文件CRUD、分片上传、分享管理、回收站管理 |
| **Python AI Agent** | Python 3.12 + FastAPI + LangChain | 8000 | 智能对话、文档智能总结、自然语言数据库查询、LLM 调用 |

### API 端点对照表

#### Spring Boot 后端 (`http://localhost:9090`)

| 模块 | 端点 | 方法 | 说明 |
|------|------|------|------|
| **用户管理** | `/api/account/v1/register` | POST | 用户注册 |
|  | `/api/account/v1/login` | POST | 用户登录，返回 JWT Token |
|  | `/api/account/v1/detail` | GET | 获取当前用户详情 |
|  | `/api/account/v1/upload_avatar` | POST | 上传用户头像 |
| **文件管理** | `/api/file/v1/list` | GET | 查询文件列表 |
|  | `/api/file/v1/create_folder` | POST | 创建文件夹 |
|  | `/api/file/v1/rename_file` | POST | 文件重命名 |
|  | `/api/file/v1/folder/tree` | GET | 获取文件树 |
|  | `/api/file/v1/upload` | POST | 普通小文件上传 |
|  | `/api/file/v1/second_upload` | POST | 文件秒传 |
|  | `/api/file/v1/init_file_chunk_task` | POST | 创建分片上传任务 |
|  | `/api/file/v1/merge_file_chunk` | POST | 合并分片 |
|  | `/api/file/v1/move_batch` | POST | 批量移动文件 |
|  | `/api/file/v1/copy_batch` | POST | 批量复制文件 |
|  | `/api/file/v1/del_batch` | POST | 批量删除文件 |
| **分享管理** | `/api/share/v1/create` | POST | 创建分享链接 |
|  | `/api/share/v1/list` | GET | 分享列表 |
|  | `/api/share/v1/cancel` | POST | 取消分享 |
| **回收站** | `/api/recycle/v1/list` | GET | 回收站列表 |
|  | `/api/recycle/v1/delete` | POST | 彻底删除 |
|  | `/api/recycle/v1/restore` | POST | 文件还原 |

#### Python AI Agent (`http://localhost:8000`)

| 模块 | 端点 | 方法 | 说明 |
|------|------|------|------|
| **智能聊天** | `/api/chat/stream` | POST | 流式对话接口 (SSE) |
| **文档处理** | `/api/document/stream` | POST | 流式文档处理 (SSE) |
| **网盘查询** | `/api/pan/query` | POST | 自然语言查询网盘数据 |

### 共享数据库表结构

Python AI Agent 的网盘查询功能直接访问 Spring Boot 后端使用的 MySQL 数据库。以下是相关的核心表结构：

| 表名 | 说明 | AI Agent 使用场景 |
|------|------|-------------------|
| `account` | 用户信息表 | 用户身份验证 |
| `account_file` | 用户文件关联表 | 智能查询用户文件列表、搜索文件、统计文件类型 |
| `file` | 文件存储表 | 获取文件元信息 |
| `file_chunk` | 文件分片信息表 | - |
| `file_type` | 文件类型表 | 文件类型分类统计 |
| `file_suffix` | 文件扩展名表 | - |
| `share` | 分享信息表 | - |
| `share_file` | 分享文件关联表 | - |
| `storage` | 用户存储空间表 | 查询用户存储空间使用情况 |

#### account_file 表关键字段

```sql
-- 用户文件关联表 (AI Agent 网盘查询的核心数据源)
CREATE TABLE `account_file` (
  `id` bigint NOT NULL COMMENT 'id',
  `account_id` bigint DEFAULT NULL COMMENT '用户ID',
  `is_dir` int NOT NULL COMMENT '状态 0不是文件夹，1是文件夹',
  `parent_id` bigint DEFAULT NULL COMMENT '上层文件夹ID,顶层文件夹为0',
  `file_id` bigint DEFAULT NULL COMMENT '文件ID，真正存储的文件',
  `file_name` varchar(256) DEFAULT NULL COMMENT '文件名称',
  `file_type` varchar(32) DEFAULT NULL COMMENT '文件类型：common/compress/excel/word/pdf/txt/img/audio/video/ppt/code/csv',
  `file_suffix` varchar(255) DEFAULT NULL COMMENT '文件的后缀拓展名',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小，字节',
  `del` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除（0未删除，1已删除）',
  `del_time` datetime DEFAULT NULL COMMENT '删除日期',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) COMMENT='用户文件关联表';
```

#### storage 表关键字段

```sql
-- 存储信息表 (AI Agent 查询用户存储空间)
CREATE TABLE `storage` (
  `id` bigint NOT NULL,
  `account_id` bigint DEFAULT NULL COMMENT '所属用户',
  `used_size` bigint DEFAULT NULL COMMENT '占用存储大小（字节）',
  `total_size` bigint DEFAULT NULL COMMENT '总容量大小（字节）',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userid_index` (`account_id`)
) COMMENT='存储信息表';
```

### JWT 统一认证机制

两个服务使用相同的 JWT 密钥和验证逻辑，实现用户在 Spring Boot 登录后，Token 可以在 Python AI Agent 中无缝验证。

#### JWT Token 格式

| 配置项 | Spring Boot | Python AI Agent | 说明 |
|--------|-------------|-----------------|------|
| 密钥 | `SECRET_KEY` | `JWT_SECRET_KEY` | 必须完全一致 |
| 算法 | `HS256` | `JWT_ALGORITHM` | 必须为 `HS256` |
| 主题前缀 | `XDCLASS` | `JWT_LOGIN_SUBJECT` | Token 前缀标识 |
| 过期时间 | 7 天 | 可配置 | Token 有效期 |

#### Token 结构

```
XDCLASS{JWT_PAYLOAD}
```

Token 内容包含：
- `sub`: 主题 (`XDCLASS`)
- `accountId`: 用户 ID
- `username`: 用户名
- `iat`: 签发时间
- `exp`: 过期时间

#### 认证流程

```
┌──────────┐     1. POST /api/account/v1/login     ┌─────────────────┐
│   前端   │ ───────────────────────────────────►  │  Spring Boot   │
│          │                                       │                 │
│          │  ◄─────────────────────────────────── │  生成 JWT Token │
└──────────┘     2. 返回 Token                     └─────────────────┘
     │
     │ 3. 携带 Token 请求 AI 功能
     │    Header: token: XDCLASS...
     ▼
┌───────────────────────┐
│   Python AI Agent     │
│                       │
│  ○ 移除 XDCLASS 前缀   │
│  ○ 使用相同密钥验证    │
│  ○ 提取 accountId     │
│  ○ 执行 AI 查询        │
└───────────────────────┘
```

### 配置同步说明

为确保两个服务正常协作，以下配置必须保持一致：

#### 1. 数据库配置

**Spring Boot** (`application-dev.yml`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dcloud_aipan
    username: root
    password: your_password
```

**Python AI Agent** (`core/config.py` 或 `.env`):
```python
MYSQL_HOST = "127.0.0.1"
MYSQL_PORT = 3306
MYSQL_DATABASE = "dcloud_aipan"
MYSQL_USER = "root"
MYSQL_PASSWORD = "your_password"
```

#### 2. JWT 配置

**Spring Boot** (`JwtUtil.java`):
```java
private final static String SECRET_KEY = "xdclass.net168xdclass.net168xdclass.net168xdclass.net168";
private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
private static final String LOGIN_SUBJECT = "XDCLASS";
```

**Python AI Agent** (`core/config.py` 或 `.env`):
```python
JWT_SECRET_KEY = "xdclass.net168xdclass.net168xdclass.net168xdclass.net168"
JWT_ALGORITHM = "HS256"
JWT_LOGIN_SUBJECT = "XDCLASS"
```

#### 3. Redis 配置 (可选)

**Spring Boot** (`application-dev.yml`):
```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: your_redis_password
```

**Python AI Agent** (`core/config.py` 或 `.env`):
```python
REDIS_HOST = "127.0.0.1"
REDIS_PORT = 6379
REDIS_PASSWORD = "your_redis_password"
```

### 典型交互场景

#### 场景 1: 用户登录后使用 AI 查询网盘

```sequence
用户 -> 前端: 输入用户名密码
前端 -> Spring Boot: POST /api/account/v1/login
Spring Boot -> 前端: 返回 JWT Token
前端 -> Python AI Agent: POST /api/pan/query (携带 Token)
Python AI Agent -> MySQL: 查询 account_file 表
MySQL -> Python AI Agent: 返回文件列表
Python AI Agent -> LLM: 格式化查询结果
LLM -> Python AI Agent: 返回自然语言描述
Python AI Agent -> 前端: 返回 AI 处理后的结果
```

#### 场景 2: AI 智能聊天

```sequence
前端 -> Python AI Agent: POST /api/chat/stream (携带 Token)
Python AI Agent -> Redis: 获取对话历史
Python AI Agent -> LLM: 发送对话请求
LLM -> Python AI Agent: 流式返回响应
Python AI Agent -> 前端: SSE 流式推送
Python AI Agent -> Redis: 保存对话历史
```

## API 接口

### 基础信息

- **Base URL**: `http://localhost:8000`
- **认证方式**: JWT Bearer Token

### 接口列表

#### 1. 聊天助手 `/api/chat`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/chat/stream` | 流式对话接口 |

**请求示例**：

```json
{
  "message": "你好，请帮我搜索一下最近的新闻"
}
```

#### 2. 文档助手 `/api/document`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/document/stream` | 流式文档处理 |

**请求示例**：

```json
{
  "url": "https://example.com/document.pdf",
  "summary_type": "detailed",
  "requirements": "请重点总结技术相关内容"
}
```

#### 3. 网盘查询 `/api/pan`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/pan/query` | 自然语言查询网盘 |

**请求示例**：

```json
{
  "query": "帮我查看最近上传的图片文件"
}
```

**响应示例**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "type": "file_list",
    "data": [
      {
        "id": 1,
        "file_id": "abc123",
        "file_name": "photo.jpg",
        "file_type": "img",
        "file_size": 1024000,
        "gmt_create": "2026-01-15T10:00:00"
      }
    ]
  }
}
```

## 核心功能详解

### 1. 智能聊天助手

基于 LangChain 的 OpenAI Functions Agent 实现，具备以下特性：

- **流式响应**：实时返回对话内容，提升用户体验
- **对话历史**：基于 Redis 存储对话历史，支持上下文理解
- **智能摘要**：自动生成对话摘要，优化长对话场景
- **工具调用**：支持网络搜索等外部工具扩展

### 2. 文档处理助手

支持多种文档格式的智能处理：

- **HTML 解析**：自动提取网页正文内容
- **PDF 解析**：支持在线 PDF 流式下载与解析
- **智能总结**：支持简要/详细/要点等多种总结模式
- **流式输出**：边处理边返回，适用于大文档场景

### 3. 网盘查询助手

基于 LangChain SQL Agent 实现的智能查询：

- **自然语言查询**：将用户问题转换为 SQL 查询
- **数据安全**：强制用户隔离，仅允许 SELECT 操作
- **结构化响应**：返回标准化的 JSON 数据格式
- **多种查询类型**：支持文件列表、存储统计、类型分布等

## 安全特性

- **JWT 认证**：所有 API 接口均需要 JWT Token 认证
- **用户隔离**：网盘查询强制绑定用户 ID，确保数据隔离
- **只读查询**：网盘 Agent 仅支持 SELECT 操作，禁止数据修改
- **非 Root 运行**：Docker 容器使用非 root 用户运行

## 配置说明

所有配置项均可通过环境变量或 `.env` 文件进行设置：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `REDIS_HOST` | Redis 主机地址 | localhost |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `MYSQL_HOST` | MySQL 主机地址 | localhost |
| `MYSQL_PORT` | MySQL 端口 | 3306 |
| `LLM_MODEL_NAME` | 大模型名称 | qwen-plus |
| `LLM_TEMPERATURE` | 生成温度 | 0.7 |
| `JWT_ACCESS_TOKEN_EXPIRE_MINUTES` | Token 过期时间(分钟) | 30 |

## 开发指南

### 添加新的智能体

1. 在 `agents/` 目录下创建新的 Agent 文件
2. 在 `routers/` 目录下添加对应的 API 路由
3. 在 `models/` 目录下定义请求/响应模型
4. 在 `app/main.py` 中注册路由

### 添加新的工具

1. 在 `tools/` 目录下创建工具类
2. 实现 `create_tools()` 方法返回 LangChain Tool 列表
3. 在对应的 Agent 中引入并注册工具

## 常见问题

### Q: 如何更换大模型？

修改配置中的 `LLM_MODEL_NAME` 和 `LLM_BASE_URL`，确保使用兼容 OpenAI API 格式的模型服务。

### Q: 如何扩展网盘查询支持的表？

修改 `agents/pan_agent.py` 中 `SQLDatabase.from_uri()` 的 `include_tables` 参数。

### Q: 流式响应如何处理？

前端需使用 SSE (Server-Sent Events) 方式接收数据，每条消息格式为 `data: {json}\n\n`。

## 许可证

本项目仅供学习和内部使用。

## 联系方式

如有问题或建议，请联系项目维护团队。
