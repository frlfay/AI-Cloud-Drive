package net.xdclass.controller;

import net.xdclass.controller.req.AccountLoginReq;
import net.xdclass.controller.req.AccountRegisterReq;
import net.xdclass.dto.AccountDTO;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.service.AccountService;
import net.xdclass.util.JsonData;
import net.xdclass.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/account/v1")
public class AccountController {


    @Autowired
    private AccountService accountService;

    /**
     * 注册接口
     */
    @PostMapping("register")
    public JsonData register(@RequestBody AccountRegisterReq req){
        accountService.register(req);
        return JsonData.buildSuccess();
    }

    /**
     * 头像上传接口
     */
    @PostMapping("upload_avatar")
    public JsonData uploadAvatar(@RequestParam("file") MultipartFile file){

        String url = accountService.uploadAvatar(file);

        return JsonData.buildSuccess(url);
    }


    /**
     * 登录模块
     */
    @PostMapping("login")
    public JsonData login(@RequestBody AccountLoginReq req){

        AccountDTO accountDTO = accountService.login(req);

        //生成token jwt ssm  一般前端存储在localStorage里面，或 sessionStorage里面
        String token = JwtUtil.geneLoginJWT(accountDTO);

        return JsonData.buildSuccess(token);
    }


    /**
     * 获取用户详情接口
     */
    @GetMapping("detail")
    public JsonData detail(){

        AccountDTO accountDTO = accountService.queryDetail(LoginInterceptor.threadLocal.get().getId());

        return JsonData.buildSuccess(accountDTO);
    }



}