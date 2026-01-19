import SparkMD5 from "spark-md5";

const DEFAULT_SIZE = 20 * 1024 * 1024;

// 确保 md5 函数是独立的，并且不会引起上下文问题
const md5 = (file, chunkSize = DEFAULT_SIZE) => {
  return new Promise((resolve, reject) => {
    const startMs = new Date().getTime();
    const chunks = Math.ceil(file.size / chunkSize);
    let currentChunk = 0;
    const spark = new SparkMD5.ArrayBuffer();
    const fileReader = new FileReader();

    fileReader.onload = function (e) {
      spark.append(e.target.result);
      currentChunk++;
      if (currentChunk < chunks) {
        loadNext();
      } else {
        const md5sum = spark.end();
        console.log(
          "文件md5计算结束，总耗时：",
          (new Date().getTime() - startMs) / 1000,
          "s"
        );
        resolve(md5sum);
      }
    };

    fileReader.onerror = function (error) {
      reject(error);
    };

    function loadNext() {
      console.log("当前part number：", currentChunk, "总块数：", chunks);
      const start = currentChunk * chunkSize;
      const end = Math.min(start + chunkSize, file.size);
      fileReader.readAsArrayBuffer(file.slice(start, end)); // 直接在 file 上调用 slice
    }

    loadNext();
  });
};

export default md5;

// 其他逻辑保持不变