package net.xdclass.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Slf4j
public class CommonUtil {

    /**
     * 响应json数据给前端
     *
     * @param response
     * @param obj
     */
    public static void sendJsonMessage(HttpServletResponse response, Object obj) {

        response.setContentType("application/json; charset=utf-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.print(JsonUtil.obj2Json(obj));
            response.flushBuffer();

        } catch (IOException e) {
            log.warn("响应json数据给前端异常:{}", e);
        }
    }

    /**
     * 根据文件名称获取文件后缀
     */
    public static String getFileSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }


    /**
     * 根据文件后缀，生成文件存储：年/月/日/uuid.suffix 格式
     */
    public static String getFilePath(String fileName) {
        String suffix = getFileSuffix(fileName);
        // 生成文件在存储桶中的唯一键
        return StrUtil.format("{}/{}/{}/{}.{}", DateUtil.thisYear(), DateUtil.thisMonth() + 1,DateUtil.thisDayOfMonth(), IdUtil.randomUUID(), suffix);
    }
}