<template>
  <div class="admin-user-view">
    <el-form :model="formSearchParams" inline class="search-form">
      <el-form-item label="用户名">
        <el-input
          v-model="formSearchParams.username"
          placeholder="请输入用户名"
          clearable
        />
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input
          v-model="formSearchParams.userId"
          placeholder="请输入用户ID"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="doSearch">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table
      :data="dataList"
      style="width: 100%"
      stripe
      border
      highlight-current-row
    >
      <el-table-column
        type="index"
        label="序号"
        width="60"
        :index="(index: number) =>
          index + 1 +
          ((searchParams.current ?? 1) - 1) * (searchParams.pageSize ?? 10)"
      />
      <el-table-column prop="username" label="用户名" />
      <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-image
            :src="row.avatarUrl || getDefaultAvatar(row.userId)"
            style="width: 40px; height: 40px; border-radius: 50%"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="容量">
        <template #default="{ row }">
          <el-progress
            :text-inside="true"
            :stroke-width="18"
            :percentage="calculateStoragePercentage(row.storage)"
            :status="getStorageStatus(row.storage)"
          />
          <div class="storage-info">
            {{ formatStorage(row.storage) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="角色">
        <template #default="{ row }">
          <el-tag :type="getRoleType(row.role)">
            {{ formatRole(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间">
        <template #default="{ row }">
          {{ formatDate(row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" @click="openEditDialog(row)"
            >编辑</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      layout="prev, pager, next, jumper, total"
      :total="total"
      :page-size="searchParams.pageSize"
      :current-page="searchParams.current"
      @current-change="onPageChange"
      class="pagination"
    />

    <el-dialog title="编辑用户信息" v-model="editDialogVisible" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="editForm.userId" disabled />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="容量 (MB)" style="width: 100%">
          <el-input-number v-model="editForm.capacity" :min="1" :step="100">
            <template #suffix>MB</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="0" />
            <el-option label="封号" :value="-1" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
          <div style="width: 120px">
            <ImageUpload v-model="editForm.avatarUrl" />
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateUserInfo">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { listUsersByPage, updateUser } from "@/api/user";
import ImageUpload from "@/components/common/ImageUpload.vue";
import { ElMessage } from "element-plus";
import { ref, watchEffect } from "vue";

const formSearchParams = ref<API.UserQueryRequest>({});

// 初始化搜索条件
const initSearchParams = {
  current: 1,
  pageSize: 10,
};

const searchParams = ref<API.UserQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.UserDTO[]>([]);
const total = ref<number>(10);

// 编辑对话框相关
const editDialogVisible = ref(false);
const editForm = ref<API.UserUpdateRequest>({});

/**
 * 加载数据
 */
const loadData = async () => {
  const res = await listUsersByPage(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data?.data?.records || [];
    total.value = Number(res.data?.data?.total) || 0;
  } else {
    ElMessage.error("获取数据失败，" + res.data?.msg);
  }
};

/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...initSearchParams,
    ...formSearchParams.value,
  };
};

/**
 * 当分页变化时，改变搜索条件，触发数据加载
 * @param page
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 格式化日期
 * @param date
 */
const formatDate = (date: string) => {
  return new Date(date).toLocaleString();
};

/**
 * 格式化角色
 * @param role
 */
const formatRole = (role: number) => {
  switch (role) {
    case 1:
      return "管理员";
    case 0:
      return "普通用户";
    case -1:
      return "封号";
    default:
      return "未知";
  }
};

/**
 * 获取角色标签类型
 * @param role
 */
const getRoleType = (role: number) => {
  switch (role) {
    case 1:
      return "success";
    case 0:
      return "info";
    case -1:
      return "danger";
    default:
      return "";
  }
};

/**
 * 格式化存储信息
 * @param storage
 */
const formatStorage = (storage: API.StorageDTO | undefined) => {
  if (!storage) return "未知";
  const used = calculateStorageSize(storage.storageSize);
  const total = calculateStorageSize(storage.totalStorageSize);
  return `${used} / ${total}`;
};

/**
 * 计算存储使用百分比
 * @param storage
 */
const calculateStoragePercentage = (storage: API.StorageDTO | undefined) => {
  if (!storage || !storage.totalStorageSize) return 0;
  return (
    ((storage.storageSize || 0) / storage.totalStorageSize) *
    100
  ).toFixed(2);
};

/**
 * 获取存储状态
 * @param storage
 */
const getStorageStatus = (storage: API.StorageDTO | undefined) => {
  const percentage = Number(calculateStoragePercentage(storage)); // 使用 Number 代替 parseFloat
  if (percentage > 80) return "exception";
  if (percentage > 50) return "warning";
  return "success";
};

/**
 * 计算储存大小
 * @param size
 */
const calculateStorageSize = (size: number | undefined) => {
  if (size === undefined || isNaN(size)) {
    return "未知";
  }

  let sizeNum = Number(size);
  const units = ["B", "KB", "MB", "GB", "TB"];
  let index = 0;
  while (sizeNum >= 1024 && index < units.length - 1) {
    sizeNum /= 1024;
    index++;
  }
  return `${sizeNum.toFixed(index > 0 ? 1 : 0)} ${units[index]}`;
};

/**
 * 获取默认头像
 * @param userId
 */
const getDefaultAvatar = (userId?: string) => {
  const seed = userId || Math.random().toString(36).substring(2, 15);
  return `https://api.dicebear.com/9.x/micah/svg?seed=${seed}`;
};

/**
 * 打开编辑对话框
 * @param user
 */
const openEditDialog = (user: API.UserDTO) => {
  const { createTime, updateTime, storage, ...updateInfo } = user;
  editForm.value = {
    ...updateInfo,
  };
  editDialogVisible.value = true;
};

/**
 * 更新用户信息
 */
const updateUserInfo = async () => {
  try {
    // 确保 editForm 中包含了最新的 avatarUrl
    const updateData: API.UserUpdateRequest = {
      ...editForm.value,
      avatarUrl: editForm.value.avatarUrl, // 确保包含了头像 URL
    };

    const res = await updateUser(updateData);
    if (res.data.code === 0) {
      ElMessage.success("用户信息更新成功");
      editDialogVisible.value = false;
      loadData();
    } else {
      ElMessage.error("更新失败，" + res.data?.msg);
    }
  } catch (error) {
    ElMessage.error("更新失败，请稍后重试");
  }
};

/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});
</script>

<style scoped>
.admin-user-view {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.search-form {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.storage-info {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style>
