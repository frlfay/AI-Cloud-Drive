//package net.xdclass.controller;
//
//
//import jakarta.validation.constraints.Min;
//import net.xdclass.config.MinioConfig;
//import net.xdclass.util.CommonUtil;
//import net.xdclass.util.JsonData;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//
///**
// * 小滴课堂,愿景：让技术不再难学
// *
// * @Description
// * @Author 二当家小D
// * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
// * @Version 1.0
// **/
//@RestController
//@RequestMapping("/api/test/v1")
//public class TestController {
//
//
//    @Autowired
//    private MinioConfig minioConfig;
//
////    @Autowired
////    private MinioClient minioClient;
//
//    @PostMapping("upload")
//    public JsonData testUpload(@RequestParam("file") MultipartFile file){
//
//        String filename = CommonUtil.getFilePath(file.getOriginalFilename());
//
//        //读取文件流，上传到minio
//        try {
//            InputStream inputStream = file.getInputStream();
//
////            minioClient.putObject(PutObjectArgs.builder().bucket(minioConfig.getBucketName())
////                    .object(filename)
////                    .stream(inputStream, inputStream.available(), -1)
////                    .build());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String url = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + filename;
//        return JsonData.buildSuccess(url);
//    }
//
//}