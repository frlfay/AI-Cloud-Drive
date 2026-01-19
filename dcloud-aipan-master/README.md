# DCloud AI Pan - 智能云盘项目

## 项目简介

DCloud AI Pan 是一个基于 Spring Boot 3.2 构建的智能云盘后端服务，提供完整的个人文件云存储解决方案。项目采用现代化的技术栈，支持文件上传、下载、分享、回收站等核心云盘功能，并且支持大文件分片上传和秒传功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 主要开发语言 |
| Spring Boot | 3.2.4 | 核心框架 |
| MyBatis-Plus | 3.5.6 | ORM 框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | - | 缓存服务 |
| MinIO | - | 对象存储服务（S3 协议兼容） |
| AWS SDK S3 | 1.12.730 | 对象存储客户端 |
| Knife4j | 4.4.0 | API 文档工具 |
| Hutool | 5.8.27 | Java 工具类库 |
| JWT | 0.12.3 | 用户认证 |
| Lombok | 1.18.30 | 代码简化工具 |

## 功能模块

### 1. 用户管理模块 (`/api/account/v1`)

- **用户注册**: 支持用户注册功能
- **用户登录**: 基于 JWT 的用户认证
- **头像上传**: 用户头像上传至对象存储
- **用户详情**: 获取当前登录用户信息

### 2. 文件管理模块 (`/api/file/v1`)

- **文件列表**: 根据父文件夹 ID 查询文件列表
- **创建文件夹**: 支持创建文件夹目录
- **文件重命名**: 修改文件/文件夹名称
- **文件树**: 获取文件夹树形结构
- **普通上传**: 支持小文件直接上传
- **文件秒传**: 基于 MD5 的文件秒传功能
- **分片上传**: 支持大文件分片上传
  - 创建分片上传任务
  - 获取分片上传签名地址
  - 合并分片
  - 查询上传进度
- **文件操作**: 支持批量移动、复制、删除

### 3. 分享管理模块 (`/api/share/v1`)

- **创建分享**: 创建文件分享链接
- **分享列表**: 查看个人分享列表
- **取消分享**: 取消已创建的分享
- **访问分享**: 访问分享链接获取基本信息
- **校验提取码**: 验证分享提取码
- **分享详情**: 查看分享内容详情
- **分享文件列表**: 浏览分享的文件夹内容
- **文件转存**: 将分享文件保存到自己的云盘

### 4. 回收站模块 (`/api/recycle/v1`)

- **回收站列表**: 查看回收站中的文件
- **彻底删除**: 永久删除回收站中的文件
- **文件还原**: 恢复回收站中的文件

## 数据库设计

项目包含以下核心数据表：

| 表名 | 说明 |
|------|------|
| `account` | 用户信息表 |
| `account_file` | 用户文件关联表 |
| `file` | 文件存储表 |
| `file_chunk` | 文件分片信息表 |
| `file_type` | 文件类型表 |
| `file_suffix` | 文件扩展名表 |
| `share` | 分享信息表 |
| `share_file` | 分享文件关联表 |
| `storage` | 用户存储空间表 |

## 项目结构

```
dcloud-aipan-master/
├── src/
│   ├── main/
│   │   ├── java/net/xdclass/
│   │   │   ├── DcloudAipanApplication.java   # 启动类
│   │   │   ├── annotation/                    # 自定义注解
│   │   │   ├── aspect/                        # 切面
│   │   │   ├── component/                     # 组件
│   │   │   ├── config/                        # 配置类
│   │   │   ├── controller/                    # 控制器层
│   │   │   │   ├── req/                       # 请求对象
│   │   │   │   ├── AccountController.java
│   │   │   │   ├── AccountFileController.java
│   │   │   │   ├── RecycleController.java
│   │   │   │   └── ShareController.java
│   │   │   ├── dto/                           # 数据传输对象
│   │   │   ├── enums/                         # 枚举类
│   │   │   ├── exception/                     # 异常处理
│   │   │   ├── interceptor/                   # 拦截器
│   │   │   ├── mapper/                        # MyBatis Mapper
│   │   │   ├── model/                         # 数据模型
│   │   │   ├── service/                       # 服务层
│   │   │   │   └── impl/                      # 服务实现
│   │   │   └── util/                          # 工具类
│   │   └── resources/
│   │       ├── application.yml                # 主配置文件
│   │       ├── application-dev.yml            # 开发环境配置
│   │       ├── application-test.yml           # 测试环境配置
│   │       └── mapper/                        # MyBatis XML 映射文件
│   └── test/                                  # 测试代码
├── dcloud_aipan.sql                           # 数据库初始化脚本
├── pom.xml                                    # Maven 依赖配置
└── README.md                                  # 项目说明文档
```

## 环境要求

- **JDK**: 21+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **MinIO**: 最新版本（或其他 S3 兼容的对象存储服务）

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd dcloud-aipan-master
```

### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE dcloud_aipan DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 导入数据库脚本
mysql -u root -p dcloud_aipan < dcloud_aipan.sql
```

### 3. 配置服务

修改 `src/main/resources/application-dev.yml` 文件，配置以下信息：

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dcloud_aipan?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password

  # Redis 配置
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: your_redis_password

# MinIO 配置
minio:
  endpoint: http://your_minio_host:9000
  access-key: your_access_key
  access-secret: your_access_secret
  bucket-name: ai-pan
  avatar-bucket-name: avatar
```

### 4. 启动项目

```bash
# 使用 Maven 启动
./mvnw spring-boot:run

# 或者编译后启动
./mvnw clean package -DskipTests
java -jar target/dcloud-aipan-0.0.1-SNAPSHOT.jar
```

### 5. 访问服务

- **应用地址**: `http://localhost:9090`
- **API 文档**: `http://localhost:9090/doc.html`

## API 文档

项目集成了 Knife4j（基于 OpenAPI 3），启动项目后访问 `http://localhost:9090/doc.html` 查看完整的 API 文档。

## 特性说明

### 文件秒传

基于文件 MD5 值实现秒传功能。上传文件前，客户端先计算文件 MD5 值并调用秒传接口，如果服务端已存在相同文件，则直接关联用户文件记录，无需重复上传。

### 分片上传

支持大文件分片上传，流程如下：

1. **初始化任务**: 创建分片上传任务，获取 uploadId
2. **获取签名地址**: 根据分片编号获取 MinIO 预签名上传地址
3. **上传分片**: 客户端直接上传分片到 MinIO
4. **合并分片**: 所有分片上传完成后，合并为完整文件

### 分享功能

支持两种分享方式：

- **无提取码分享**: 直接通过链接访问
- **有提取码分享**: 需要输入提取码验证后访问

支持设置分享有效期：

- 永久有效
- 7 天有效
- 30 天有效

## 许可证

本项目仅供学习交流使用。

## 联系方式

如有问题，请联系作者。
