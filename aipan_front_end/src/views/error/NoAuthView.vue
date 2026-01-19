<template>
  <div class="error-page">
    <div class="error-content">
      <el-image
        src="https://gw.alipayobjects.com/zos/rmsportal/wZcnGqRDyhPOEYFcZDnb.svg"
        class="error-image"
      />
      <h1>403</h1>
      <h2>抱歉，您没有访问该页面的权限</h2>
      <p>请联系管理员获取权限或返回首页</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

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
</style>
