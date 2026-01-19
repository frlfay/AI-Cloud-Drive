# AI 智能云盘 (AI Cloud Drive)

基于 **Spring Boot + Python FastAPI + Vue** 构建的智能云盘系统，集成 AI 能力实现智能文件管理、自然语言查询和智能对话等功能。

## 项目架构

```
┌────────────────────────────────────────────────────────────────────────────┐
│                              前端 (Vue 3)                                   │
│                            aipan_front_end/                                │
└──────────────────────────────────┬─────────────────────────────────────────┘
                                   │
         ┌─────────────────────────┴─────────────────────────┐
         │                                                   │
         ▼                                                   ▼
┌─────────────────────────────┐               ┌─────────────────────────────┐
│    Spring Boot 后端          │               │    Python AI Agent          │
│    dcloud-aipan-master/     │               │    dcloud-ai-agent/         │
│    端口: 9090               │               │    端口: 8000               │
│                             │               │                             │
│  ○ 用户认证与管理            │               │  ○ 智能聊天助手              │
│  ○ 文件上传/下载/秒传        │               │  ○ 文档智能总结              │
│  ○ 分片上传                 │               │  ○ 自然语言查询网盘          │
│  ○ 文件分享                 │               │  ○ LLM 大模型调用            │
│  ○ 回收站管理               │               │                             │
└──────────┬──────────────────┘               └──────────────┬──────────────┘
           │                                                 │
           │     ┌─────────────────────────────────────┐     │
           │     │           共享基础设施                │     │
           └────►│                                     │◄────┘
                 │  ┌─────────┐    ┌─────────────────┐ │
                 │  │  MySQL  │    │     Redis       │ │
                 │  │ 业务数据  │    │   缓存/会话     │ │
                 │  └─────────┘    └─────────────────┘ │
                 │                                     │
                 │  ┌─────────────────────────────────┐│
                 │  │    MinIO (S3 兼容对象存储)        ││
                 │  │    文件/图片/视频存储             ││
                 │  └─────────────────────────────────┘│
                 └─────────────────────────────────────┘
                                   │
                                   ▼ (AI Agent 专用)
                 ┌─────────────────────────────────────┐
                 │         阿里云通义千问               │
                 │         (Qwen-Plus 大语言模型)       │
                 └─────────────────────────────────────┘
```

## 项目结构

```
AI-Cloud-Drive/
├── aipan_front_end/          # 前端项目 (Vue 3 + TypeScript)
├── dcloud-aipan-master/      # Spring Boot 后端 (Java 21)
├── dcloud-ai-agent/          # Python AI 智能体服务 (FastAPI + LangChain)
├── rag/                      # RAG 示例代码 (学习用)
└── README.md                 # 本文件
```

## 技术栈

### 后端服务

| 服务 | 技术栈 | 说明 |
|------|--------|------|
| Spring Boot 后端 | Java 21, Spring Boot 3.2, MyBatis-Plus, JWT | 核心业务服务 |
| Python AI Agent | Python 3.12, FastAPI, LangChain, 通义千问 | AI 智能体服务 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| TypeScript | - | 类型安全 |
| Ant Design Vue | - | UI 组件库 |

### 基础设施

| 组件 | 版本 | 说明 |
|------|------|------|
| MySQL | 8.x | 关系型数据库 |
| Redis | 7.x | 缓存服务 |
| MinIO | 最新 | S3 兼容对象存储 |

## 核心功能

### 云盘功能
- 用户注册/登录
- 文件上传、下载、预览
- 文件秒传（基于 MD5）
- 大文件分片上传
- 文件/文件夹管理
- 文件分享（支持提取码）
- 回收站

### AI 智能功能
- 智能对话助手（支持流式响应）
- 文档智能总结（HTML/PDF）
- 自然语言查询网盘（如："查看最近上传的图片"）
- 存储空间智能分析

## 快速开始

### 环境要求

- JDK 21+
- Python 3.12+
- Node.js 18+
- MySQL 8.x
- Redis 7.x
- MinIO

### 1. 启动数据库服务

```bash
# 确保 MySQL 和 Redis 已启动
# 导入数据库
mysql -u root -p < dcloud-aipan-master/dcloud_aipan.sql
```

### 2. 启动 Spring Boot 后端

```bash
cd dcloud-aipan-master
./mvnw spring-boot:run
# 服务启动在 http://localhost:9090
# API 文档: http://localhost:9090/doc.html
```

### 3. 启动 Python AI Agent

```bash
cd dcloud-ai-agent
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python -m uvicorn app.main:app --reload --port 8000
# 服务启动在 http://localhost:8000
```

### 4. 启动前端

```bash
cd aipan_front_end
npm install
npm run serve
# 前端启动在 http://localhost:8080
```

## 配置说明

### JWT 认证

两个后端服务使用相同的 JWT 密钥，实现统一认证：

| 配置项 | 值 |
|--------|-----|
| 密钥 | `xdclass.net168xdclass.net168xdclass.net168xdclass.net168` |
| 算法 | HS256 |
| Token 前缀 | XDCLASS |

### 数据库

两个后端共用同一个 MySQL 数据库 `dcloud_aipan`。

## 子项目文档

- [前端项目文档](./aipan_front_end/README.md)
- [Spring Boot 后端文档](./dcloud-aipan-master/README.md)
- [AI Agent 文档](./dcloud-ai-agent/README.md)

## 许可证

本项目仅供学习和内部使用。

## 联系方式

如有问题，请联系项目维护团队。
