<template>
  <div class="home-container">
    <!-- 顶部欢迎区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="title">智能文件管理系统</h1>
        <p class="subtitle">让文件管理更简单，协作更高效</p>
        <div class="hero-buttons">
          <el-button type="primary" size="large" @click="goToFiles">
            开始使用
          </el-button>
          <!-- <el-button size="large">了解更多</el-button> -->
        </div>
      </div>
      <div class="hero-image">
        <img src="@/assets/file-management.svg" alt="文件管理" />
      </div>
    </div>

    <!-- 特性展示区域 -->
    <div class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="feature-grid">
        <div class="feature-item">
          <el-icon class="feature-icon"><Upload /></el-icon>
          <h3>文件上传</h3>
          <p>支持拖拽上传、批量上传，轻松管理各类文件</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><Share /></el-icon>
          <h3>文件分享</h3>
          <p>一键分享，支持加密分享和访问权限控制</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><Lock /></el-icon>
          <h3>安全存储</h3>
          <p>企业级加密技术，确保数据安全</p>
        </div>
        <div class="feature-item">
          <el-icon class="feature-icon"><Cpu /></el-icon>
          <h3>AI 赋能</h3>
          <p>智能文件分类、内容识别，让管理更高效</p>
        </div>
      </div>
    </div>

    <!-- 统计数据展示 -->
    <div class="stats-section">
      <div class="stat-item">
        <div class="stat-number">100万+</div>
        <div class="stat-label">活跃用户</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">99.9%</div>
        <div class="stat-label">服务可用性</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">50PB+</div>
        <div class="stat-label">数据存储</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import { Upload, Share, Lock, Cpu } from "@element-plus/icons-vue";

export default defineComponent({
  name: "HomeView",
  components: {
    Upload,
    Share,
    Lock,
    Cpu,
  },
  setup() {
    const router = useRouter();

    const goToFiles = () => {
      router.push("/file");
    };

    return {
      goToFiles,
    };
  },
});
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0px 20px;
}

.hero-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 40px;
}

.hero-content {
  flex: 1;
}

.hero-image {
  flex: 1;
  display: flex;
  justify-content: center;
}

.hero-image img {
  max-width: 55%;
  height: auto;
}

.title {
  font-size: 3em;
  color: #2c3e50;
  margin-bottom: 20px;
  line-height: 1.2;
}

.subtitle {
  font-size: 1.5em;
  color: #666;
  margin-bottom: 30px;
}

.hero-buttons {
  display: flex;
  gap: 20px;
}

.features-section {
  margin-bottom: 60px;
}

.section-title {
  text-align: center;
  font-size: 2em;
  color: #2c3e50;
  margin-bottom: 40px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.feature-item {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.feature-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.feature-item h3 {
  font-size: 1.3em;
  color: #2c3e50;
  margin-bottom: 10px;
}

.feature-item p {
  color: #666;
  line-height: 1.6;
}

.stats-section {
  display: flex;
  justify-content: space-around;
  padding: 60px 0;
  background: #f8f9fa;
  border-radius: 10px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2.5em;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  color: #666;
  font-size: 1.1em;
}

@media (max-width: 968px) {
  .hero-section {
    flex-direction: column;
    text-align: center;
  }

  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-buttons {
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .feature-grid {
    grid-template-columns: 1fr;
  }

  .stats-section {
    flex-direction: column;
    gap: 30px;
  }

  .title {
    font-size: 2em;
  }

  .subtitle {
    font-size: 1.2em;
  }
}

.feature-icon {
  font-size: 2.5em;
  color: #409eff;
  margin-bottom: 20px;
}
</style>
