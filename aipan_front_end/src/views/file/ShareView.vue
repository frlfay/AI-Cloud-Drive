<template>
  <div class="share-view">
    <!-- 验证页面 -->
    <div v-if="needVerify" class="verify-page">
      <div class="verify-left">
        <div class="share-info">
          <div class="share-user">
            <el-avatar
              :size="100"
              :src="shareInfo?.shareAccountDTO?.avatarUrl"
              class="user-avatar"
            />
            <div class="user-info">
              <span class="user-name">{{
                shareInfo?.shareAccountDTO?.username
              }}</span>
              <span class="share-title">分享了{{ shareInfo?.shareName }}</span>
            </div>
          </div>
        </div>
        <div class="verify-form">
          <el-input
            v-model="verifyForm.shareCode"
            placeholder="请输入提取码"
            class="verify-input"
            :prefix-icon="Lock"
            @keyup.enter="handleVerify"
          />
          <el-button type="primary" class="verify-button" @click="handleVerify">
            提取文件
          </el-button>
        </div>
      </div>
      <div class="verify-right">
        <img src="@/assets/verify_bg.svg" alt="分享" class="verify-image" />
      </div>
    </div>

    <!-- 文件列表 -->
    <div v-if="shareDetail && !needVerify" class="file-container">
      <div class="share-info-bar">
        <div class="share-user">
          <el-avatar
            :size="40"
            :src="shareInfo?.shareAccountDTO?.avatarUrl"
            class="user-avatar"
          />
          <div class="user-info">
            <span class="user-name">{{
              shareInfo?.shareAccountDTO?.username
            }}</span>
          </div>
        </div>
        <div class="share-actions">
          <el-button type="primary" @click="handleSaveClick">
            <el-icon><FolderAdd /></el-icon>
            保存{{
              selectedFiles.length ? `(${selectedFiles.length}个文件)` : ""
            }}
          </el-button>
        </div>
      </div>

      <!-- 添加面包屑导航 -->
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item @click="handlePathChange('/')"
            >全部文件</el-breadcrumb-item
          >
          <template v-for="(path, index) in currentPathArray" :key="index">
            <el-breadcrumb-item @click="handlePathChange(getFullPath(index))">
              {{ path }}
            </el-breadcrumb-item>
          </template>
        </el-breadcrumb>
      </div>

      <div class="file-list">
        <FileTable
          :fileList="currentFileList"
          @fileClick="handleFileClick"
          @selection-change="handleSelectionChange"
        />
        <div class="pagination-container">
          <div class="pagination-wrapper">
            <span class="total-info">共 {{ pagination.total }} 条</span>
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.pageSize"
              :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 添加底部信息 -->
    <div class="footer-info">
      <div class="footer-links">
        <a href="#" target="_blank">服务协议</a>
        <span class="divider">|</span>
        <a href="#" target="_blank">隐私政策</a>
        <span class="divider">|</span>
        <a href="#" target="_blank">帮助中心</a>
        <span class="divider">|</span>
        <a href="#" target="_blank">问题反馈</a>
      </div>
      <div class="copyright">©2024 DPan 版权所有</div>
    </div>

    <!-- 添加存对话框 -->
    <el-dialog v-model="saveDialogVisible" title="保存到网的网盘" width="50%">
      <div class="save-dialog-content">
        <div class="save-file-info">
          <template v-if="selectedFiles.length === 1">
            <p>文件名：{{ selectedFiles[0]?.fileName }}</p>
          </template>
          <template v-else>
            <p>已选择 {{ selectedFiles.length }} 个文件</p>
          </template>
          <p>选择要保存到的文件夹：</p>
        </div>
        <div class="folder-tree-container">
          <FolderTree @select="handleFolderSelect" />
        </div>
        <div class="selected-path">
          <span>当前选择：</span>
          <el-tag size="small">{{ saveForm.filePath }}</el-tag>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="saveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="delayedHandleSave">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {
  checkShareCode,
  getShareDetail,
  getShareFileList,
  saveFiles,
  visitShare,
} from "@/api/share";
import FileTable from "@/components/file/FileTable.vue";
import FolderTree from "@/components/file/FolderTree.vue";
import { FolderAdd, Lock } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useLoginUserStore } from "@/store/user";

const { loginUser } = useLoginUserStore();
const accountId = loginUser.id;

const route = useRoute();
const shareId = route.params.shareId as string;

const shareInfo = ref<API.ShareDetailDTO>();
const shareDetail = ref<API.ShareDetailDTO>();
const needVerify = ref(false);
const verifyForm = ref({
  shareCode: "",
});

// 格式化日期
const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return "-";
  return new Date(dateStr).toLocaleString();
};

// 添加获取和设置 Share-Token 的工具函数
const getShareToken = (shareId: string) => {
  return localStorage.getItem(`share_token_${shareId}`);
};

const setShareToken = (shareId: string, token: string) => {
  localStorage.setItem(`share_token_${shareId}`, token);
};

// 访问分享链接
const visitShareLink = async () => {
  try {
    // 先检查本地是否有 token
    const storedToken = getShareToken(shareId);
    if (storedToken) {
      // 如果有 token，直接尝试获取详情
      const detailResponse = await getShareDetail({
        headers: {
          "Share-Token": storedToken,
        },
      });

      if (detailResponse.data?.success && detailResponse.data.data) {
        shareDetail.value = detailResponse.data.data as API.ShareDetailDTO;
        shareInfo.value = detailResponse.data.data as API.ShareDetailDTO;
        needVerify.value = false;
        return;
      }
    }

    // 如果没有 token 或 token 失效，则正常访问
    const response = await visitShare({
      shareId: shareId,
    });

    if (response.data?.success && response.data.data) {
      const data = response.data.data as API.ShareDetailDTO;
      shareInfo.value = data;

      console.log("=======data======", shareInfo.value);
      localStorage.setItem("share_token", response.data.data.shareToken);

      const resp = await getShareDetail({
        headers: {
          "Share-Token": localStorage.getItem("share_token"),
        },
      });

      if (resp.data?.success && resp.data.data) {
        shareDetail.value = resp.data.data as API.ShareDetailDTO;
        needVerify.value = false;
      }

      if (data.shareType === "no_code") {
        needVerify.value = false;
      } else {
        needVerify.value = true;
      }
    } else {
      ElMessage.error(response.data?.msg || "访问分享失败");
    }
  } catch (error) {
    console.error("访问分享出错:", error);
    ElMessage.error("访问分享失败");
  }
};

// 验证分享码
const handleVerify = async () => {
  if (!verifyForm.value.shareCode) {
    ElMessage.warning("请输��分享码");
    return;
  }

  try {
    const response = await checkShareCode({
      shareId: shareId,
      shareCode: verifyForm.value.shareCode,
    });

    if (response.data?.success && response.data.data) {
      const shareToken = response.data.data.toString();
      // 存储 token
      setShareToken(shareId, shareToken);

      // 使用 token 获取详情
      const detailResponse = await getShareDetail({
        headers: {
          "Share-Token": shareToken,
        },
      });

      if (detailResponse.data?.success && detailResponse.data.data) {
        shareDetail.value = detailResponse.data.data as API.ShareDetailDTO;
        needVerify.value = false;
      } else {
        ElMessage.error(detailResponse.data?.msg || "获取分享详情失败");
      }
    } else {
      ElMessage.error(response.data?.msg || "验证码错误");
    }
  } catch (error) {
    console.error("验证分享码出错:", error);
    ElMessage.error("验证失败");
  }
};

// 添加路径相关的应式变量
const currentPath = ref("/");
const currentFileList = ref<API.FileDTO[]>([]);
// 添加路径相关的响应式变量
// 计算当前路径数组
const currentPathArray = computed(() => {
  return currentPath.value.split("/").filter(Boolean);
});

// 获取完整路径
const getFullPath = (index: number) => {
  return "/" + currentPathArray.value.slice(0, index + 1).join("/") + "/";
};

// 添加分页相关的响应式变量
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 更新文件列表
const updateFileList = async () => {
  if (!shareDetail.value?.fileDTOList) return;

  // 如果是根路径，显示所有共享的文件/文件夹
  if (currentPath.value === "/") {
    currentFileList.value = shareDetail.value.fileDTOList.map((file) => ({
      ...file,
      fileType: file.isDir === 1 ? "folder" : file.fileType || "other",
      fileSuffix: file.isDir === 1 ? "folder" : file.fileSuffix || "other",
    }));
    pagination.value.total = shareDetail.value.fileDTOList.length;
    return;
  }

  // 构建查询参数
  const queryParams: API.ShareFileQueryRequest = {
    shareId: shareId,
    parentId: String(localStorage.getItem("shareid")),
  };

  try {
    const storedToken = getShareToken(shareId);
    let response;

    if (storedToken) {
      response = await getShareFileList(queryParams, {
        headers: {
          "Share-Token": storedToken,
        },
      });
    } else {
      response = await getShareFileList(queryParams, {
        headers: {
          "Share-Token": localStorage.getItem("share_token"),
        },
      });
    }

    if (response.data?.success && response.data.data) {
      currentFileList.value = response.data.data.map((file: API.FileDTO) => ({
        ...file,
        fileSuffix: file.isDir === 1 ? "folder" : file.fileSuffix || "other",
        fileType: file.isDir === 1 ? "folder" : file.fileType || "other",
      }));
      // 更新分页信息
      pagination.value.total = Number(response.data.data.total) || 0;
    } else {
      ElMessage.error(response.data?.msg || "获取文件列表失败");
    }
  } catch (error) {
    console.error("获取文件列表失败:", error);
    ElMessage.error("获取文件列表失败");
  }
};

// 处理文件点击
const handleFileClick = async (file: API.FileDTO) => {
  if (file.isDir === 1) {
    currentPath.value = `${file.fileName}/`;
    // currentPath.value = getFullPath(currentPathArray.value.length) + file.fileName + "/";
    pagination.value.current = 1; // 重置到第一页
    localStorage.setItem("shareid", String(file.id));
    localStorage.setItem("parentId", String(file.parentId));
    console.log("Folder clicked:", file);
    await updateFileList();
  } else {
    // TODO: 处理文件预览或下载
    console.log("File clicked:", file);
  }
};

// 处理路径变化
const handlePathChange = async (path: string) => {
  currentPath.value = path;
  pagination.value.current = 1; // 重置到第一页
  await updateFileList();
};

// 监听分享详情变化
watch(
  () => shareDetail.value,
  () => {
    currentPath.value = "/";
    updateFileList();
  },
  { immediate: true }
);

// 添加分页处理函数
const handleSizeChange = async (size: number) => {
  pagination.value.pageSize = size;
  pagination.value.current = 1; // 重置到第一页
  await updateFileList();
};

const handleCurrentChange = async (page: number) => {
  pagination.value.current = page;
  await updateFileList();
};

// 添加格式化有效期的函数
const formatShareEndTime = (
  endTime: string | undefined,
  dayType: number | undefined
) => {
  if (!endTime) return "-";
  // 如果是永久有效
  if (dayType === 0) {
    return "永久有效";
  }
  return new Date(endTime).toLocaleString();
};

// 添加转存关的响应式变量
const saveDialogVisible = ref(false);
const saveForm = ref({
  filePath: "/",
  fileIds: [] as any,
});
const selectedFile = ref<API.FileDTO | null>(null);

// 修改选中文件的响应式变量初始化
const selectedFiles = ref<API.FileDTO[]>([]);

// 修改文件选择变化的处理函数
const handleSelectionChange = (selection: API.FileDTO[]) => {
  selectedFiles.value = selection;
  saveForm.value.fileIds = selection.map((file) => file.id);
};

// 修改保存按钮的点击处理函数

const handleSaveClick = () => {
  if (!selectedFiles.value.length) {
    ElMessage.warning("请选择要保存的文件");
    return;
  }

  saveForm.value.fileIds = selectedFiles.value.map((file) => String(file.id));
  console.log("保存文件:", saveForm.value.fileIds);
  saveDialogVisible.value = true;
};

const handleFolderSelect = (node: API.TreeNodeDTO & { path: string }) => {
  saveForm.value.filePath = node.path;
  localStorage.setItem("fileid", String(node.id));
};

const handleSave = async () => {
  if (!saveForm.value.filePath) {
    ElMessage.warning("请选择保存位置");
    return;
  }

  try {
    const storedToken = getShareToken(shareId);
    let response;
    console.log("即将转存的ID:", shareId);
    console.log("即将转存的文件ID:", saveForm.value.fileIds);
    const saveParams = {
      shareId: shareId,
      fileIds: saveForm.value.fileIds,
      parentId: String(localStorage.getItem("fileid")),
      accountId: accountId,
    };

    if (storedToken) {
      response = await saveFiles(saveParams, {
        headers: {
          "Share-Token": storedToken,
        },
      });
    } else {
      response = await saveFiles(saveParams, {
        headers: {
          "Share-Token": localStorage.getItem("share_token"),
        },
      });
    }

    if (response.data?.success) {
      ElMessage.success("文件转存成功");
      saveDialogVisible.value = false;
      // 重置表单
      saveForm.value = {
        filePath: "/",
        fileIds: [],
      };
    } else {
      if (loginUser.storageDTO.usedSize > loginUser.storageDTO.totalSize) {
        ElMessage.error("转存失败,存储空间不足");
      } else {
        ElMessage.error(response.data.msg);
      }
    }
  } catch (error) {
    console.error("转存文件失败:", error);
    ElMessage.error("转存失败");
  }
};

const isSaving = ref(false);

// 定义一个新的方法来处理延迟调用和按钮状态
const delayedHandleSave = () => {
  if (isSaving.value) return;

  isSaving.value = true;

  setTimeout(async () => {
    await handleSave();
    // 操作完成后重新允许点击
    isSaving.value = false;
  }, 500);
};

onMounted(() => {
  visitShareLink();
});
</script>

<style scoped>
.share-view {
  height: calc(100vh - 60px);
  overflow: hidden;
}

.verify-page {
  display: flex;
  height: 100%;
  position: relative;
  padding-bottom: 100px;
}

.verify-left {
  flex: 0.3;
  padding: 130px 100px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(5px) saturate(180%);
  -webkit-backdrop-filter: blur(5px) saturate(180%);
}

.share-info {
  width: 400px;
  margin-bottom: 60px;
}

.share-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 30px;
  color: #000000;
  margin-bottom: 10px;
}

.share-time {
  font-size: 13px;
  color: #999;
}

.share-title {
  font-size: 16px;
  color: #666;
  line-height: 1.5;
}

.verify-form {
  width: 400px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

:deep(.el-input__wrapper) {
  height: 52px;
  border-radius: 26px;
}

:deep(.el-input__inner) {
  height: 52px;
}

.verify-button {
  height: 52px;
  border-radius: 26px;
  font-size: 16px;
  font-weight: 500;
  background-color: #3370ff;
  border: none;
  transition: background-color 0.3s;
}

.verify-button:hover {
  background-color: #2b62e6;
}

.verify-right {
  position: absolute;
  right: 0;
  top: 0;
  height: 80%;
  width: 65%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  transition: transform 0.5s ease;
}

.verify-image {
  width: 600px;
  max-width: 95%;
  transition: opacity 0.5s ease;
  will-change: opacity;
  margin-right: -40px;
}

@media (max-width: 1200px) {
  .verify-right {
    width: 85%;
  }

  .verify-image {
    width: 550px;
  }
}

@media (max-width: 900px) {
  .verify-right {
    width: 90%;
  }

  .verify-image {
    width: 500px;
  }
}

@media (max-width: 768px) {
  .verify-right {
    width: 100%;
    opacity: 0.5;
  }

  .verify-left {
    flex: 1;
    padding: 80px 40px 0;
  }

  .share-info,
  .verify-form {
    width: 100%;
    max-width: 400px;
  }
}

.footer-info {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 0;
  text-align: center;
  font-size: 12px;
  color: #909399;
  background: linear-gradient(
    to bottom,
    transparent,
    rgba(255, 255, 255, 0.9) 20%
  );
  z-index: 3;
}

.footer-links {
  margin-bottom: 8px;
}

.footer-links a {
  color: #909399;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #409eff;
}

.divider {
  margin: 0 12px;
  color: #dcdfe6;
}

.copyright {
  color: #c0c4cc;
}

.file-container {
  padding: 0; /* 移除内边距 */
  background: white;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.share-info-bar {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breadcrumb {
  padding: 12px 24px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-breadcrumb__item) {
  cursor: pointer;
}

.file-list {
  flex: 1;
  overflow: auto;
  padding: 0 24px;
  display: flex;
  flex-direction: column;
  padding-bottom: 100px;
}

.share-meta {
  display: flex;
  gap: 24px;
  color: #666;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pagination-container {
  padding: 16px 0;
  background-color: white;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.total-info {
  position: absolute;
  left: 24px;
  color: #606266;
  font-size: 14px;
}

:deep(.el-pagination) {
  justify-content: center;
}

.share-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.share-actions .el-button {
  display: flex;
  align-items: center;
  gap: 4px;
}

.share-actions .el-icon {
  font-size: 16px;
}

.save-dialog-content {
  padding: 0 20px;
}

.save-file-info {
  margin-bottom: 16px;
}

.save-file-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.folder-tree-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 16px;
  max-height: 300px;
  overflow-y: auto;
}

.selected-path {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
  margin-top: 16px;
}

.selected-path .el-tag {
  flex: 1;
}
</style>
