import axios from 'axios';
import { ElMessage } from 'element-plus';
import JSONBIG from 'json-bigint';
import { useLoginUserStore } from "@/store/user";


axios.defaults.transformResponse = [];
// 创建自定义 Axios 实例
const myAxios = axios.create({
  // baseURL: "http://127.0.0.1:8080/api",
  // baseURL: "http://47.119.128.20:8080/api",
  baseURL: "http://pan-api.open1024.com/api",
  // baseURL: "http://192.168.137.1:8080/api",
  // baseURL: "http://192.168.3.114:8082/api",
  timeout: 10000,
  withCredentials: true,
  // 直接在创建时设置 transformResponse
  transformResponse: [function (data) {
    try {
      if (typeof data !== 'string') {

        // 如果数据不是字符串（例如，对于二进制数据），则直接返回
        return data;
      }

      // 使用 json-bigint 解析 JSON 数据，storeAsString 参数确保大整数被存储为字符串
      const jsonParser = JSONBIG({ storeAsString: true });
      console.log('Parsing data with json-bigint:', data);
      const parsedData = jsonParser.parse(data);
      console.log('Parsed data:', parsedData);

      return parsedData;

    } catch (error) {
      console.error('Error parsing response:', error);
      // 可以选择抛出错误或返回默认值
      throw error;
    }
  }]
});


// 添加请求拦截器
myAxios.interceptors.request.use(
  (config) => {
    // 在每次请求前添加token
    const { token } = useLoginUserStore();
    console.log(111)
    if (token) {
      config.headers['token'] = `${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 添加响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    console.log('Raw response:', response);
    // localStorage.setItem("time", response.data.data.createTime)

    // 检查并处理数据（如果需要）
    if (typeof response.data === 'object') {
      console.log('Parsed data in response interceptor:', response.data);
    }

    // 继续原有的逻辑...
    const { data } = response;
    if (data.code === 20004) {
      if (
        !response.request.responseURL.includes("/account/v1/detail") &&
        !window.location.pathname.includes("/account/v1/login")
      ) {
        ElMessage.warning("请先登录");
        window.location.href = `/account/v1/login?redirect=${window.location.href}`;
      }
    }

    return response;
  },
  function (error) {
    return Promise.reject(error);
  }
);

export default myAxios;
