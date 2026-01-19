<template>
  <div class="chat-container">
    <div class="chat-header">
      AI聊天智能助理
    </div>

    <div class="chat-content" ref="messageContainer">
      <div v-for="(message, index) in messages"
           :key="index"
           :class="['message-item', message.type]">
        <el-avatar
          v-if="message.type === 'ai'"
          :size="40"
          :src="aiAvatar"
          class="message-avatar"
        />
        <div class="message-content">
          <div v-if="message.type === 'ai' && !message.isHistory" class="typewriter">
            {{ message.displayText }}
          </div>
          <span v-else>{{ message.content }}</span>
        </div>
        <el-avatar
          v-if="message.type === 'user'"
          :size="40"
          :src="userAvatar"
          class="message-avatar"
        />
      </div>
      <div v-if="isLoading" class="message-item ai">
        <el-avatar :size="40" :src="aiAvatar" class="message-avatar" />
        <div class="message-content loading">
          <span class="dot-loading"></span>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="3"
        placeholder="请问需要帮您..."
        resize="none"
        :disabled="isLoading"
        @keyup.enter.prevent="sendMessage"
      />
      <div class="button-group">
        <el-button
          type="primary"
          :loading="isLoading"
          @click="sendMessage">
          发送
        </el-button>
        <el-button
          type="primary"
          circle
          @click="sendMessage"
          :loading="isLoading">
          <el-icon v-if="!isLoading"><Position /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Position } from '@element-plus/icons-vue'
import { useLoginUserStore } from "@/store/user";
const { token } = useLoginUserStore();
const messages = ref([
  {
    type: 'ai',
    content: '亲爱的用户，很高兴能为你解答问题，陪你交流',
    displayText: '亲爱的用户，很高兴能为你解答问题，陪你交流',
    timestamp: new Date().toISOString(),
    isHistory: true
  }
])
const inputMessage = ref('')
const messageContainer = ref(null)
const isLoading = ref(false)
const aiAvatar = '/path/to/ai-avatar.png'
const userAvatar = '/path/to/user-avatar.png'

// 获取历史记录
const fetchChatHistory = async () => {
  try {
    const response = await axios.get('/api/chat/history', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (response.data.messages && Array.isArray(response.data.messages)) {
      const historyMessages = response.data.messages.map(msg => ({
        type: msg.role === 'user' ? 'user' : 'ai',
        content: msg.content,
        displayText: msg.content,
        timestamp: msg.timestamp,
        isHistory: true
      }))
      messages.value = historyMessages
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    if (error.response?.status === 422) {
      const errorDetail = error.response.data.detail
      ElMessage.error(`验证错误: ${errorDetail.map(d => d.msg).join(', ')}`)
    } else {
      ElMessage.error('获取历史记录失败')
    }
    console.error('获取历史记录错误:', error)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  isLoading.value = true

  // 添加用户消息
  const userMessage = {
    type: 'user',
    content: inputMessage.value,
    displayText: inputMessage.value,
    timestamp: new Date().toISOString(),
    isHistory: false
  }
  messages.value.push(userMessage)

  try {
    // 发送聊天请求
    const response = await fetch('/api/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({
        message: inputMessage.value  // 修改为符合接口要求的参数
      })
    })

    if (response.status === 422) {
      const errorData = await response.json()
      throw new Error(`验证错误: ${errorData.detail.map(d => d.msg).join(', ')}`)
    }

    if (!response.ok) {
      throw new Error(`请求失败，状态码: ${response.status}`)
    }

    // 处理流式响应
    await handleStreamResponse(response)
  } catch (error) {
    isLoading.value = false
    ElMessage.error(error.message || 'AI响应失败')
    console.error('发送消息错误:', error)

    // 添加错误提示消息
    messages.value.push({
      type: 'ai',
      content: '很抱歉，处理您的请求时出现错误。请稍后再试。',
      displayText: '很抱歉，处理您的请求时出现错误。请稍后再试。',
      timestamp: new Date().toISOString(),
      isHistory: false
    })
  }

  inputMessage.value = ''
  await nextTick()
  scrollToBottom()
}

// 处理流式响应
const handleStreamResponse = async (response) => {
  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  let aiMessage = {
    type: 'ai',
    content: '',
    displayText: '',
    timestamp: new Date().toISOString(),
    isHistory: false
  }
  messages.value.push(aiMessage)
  isLoading.value = false

  try {
    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      const chunk = decoder.decode(value)
      // 直接使用返回的字符串，不需要 JSON 解析
      aiMessage.content += chunk
      aiMessage.displayText += chunk
      scrollToBottom()
    }
  } catch (error) {
    console.error('处理流式响应错误:', error)
    ElMessage.error('接收AI响应出错')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  const container = messageContainer.value
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

// 打字机效果函数
const typeWriter = (message, speed = 50) => {
  let index = 0
  message.displayText = ''

  const timer = setInterval(() => {
    if (index < message.content.length) {
      message.displayText += message.content[index]
      index++
    } else {
      clearInterval(timer)
    }
  }, speed)
}

// 组件加载时显示欢迎消息和获取历史记录
onMounted(async () => {
  // 不需要对首句使用打字机效果
  await fetchChatHistory()
})

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  scrollToBottom()
}, {deep: true})
</script>

<style scoped>
.chat-container {
  width: 100%;
  height: 100vh;
  margin: 0;
  border-radius: 0;
  box-shadow: none;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  z-index: 1000;
  position: relative;
}

.chat-header {
  padding: 15px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  border-radius: 8px 8px 0 0;
  text-align: center;
  font-weight: bold;
}

.chat-content {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  padding: 10px 12px;
  border-radius: 8px;
  background-color: #f5f7fa;
  margin: 0 10px;
  word-break: break-word;
}

.message-item.user .message-content {
  background-color: #ecf5ff;
}

.message-content.loading {
  display: flex;
  justify-content: center;
  min-width: 60px;
}

.dot-loading {
  display: inline-block;
  position: relative;
  width: 40px;
  height: 20px;
}

.dot-loading:before,
.dot-loading:after,
.dot-loading span {
  content: "";
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #909399;
  margin: 0 4px;
  animation: dotLoading 1.4s infinite ease-in-out both;
}

.dot-loading:before {
  animation-delay: -0.32s;
}

.dot-loading span {
  animation-delay: -0.16s;
}

@keyframes dotLoading {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 12px;
}

.chat-input .el-input {
  flex: 1;
}

.button-group {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

/* 设置滚动条样式 */
.chat-content::-webkit-scrollbar {
  width: 6px;
}

.chat-content::-webkit-scrollbar-thumb {
  background-color: #c0c4cc;
  border-radius: 3px;
}

.chat-content::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}

.typewriter {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
