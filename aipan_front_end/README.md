# 小滴云盘前端项目

## 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0 或 yarn >= 1.22.0
- Vue CLI >= 5.0.0

## 项目设置

### 1. 安装依赖

```bash
# 使用 npm
npm install

# 或使用 yarn
yarn install
```

### 2. 开发环境运行

```bash
# 使用 npm
npm run serve

# 或使用 yarn
yarn serve
```

### 3. 生产环境构建

```bash
# 使用 npm
npm run build

# 或使用 yarn
yarn build
```

## 后端接口配置

### 1. 基础接口配置

后端接口配置文件位置：`src/utils/request.ts`

你可以在此文件中修改 `baseURL` 来更改后端请求地址：

```typescript
const myAxios = axios.create({
  baseURL: "http://47.119.128.20:8080/api",  // 修改这里的地址为你的后端服务地址
  timeout: 10000,
  withCredentials: true,
  // ...
});
```

### 2. AI 接口配置

AI 相关接口配置文件位置：`src/config/api.ts`

你可以在此文件中修改 AI 接口的基础地址和各个接口路径：

```typescript
// API基础地址
export const API_BASE_URL = 'http://107fopb479409.vicp.fun/api'  // 修改这里的地址为你的AI服务地址

// API路径配置
export const API_PATHS = {
  CHAT: '/chat/stream',      // AI聊天接口
  DOCUMENT: '/document/stream',  // AI文档处理接口
  PAN_QUERY: '/pan/query'    // AI云盘查询接口
}
```

## npm 镜像加速配置

为了加快依赖包的下载速度，你可以使用以下命令配置 npm 镜像：

### 使用淘宝镜像

```bash
# 设置淘宝镜像
npm config set registry https://registry.npmmirror.com

# 或使用 yarn 设置淘宝镜像
yarn config set registry https://registry.npmmirror.com
```

### 查看当前镜像
```bash
npm config get registry
# 或
yarn config get registry
```

### 恢复官方镜像
```bash
npm config set registry https://registry.npmjs.org
# 或
yarn config set registry https://registry.yarnpkg.com
```

## 项目技术栈

- Vue 3
- TypeScript
- Element Plus
- Ant Design Vue
- Axios
- Pinia
- Vue Router
