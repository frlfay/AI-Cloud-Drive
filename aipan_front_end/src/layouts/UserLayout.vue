<template>
  <div id="user-layout">
    <div class="content">
      <div class="left-section">
        <h2 class="welcome-text">欢迎使用小滴云盘</h2>
        <p class="site-description">安全便捷的云存储服务</p>
        <img
          src="../assets/user_login_bg.svg"
          alt="Cloud Files Illustration"
          class="illustration"
        />
      </div>
      <div class="right-section">
        <div class="right-content">
          <div class="logo-container">
            <img src="../assets/logo.svg" alt="Logo" class="logo-image" />
            <span class="logo-text">小滴云盘</span>
          </div>
          <router-view></router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 用户登录/注册页面的布局组件
 * 左侧展示欢迎信息和背景图，右侧显示登录/注册表单
 */
</script>

<style scoped>
#user-layout {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.content {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.left-section {
  flex: 1;
  background: linear-gradient(135deg, #4e54c8 0%, #8f94fb 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  color: #fff;
}

.welcome-text {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 16px;
}

.site-description {
  font-size: 16px;
  margin-bottom: 32px;
  text-align: center;
}

.illustration {
  max-width: 80%;
  height: auto;
}

.right-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.right-content {
  width: 100%;
  max-width: 360px;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
}

.logo-image {
  width: 48px;
  height: 48px;
  margin-right: 16px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

@media (max-width: 768px) {
  .content {
    flex-direction: column;
  }

  .left-section {
    display: none;
  }

  .right-section {
    padding: 30px;
  }
}
</style>
