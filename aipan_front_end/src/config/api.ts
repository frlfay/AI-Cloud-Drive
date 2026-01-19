// API配置
export const API_BASE_URL = 'http://ai-api.open1024.com/api'

// API路径配置
export const API_PATHS = {
  CHAT: '/chat/stream',
  DOCUMENT: '/document/stream',
  PAN_QUERY: '/pan/query'
}

// 获取完整的API URL
export const getApiUrl = (path: string) => `${API_BASE_URL}${path}` 