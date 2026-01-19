import { defineStore } from "pinia";
import { ref } from "vue";

export const useFileStore = defineStore(
  "file",
  () => {
    const viewMode = ref<"table" | "grid">("table");

    function setViewMode(mode: "table" | "grid") {
      viewMode.value = mode;
    }

    return {
      viewMode,
      setViewMode,
    };
  },
  {
    persist: {
      storage: {
        getItem(key: string): string | null {
          const value = localStorage.getItem(key);
          if (value) {
            const parsed = JSON.parse(value);
            return JSON.stringify({ viewMode: parsed.viewMode });
          }
          return null;
        },
        setItem(key: string, value: string) {
          const parsed = JSON.parse(value);
          localStorage.setItem(
            key,
            JSON.stringify({ viewMode: parsed.viewMode })
          );
        },
      },
    },
  }
);
