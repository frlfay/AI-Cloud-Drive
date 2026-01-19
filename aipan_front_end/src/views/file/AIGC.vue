<template>
  <div class="aigc-container">
    <!-- 顶部生成内容容器 -->
    <div class="content-container">
      <template v-if="isGenerating">
        <div class="loading-container">
          <LoadingDots />
          <span class="loading-text">正在生成内容...</span>
        </div>
      </template>
      <div v-else class="generated-content" v-html="generatedContent"></div>
    </div>

    <!-- 底部输入区域 -->
    <div class="input-section">
      <!-- 原始方案输入框 -->
      <div class="original-text-area">
        <span class="label">原始方案</span>
        <textarea 
          v-model="formData.originalText"
          placeholder="请输入原始方案内容..."
        ></textarea>
      </div>

      <!-- 三个输入框一行显示 -->
      <div class="input-row">
        <div class="input-item">
          <input 
            type="text" 
            v-model="formData.style"
            placeholder="仿写风格"
          >
        </div>
        <div class="input-item">
          <input 
            type="text" 
            v-model="formData.length"
            placeholder="文案长度"
          >
        </div>
        <div class="input-item">
          <input 
            type="text" 
            v-model="formData.additionalInstructions"
            placeholder="附加要求"
          >
        </div>
      </div>

      <!-- AI仿写按钮 -->
      <div class="button-area">
        <button 
          class="start-button" 
          @click="startGeneration"
          :disabled="isGenerating"
        >
          AI仿写开始
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from 'vue'
import LoadingDots from '@/components/common/LoadingDots.vue'

export default defineComponent({
  name: 'AIGC',
  components: {
    LoadingDots
  },
  setup() {
    const generatedContent = ref('')
    const isGenerating = ref(false)

    const formData = reactive({
      originalText: '',
      style: '',
      length: '',
      additionalInstructions: ''
    })

    const processStreamResponse = async (response: Response) => {
      const reader = response.body?.getReader()
      if (!reader) return

      generatedContent.value = ''
      const decoder = new TextDecoder()

      try {
        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          // 解码当前数据块
          const chunk = decoder.decode(value, { stream: true })
          const lines = chunk.split('\n')
          
          for (const line of lines) {
            if (line.trim() === '' || line.trim() === 'data: [DONE]') continue
            
            try {
              const jsonStr = line.replace(/^data: /, '').trim()
              if (!jsonStr) continue
              
              const jsonData = JSON.parse(jsonStr)
              if (jsonData.code === 0) {
                // 立即更新内容，移除加载状态
                isGenerating.value = false
                generatedContent.value += jsonData.data
              }
            } catch (e) {
              console.error('Error parsing JSON:', e)
            }
          }
        }

        // 响应完成后清空输入框
        formData.originalText = ''
        formData.style = ''
        formData.length = ''
        formData.additionalInstructions = ''
      } catch (error) {
        console.error('Stream reading error:', error)
        generatedContent.value += '\n[读取数据时发生错误]'
      } finally {
        isGenerating.value = false
      }
    }

    const startGeneration = async () => {
      if (!formData.originalText.trim()) {
        alert('请输入原始方案内容')
        return
      }

      isGenerating.value = true
      try {
        const response = await fetch('http://47.119.128.20:8000/api/rewrite/stream', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'token': 'XDCLASSeyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJYRENMQVNTIiwiYWNjb3VudElkIjoxODg5MzEyNzAyNTczMDcyMzg1LCJ1c2VybmFtZSI6IjAwMDAwIiwiaWF0IjoxNzQ0ODYyMzA2LCJleHAiOjE3NDU0NjcxMDZ9.JU50UzHhCe3Pylc9_RDyZU9zWI90JAzpCJrIedypCag'
          },
          body: JSON.stringify({
            original_text: formData.originalText,
            style: formData.style,
            length: formData.length,
            additional_instructions: formData.additionalInstructions
          })
        })

        if (!response.ok) {
          throw new Error('Network response was not ok')
        }

        await processStreamResponse(response)
      } catch (error) {
        console.error('Error:', error)
        generatedContent.value = '生成过程中发生错误，请稍后重试'
        isGenerating.value = false
      }
    }

    return {
      generatedContent,
      formData,
      isGenerating,
      startGeneration
    }
  }
})
</script>

<style scoped>
.aigc-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 20px;
  gap: 20px;
}

.content-container {
  flex: 1;
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  overflow-y: auto;
}

.generated-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

.input-section {
  background: #f5f8fc;
  border-radius: 8px;
  padding: 20px;
}

.original-text-area {
  margin-bottom: 20px;
}

.label {
  display: inline-block;
  margin-bottom: 8px;
  font-weight: 500;
}

textarea {
  margin-left: -13px;
  width: 100%;
  height: 120px;
  padding: 12px;
  border: 2px solid #ededed;
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  background: #ffffff;
}

.input-row {
  display: flex;
  gap: 20px;
  
  margin-bottom: 20px;
}

.input-item {
  flex: 1;
  margin-bottom: 50px;
 padding: 10px;
 max-width: 360px;

}

.input-item input {
  width: 100%;
  padding: 12px;
  border: 2px solid #ededed;
  border-radius: 8px;
  font-size: 14px;
  background: #ffffff;
}

.button-area {
  display: flex;
  justify-content: center;
}

.start-button {
  padding: 12px 40px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.start-button:hover {
  background: #40a9ff;
}

.start-button:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

textarea:focus,
input:focus {
  outline: none;
  border-color: #1890ff;
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100px;
  color: #909399;
}

.loading-text {
  margin-top: 8px;
  font-size: 14px;
}
</style>
