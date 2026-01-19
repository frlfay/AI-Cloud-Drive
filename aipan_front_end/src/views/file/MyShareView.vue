<template>
  <div class="my-share-view">
    <el-table :data="shareList" style="width: 100%">
      <el-table-column prop="shareName" label="分享名称" min-width="200">
        <template #default="{ row }">
          <div class="share-name">
            <el-icon><Share /></el-icon>
            <span>{{ row.shareName }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="shareType" label="分享方式" width="120">
        <template #default="{ row }">
          <el-tag :type="row.shareType === 'no_code' ? 'success' : 'warning'">
            {{ row.shareType === 'no_code' ? "公开分享" : "私密分享" }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="shareEndTime" label="有效期" width="180">
        <template #default="{ row }">
          {{ formatShareEndTime(row.shareEndTime, row.shareDayType) }}
        </template>
      </el-table-column>

      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button-group>
            <el-tooltip
              effect="dark"
              :content="row.shareType === 'need_code' ? '复制链接和提取码' : '复制链接'"
              placement="top"
            >
              <el-button type="primary" link @click="copyShareInfo(row)">
                <el-icon><DocumentCopy /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="取消分享" placement="top">
              <el-button type="danger" link @click="handleCancelShare(row)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-tooltip>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Share, DocumentCopy, Delete } from "@element-plus/icons-vue";
import { getShareUrl, cancel } from "@/api/share";
import { useLoginUserStore } from "@/store/user";

const {loginUser} = useLoginUserStore();
const accountId = loginUser.id;

const shareList = ref<API.ShareDTO[]>([]);

// 格式化日期
const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return "-";
  return new Date(dateStr).toLocaleString();
};

// 格式化有效期
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

// 获取分享列表
const fetchShareList = async () => {
  try {
    const response = await getShareUrl();
    if (response.data?.success) {
      shareList.value = response.data.data as API.ShareDTO[];
    } else {
      ElMessage.error(response.data?.msg || "获取分享列表失败");
    }
  } catch (error) {
    console.error("获取分享列表失败:", error);
    ElMessage.error("获取分享列表失败");
  }
};

// 复制分享信息
const copyShareInfo = (share: API.ShareDTO) => {
  // const shareUrl = String(share.shareUrl?.replace(share.shareUrl?.toString().split("/")[2], window.location.host));
  const shareInfo = `分享链接：${share.shareUrl}${
    share.shareCode ? `\n提取码：${share.shareCode}` : ""
  }`;

  // 添加后备方案
  if (!navigator.clipboard) {
    // 创建临时文本区域
    const textArea = document.createElement("textarea");
    textArea.value = shareInfo;
    document.body.appendChild(textArea);
    textArea.select();

    try {
      document.execCommand("copy");
      ElMessage.success(
        share.shareType === 'need_code' ? "链接和提取码已复制" : "链接已复制"
      );
    } catch (err) {
      ElMessage.error("复制失败，请手动复制");
    }

    document.body.removeChild(textArea);
    return;
  }

  // 如果支持 clipboard API，使用原来的方式
  navigator.clipboard
    .writeText(shareInfo)
    .then(() => {
      ElMessage.success(
        share.shareType === 'need_code' ? "链接和提取码已复制" : "链接已复制"
      );
    })
    .catch(() => {
      ElMessage.error("复制失败，请手动复制");
    });
};

// 取消分享
const handleCancelShare = async (share: API.ShareDTO) => {
  if (!share.id) {
    ElMessage.error("分享ID无效");
    return;
  }

  try {
    await ElMessageBox.confirm("确定要取消此分享吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const response = await cancel({
      shareIds: [share.id],
      accountId : accountId
    });

    if (response.data?.success) {
      ElMessage.success("取消分享成功");
      fetchShareList();
    } else {
      ElMessage.error(response.data?.msg || "取消分享失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消分享失败:", error);
      ElMessage.error("取消分享失败");
    }
  }
};

onMounted(() => {
  fetchShareList();
});
</script>

<style scoped>
.my-share-view {
}

.share-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-table__header) th {
  font-size: 15px;
  font-weight: bold;
  background-color: #f5f7fa;
  padding: 14px 0;
}

:deep(.el-table__body) td {
  padding: 14px 0;
  font-size: 14px;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-button-group .el-button) {
  padding: 4px 8px;
}

:deep(.el-button-group .el-button + .el-button) {
  margin-left: 8px;
}

:deep(.el-icon) {
  font-size: 16px;
}
</style>
