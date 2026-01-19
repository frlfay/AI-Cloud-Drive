<template>
    <div class="search-container">
      <!-- 问答结果界面 -->
      <div class="answer-container">
        <div v-for="(message, index) in messages" :key="index">
          <div v-if="message.type === 'question'" class="question">{{ message.content }}</div>
          <div v-else class="answer">{{ message.content }}</div>
        </div>
      </div>
      <!-- 输入框 -->
      <div class="input-container">
        <input
          v-model="question"
          placeholder="请输入你的问题"
          @keyup.enter="submitQuestion"
        />
        <button @click="submitQuestion">提问</button>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
import { ref } from 'vue';

// 存储问答消息
const messages = ref<{ type: 'question' | 'answer'; content: string }[]>([]);
// 用户输入的问题
const question = ref('');

// 提交问题
const submitQuestion = async () => {
  if (question.value.trim() === '') return;

  // 添加用户问题到消息列表
  messages.value.push({ type: 'question', content: question.value });
  question.value = '';

  // 初始化答案
  const answer = ref('');
  messages.value.push({ type: 'answer', content: answer.value });

  try {
    const response = await fetch('http://127.0.0.1:8081/ai/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ question: messages.value[messages.value.length - 2].content })
    });

    // 确保服务器返回的是一个可读流
    if (!response.body) throw new Error('Response stream not available.');

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      const chunk = decoder.decode(value, { stream: true });
      answer.value += chunk;
      messages.value[messages.value.length - 1].content = answer.value;
    }
  } catch (error) {
    console.error('请求AI接口出错:', error);
    messages.value[messages.value.length - 1].content = '请求出错，请稍后重试。';
  }
};
</script>
  
  <style scoped>
  .search-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
  }
  
  .answer-container {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
  }
  
  .question {
    color: blue;
    margin-bottom: 5px;
  }
  
  .answer {
    color: green;
    margin-bottom: 10px;
  }
  
  .input-container {
    display: flex;
    padding: 10px;
    border-top: 1px solid #ccc;
  }
  
  .input-container input {
    flex: 1;
    padding: 5px;
    margin-right: 10px;
  }
  
  .input-container button {
    padding: 5px 10px;
  }
  </style>