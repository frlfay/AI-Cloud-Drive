<template>
  <div class="folder-tree">
    <el-tree
      ref="treeRef"
      :data="treeData"
      :load="loadNode"
      
      @node-click="handleNodeClick"
      :highlight-current="true"
      node-key="id"
      
    >
      <template #default="{ node }">
        <span class="custom-tree-node">
          <el-icon><Folder /></el-icon>
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
import { onMounted, ref} from "vue";
const emit = defineEmits(["select"]);

const treeRef = ref<InstanceType<typeof ElTree>>();
const treeData = ref<API.TreeNodeDTO[]>([]);

/**
 * 树节点的配置属性
 * children: 子节点数据的字段名
 * label: 节点显示文本的字段名
 * isLeaf: 判断节点是否为叶子节点的函数
 */
const defaultProps = {
  children: "children",
  label: "label",
  isLeaf: (data: API.TreeNodeDTO) => data.state == "closed",
};

/**
 * 处理节点点击事件
 * 触发选择事件，并传递节点数据和路径信息
 * @param data 被点击的节点数据
 * @param node 节点实例
 */
const handleNodeClick = (data: API.TreeNodeDTO, node: any) => {
  emit("select", {
    ...data,
    path: data.label
  })
  loadNode({ level: 0 }, (data) => {
    treeData.value = data;
  });
};

/**
 * 加载节点数据
 * 用于懒加载树节点，根节点加载整个文件树，子节点直接使用现有数据
 * @param node 当前节点实例
 * @param resolve 加载完成的回调函数
 */

  const loadNode =  async (
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
    // 子节点直接使用现有的 children 数据
     resolve(node.data.children || []);
  }
};

/**
 * 组件挂载时初始化文件树
 * 加载根节点数据
 */
onMounted (() => {
  loadNode({ level: 0 }, (data) => {
    treeData.value = data;
  });
  
});


</script>

<style scoped>
.folder-tree {
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
</style>
