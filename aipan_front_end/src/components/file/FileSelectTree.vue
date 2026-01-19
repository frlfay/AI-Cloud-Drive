<template>
  <div class="file-select-tree">
    <el-tree
      ref="treeRef"
      :data="treeData"
      :load="loadNode"
      @node-click="handleNodeClick"
      :props="defaultProps"
      :highlight-current="true"
      node-key="id"
    >
      <template #default="{ node, data }">
        <span class="custom-tree-node">
          <el-icon>
            <Folder />
          </el-icon>
          <span>{{ node.label }}</span>
        </span>
      </template>
    </el-tree>
  </div>
</template>

<script setup lang="ts">
import { getFileTree } from "@/api/file";
import { Folder } from "@element-plus/icons-vue";
import { ElTree } from "element-plus";
import { onMounted, ref } from "vue";

const emit = defineEmits(["select"]);
const treeRef = ref<InstanceType<typeof ElTree>>();
const treeData = ref<API.TreeNodeDTO[]>([]);

const defaultProps = {
  children: "children",
  label: "label",
  isLeaf: (data: API.TreeNodeDTO) => data.state === "closed",
};

const handleNodeClick = (data: API.TreeNodeDTO, node: any) => {
  emit("select", {
    ...data,
    path: data.label,
  });
};

const loadNode = async (
  node: any,
  resolve: (data: API.TreeNodeDTO[]) => void
) => {
  if (node.level === 0) {
    try {
      const response = await getFileTree();
      if (response.data?.success && response.data.data) {
        treeData.value = response.data.data || [];
        resolve(treeData.value);
      } else {
        resolve([]);
      }
    } catch (error) {
      console.error("获取文件树失败:", error);
      resolve([]);
    }
  } else {
    resolve(node.data.children || []);
  }
};

onMounted(() => {
  loadNode({ level: 0 }, (data) => {
    treeData.value = data;
  });
});
</script>

<style scoped>
.file-select-tree {
  max-height: 400px;
  overflow-y: auto;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.custom-tree-node .el-icon {
  margin-right: 4px;
  font-size: 16px;
  color: #909399;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #e6f7ff;
}
</style> 