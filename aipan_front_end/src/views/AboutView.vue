<template>
  <div class="about">
    <div class="about-container">
      <div class="about-card">
        <div class="decorative-bar"></div>
        <div class="content">
          <MdViewer :value="aboutContent" />
        </div>
      </div>
      <div class="copyright">
        © {{ new Date().getFullYear() }} DPan. All rights reserved.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import MdViewer from "@/components/common/MdViewer.vue";
import { aboutContent } from "@/assets/about";
</script>

<style scoped>
.about {
  min-height: calc(100vh - 64px);
  background-color: #ffffff;
  padding: 0px;
}

.about-container {
  max-width: 64rem;
  margin: 0 auto;
  padding: 1rem 1rem 2rem;
}

.about-card {
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.02);
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.decorative-bar {
  height: 0.25rem;
  background: linear-gradient(to right, #4f46e5, #06b6d4);
}

.content {
  padding: 2rem 3rem;
}

.copyright {
  text-align: center;
  color: #64748b;
  font-size: 0.875rem;
  margin-top: 1.5rem;
}

:deep(.markdown-body) {
  color: #334155;
}

:deep(.markdown-body h1) {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 1.75rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

:deep(.markdown-body h2) {
  font-size: 1.5rem;
  font-weight: 600;
  color: #334155;
  margin-top: 2.5rem;
  margin-bottom: 1.25rem;
}

:deep(.markdown-body ul) {
  margin: 1.25rem 0;
  padding-left: 1.75rem;
  list-style-type: disc;
}

:deep(.markdown-body li) {
  color: #475569;
  line-height: 1.75;
  margin-bottom: 0.75rem;
}

:deep(.markdown-body p) {
  line-height: 1.75;
  margin-bottom: 1.25rem;
  color: #475569;
}

:deep(.markdown-body a) {
  color: #4f46e5;
  text-decoration: none;
  transition: color 0.2s;
}

:deep(.markdown-body a:hover) {
  color: #4338ca;
}

@media (max-width: 768px) {
  .about-container {
    padding: 0.5rem 1rem 1.5rem;
  }

  .content {
    padding: 1.5rem;
  }

  :deep(.markdown-body h1) {
    font-size: 1.5rem;
    margin-bottom: 1.25rem;
  }

  :deep(.markdown-body h2) {
    font-size: 1.25rem;
    margin-top: 2rem;
  }

  :deep(.markdown-body) {
    font-size: 0.9375rem;
  }
}
</style>
