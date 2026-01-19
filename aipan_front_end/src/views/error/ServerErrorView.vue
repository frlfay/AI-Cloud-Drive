<template>
  <div class="error-page">
    <div class="error-content">
      <el-image
        src="https://gw.alipayobjects.com/zos/rmsportal/RVRUAYdCGeYNBWoKiIwB.svg"
        class="error-image"
      />
      <h1>500</h1>
      <h2>抱歉，服务器出错了</h2>
      <p>请稍后再试或联系技术支持</p>
      <div class="button-group">
        <el-button @click="handleRetry">重试</el-button>
        <el-button type="primary" @click="$router.push('/')"
          >返回首页</el-button
        >
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

const handleRetry = () => {
  // 返回上一页并刷新
  router.go(0);
};
</script>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
}

.error-content {
  text-align: center;
  padding: 40px;
}

.error-image {
  width: 250px;
  margin-bottom: 24px;
}

h1 {
  font-size: 72px;
  font-weight: 600;
  color: var(--el-color-primary);
  margin: 0 0 16px;
}

h2 {
  font-size: 24px;
  color: #1d2129;
  margin: 0 0 16px;
}

p {
  font-size: 16px;
  color: #86909c;
  margin: 0 0 32px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>
