<template>
  <div class="document-container">
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
      <!-- 文档选择区域 -->
      <div class="doc-select-area">
        <span class="label">文档选择</span>
        <div class="url-input-container">
          <input 
            type="text" 
            v-model="formData.url"
            placeholder="请输入或选择文档..."
          >
          <button class="select-button" @click="openFileDialog">选择</button>
        </div>
      </div>

      <!-- 四个输入框一行显示 -->
      <div class="input-row">
        <div class="input-item">
          <input 
            type="text" 
            v-model="formData.length"
            placeholder="概要长度"
          >
        </div>
        <div class="input-item">
          <input 
            type="text" 
            v-model="formData.language"
            placeholder="输出语言"
          >
        </div>
        <div class="input-item">
          <input 
            type="text" 
            v-model="formData.summary_type"
            placeholder="生成风格"
          >
        </div>
        <div class="input-item">
          <input 
            type="text" 
            v-model="formData.additional_instructions"
            placeholder="附加内容"
          >
        </div>
      </div>

      <!-- 生成按钮 -->
      <div class="button-area">
        <button 
          class="start-button" 
          @click="startGeneration"
          :disabled="isGenerating"
        >
          开始生成
        </button>
      </div>
    </div>

    <!-- 文件夹选择弹窗 -->
    <el-dialog v-model="fileDialogVisible" title="选择文件" width="50%">
      <div class="file-browser">
        <!-- 面包屑导航 -->
        <div class="breadcrumb" v-if="currentPath.length > 0">
          <span 
            v-for="(item, index) in currentPath" 
            :key="item.id"
            class="breadcrumb-item"
          >
            <span 
              class="breadcrumb-link" 
              @click="navigateTo(index)"
            >{{ item.label }}</span>
            <span v-if="index < currentPath.length - 1" class="separator">/</span>
          </span>
        </div>

        <!-- 文件列表 -->
        <div class="file-list">
          <div 
            v-for="item in currentFiles" 
            :key="item.id"
            class="file-list-item"
            :class="{ 
              'is-folder': item.isDir === 1,
              'is-selected': selectedFile?.id === item.id
            }"
            @click="handleItemClick(item)"
          >
            <el-icon><Document v-if="item.isDir !== 1" /><Folder v-else /></el-icon>
            <span class="file-name">{{ item.fileName }}</span>
            <span class="file-info" v-if="item.isDir !== 1">
              {{ formatFileSize(item.fileSize) }} | {{ formatDate(item.updateTime) }}
            </span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="fileDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmSelection"
            :disabled="!selectedFile || selectedFile.isDir === 1"
          >确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import FolderTree from '@/components/file/FolderTree.vue'
import { listFiles, downloadUrlParam } from '@/api/file'
import { useLoginUserStore } from '@/store/user'
import { Document, Folder } from '@element-plus/icons-vue'
import FileSelectTree from '@/components/file/FileSelectTree.vue'
import LoadingDots from '@/components/common/LoadingDots.vue'
import { getApiUrl, API_PATHS } from '@/config/api'

const { loginUser } = useLoginUserStore()
const generatedContent = ref('')
const isGenerating = ref(false)
const fileDialogVisible = ref(false)
const selectedFile = ref<API.FileDTO | null>(null)
const currentPath = ref<API.TreeNodeDTO[]>([])
const currentFiles = ref<API.FileDTO[]>([])

const formData = reactive({
  url: '',
  length: '',
  language: '',
  summary_type: '',
  additional_instructions: ''
})

// 处理文件或文件夹点击
const handleItemClick = async (item: API.FileDTO) => {
  if (item.isDir === 1) {
    // 如果是文件夹，加载其内容
    try {
      const response = await listFiles({
        parent_id: item.id
      })
      if (response.data?.success && response.data.data) {
        currentFiles.value = response.data.data
        currentPath.value.push({
          id: item.id,
          label: item.fileName
        } as API.TreeNodeDTO)
      }
    } catch (error) {
      console.error('获取文件列表失败:', error)
      ElMessage.error('获取文件列表失败')
    }
  } else {
    // 如果是文件，选中它
    selectedFile.value = item
  }
}

// 导航到指定层级
const navigateTo = async (index: number) => {
  if (index >= currentPath.value.length - 1) return
  
  const targetFolder = currentPath.value[index]
  currentPath.value = currentPath.value.slice(0, index + 1)
  
  try {
    const response = await listFiles({
      parent_id: targetFolder.id
    })
    if (response.data?.success && response.data.data) {
      currentFiles.value = response.data.data
    }
  } catch (error) {
    console.error('获取文件列表失败:', error)
    ElMessage.error('获取文件列表失败')
  }
}

// 打开文件选择弹窗
const openFileDialog = async () => {
  fileDialogVisible.value = true
  currentPath.value = []
  selectedFile.value = null
  
  // 加载根目录文件
  try {
    const response = await listFiles({
      parent_id: 0
    })
    if (response.data?.success && response.data.data) {
      currentFiles.value = response.data.data
    }
  } catch (error) {
    console.error('获取文件列表失败:', error)
    ElMessage.error('获取文件列表失败')
  }
}

// 确认选择
const confirmSelection = async () => {
  if (!selectedFile.value || selectedFile.value.isDir === 1) {
    ElMessage.warning('请选择一个文件')
    return
  }

  try {
    const response = await downloadUrlParam({
      fileIds: [String(selectedFile.value.id)]
    })

    if (response.data?.success && response.data.data) {
      const baseUrl = formData.url.trim()
      const downloadUrl = response.data.data[0].downloadUrl
      formData.url = baseUrl ? `${baseUrl}${downloadUrl}` : downloadUrl
      fileDialogVisible.value = false
      ElMessage.success('文件选择成功')
    } else {
      ElMessage.error('获取文件下载地址失败')
    }
  } catch (error) {
    console.error('获取文件下载地址失败:', error)
    ElMessage.error('获取文件下载地址失败')
  }
}

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let index = 0
  let fileSize = size

  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024
    index++
  }

  return `${fileSize.toFixed(2)} ${units[index]}`
}

// 格式化日期
const formatDate = (timestamp: number) => {
  if (!timestamp) return '-'
  return new Date(timestamp).toLocaleString()
}

// 处理流式响应
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
    formData.url = ''
    formData.summary_type = ''
    formData.language = ''
    formData.length = ''
    formData.additional_instructions = ''
  } catch (error) {
    console.error('Stream reading error:', error)
    generatedContent.value += '\n[读取数据时发生错误]'
  } finally {
    isGenerating.value = false
  }
}

// 开始生成
const startGeneration = async () => {
  if (!formData.url) {
    ElMessage.warning('请选择文档')
    return
  }

  isGenerating.value = true
  try {
    const response = await fetch(getApiUrl(API_PATHS.DOCUMENT), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        url: formData.url,
        summary_type: formData.summary_type,
        language: formData.language,
        length: formData.length,
        additional_instructions: formData.additional_instructions
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
</script>

<style scoped>
.document-container {
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
  min-height: 200px;
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

.doc-select-area {
  margin-bottom: 20px;
}

.label {
  display: inline-block;
  margin-bottom: 8px;
  font-weight: 500;
}

.url-input-container {
  display: flex;
  gap: 10px;
}

.url-input-container input {
  flex: 1;
  padding: 12px;
  border: 2px solid #ededed;
  border-radius: 8px;
  font-size: 14px;
  background: #ffffff;
  cursor: default;
}

.select-button {
  padding: 0 20px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.select-button:hover {
  background: #40a9ff;
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
 max-width: 258px;

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

.selected-file-info {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.info-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.info-content {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.info-content .el-icon {
  font-size: 16px;
  color: #909399;
}

.file-list {
  margin: 10px 0;
  max-height: 400px;
  overflow-y: auto;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-item .el-icon {
  font-size: 16px;
  color: #909399;
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

.file-browser {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
}

.breadcrumb-link {
  color: #409EFF;
  cursor: pointer;
  padding: 2px 4px;
}

.breadcrumb-link:hover {
  background-color: #ecf5ff;
  border-radius: 4px;
}

.separator {
  margin: 0 8px;
  color: #909399;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 400px;
  overflow-y: auto;
}

.file-list-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.file-list-item:hover {
  background-color: #f5f7fa;
}

.file-list-item.is-selected {
  background-color: #ecf5ff;
}

.file-list-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #909399;
}

.file-name {
  flex: 1;
  margin-right: 16px;
}

.file-info {
  color: #909399;
  font-size: 12px;
}

.file-list-item.is-folder {
  color: #409EFF;
}

.file-list-item.is-folder .el-icon {
  color: #409EFF;
}
</style>
