<template>
  <div class="chat-container">
    <div class="message-list" ref="messageList">
      <div v-for="(message, index) in messages" :key="index" 
           :class="['message-item', message.isUser ? 'user-message' : 'assistant-message']">
        <div class="avatar">
          <img :src="message.isUser ? userAvatar : assistantAvatar" alt="avatar"/>
        </div>
        <div class="message-content">
          <template v-if="message.isLoading">
            <LoadingDots />
          </template>
          <div v-else class="message-text" v-html="message.content"></div>
        </div>
      </div>
    </div>
    
    <div class="input-area">
      <div class="input-wrapper">
        <textarea 
          v-model="inputMessage" 
          placeholder="发消息，请输入..."
          @keyup.enter.exact.prevent="sendMessage"
          rows="1"
        ></textarea>
        <div class="send-button" @click="sendMessage" :class="{ 'active': inputMessage.trim() }">
          <svg t="1710400669747" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4231" width="24" height="24">
            <path d="M931.4 481.3L815.9 396l0.3-0.4-604.7-249c-3.8-1.6-7.9-1.9-11.9-1l-0.3-0.7L135 171.3c-12.3 2.7-20.3 14.8-17.6 27.1 0.9 4.3 3.1 8 6.2 10.9l-0.3 0.7 155.6 130.3L492.3 436l-213.4 95.7-140.9 118c-9.7 8.1-11 22.6-2.8 32.3 3.9 4.6 9.3 7.5 15.2 8.1l0.3 0.7 63.9 26.3c4 1.6 8.1 1.9 12.1 1l0.3 0.7 201.4-83 139.3 57.4c4 1.6 8.1 1.9 12.1 0.9l0.3 0.7 63.9-26.3c12.3-2.7 20.3-14.8 17.6-27.1-0.9-4.3-3.1-8-6.2-10.9l0.3-0.7-102.8-86 389.4-174.6c11.3-5.1 16.3-18.3 11.3-29.6-2.3-4.9-6-8.6-10.8-11z" p-id="4232"></path>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, nextTick } from 'vue';
import LoadingDots from '@/components/common/LoadingDots.vue';
import { useLoginUserStore } from "@/store/user";
import { getApiUrl, API_PATHS } from '@/config/api';

export default defineComponent({
  name: 'Chat',
  components: {
    LoadingDots,
  },
  setup() {
    const messages = ref<Array<{ isUser: boolean; content: string; isLoading?: boolean }>>([
      {
        isUser: false,
        content: '亲爱的用户，很高兴能为你解答问题、陪你交流！',
      },
    ]);
    const inputMessage = ref('');
    const messageList = ref<HTMLElement | null>(null);
    const userAvatar = require('@/assets/assistant-avatar.png'); // 用户头像待补充
    const assistantAvatar = require('@/assets/user-avatar.png'); // 助手头像待补充

    const scrollToBottom = () => {
      if (messageList.value) {
        messageList.value.scrollTop = messageList.value.scrollHeight;
      }
    };

    const processStreamResponse = async (response: Response) => {
      const reader = response.body?.getReader();
      if (!reader) return;

      const currentMessageIndex = messages.value.length - 1;
      const decoder = new TextDecoder();
      
      try {
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          // 解码新的数据块
          const chunk = decoder.decode(value, { stream: true });
          const lines = chunk.split('\n');
          
          for (const line of lines) {
            if (line.trim() === '' || line.trim() === 'data: [DONE]') continue;
            
            try {
              const jsonStr = line.replace(/^data: /, '').trim();
              if (!jsonStr) continue;
              
              const jsonData = JSON.parse(jsonStr);
              if (jsonData.code === 0 && jsonData.data) {
                // 立即更新消息内容并移除加载状态
                messages.value[currentMessageIndex].isLoading = false;
                messages.value[currentMessageIndex].content += jsonData.data;
                
                // 强制更新视图并滚动到底部
                await nextTick(() => {
                  scrollToBottom();
                });
              }
            } catch (e) {
              console.error('Error parsing JSON:', e);
            }
          }
        }
      } catch (error) {
        console.error('Stream reading error:', error);
        messages.value[currentMessageIndex].content = '[读取数据时发生错误]';
      } finally {
        messages.value[currentMessageIndex].isLoading = false;
        scrollToBottom();
      }
    };

    const sendMessage = async () => {
      if (!inputMessage.value.trim()) return;

      const userMessage = inputMessage.value;
      messages.value.push({ isUser: true, content: userMessage });
      messages.value.push({
        isUser: false,
        content: '',
        isLoading: true,
      });
      inputMessage.value = '';
      scrollToBottom();

      try {
        console.log('Sending message:', userMessage);
        const response = await fetch(getApiUrl(API_PATHS.CHAT), {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            token: useLoginUserStore().token || '',
          },
          body: JSON.stringify({
            message: userMessage,
          }),
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error('Server response:', response.status, errorText);
          throw new Error(`Server responded with ${response.status}: ${errorText}`);
        }

        await processStreamResponse(response);
      } catch (error) {
        console.error('Error details:', error);
        messages.value.pop();
        messages.value.push({
          isUser: false,
          content: '抱歉，发生了错误，请稍后重试。',
          isLoading: false,
        });
      }
      scrollToBottom();
    };

    return {
      messages,
      inputMessage,
      messageList,
      sendMessage,
      userAvatar,
      assistantAvatar,
    };
  },
});
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ffffff;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.message-item.user-message {
  margin-left: auto;
  flex-direction: row-reverse;
}

.message-item.user-message .message-content {
  background-color: #efefef;
}

.message-item.assistant-message {
  margin-right: auto;
}

.message-item.assistant-message .message-content {
  background-color: #f5f8fc;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  min-height: 24px;
}

.input-area {
  padding: 20px;
  background-color: #ffffff;
}

.input-wrapper {
  position: relative;
  max-width: 1080px;
  min-height: 100px;
  margin: 0 auto;
  background-color: #ffffff;
  border-radius: 8px;
  border: 2px solid #ededed;
  padding: 12px 50px 12px 16px;
}

.input-wrapper textarea {
  width: 100%;
  border: none;
  background: transparent;
  resize: none;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
  max-height: 120px;
  overflow-y: auto;
}

.send-button {
  position: absolute;
  right: 12px;
  bottom: 8px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;
}

.send-button svg {
  fill: #999;
  transition: all 0.3s;
}

.send-button.active svg {
  fill: #1890ff;
}

.send-button:hover {
  background-color: rgba(24, 144, 255, 0.1);
}

.send-button.active:hover {
  background-color: rgba(24, 144, 255, 0.2);
}
</style>
