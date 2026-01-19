package net.xdclass.controller;

import net.xdclass.controller.req.RecycleDelReq;
import net.xdclass.controller.req.RecycleRestoreReq;
import net.xdclass.dto.AccountFileDTO;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.service.RecycleService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/recycle/v1/")
public class RecycleController {

    @Autowired
    private RecycleService recycleService;

    /**
     * 获取回收站列表
     */
    @GetMapping("list")
    public JsonData list(){
        Long accountId = LoginInterceptor.threadLocal.get().getId();

        List<AccountFileDTO> list = recycleService.listRecycleFiles(accountId);

        return JsonData.buildSuccess(list);
    }


    /**
     * 彻底删除回收站文件
     */
    @PostMapping("delete")
    public JsonData delete(@RequestBody RecycleDelReq req){

        req.setAccountId(LoginInterceptor.threadLocal.get().getId());

        recycleService.delete(req);

        return JsonData.buildSuccess();
    }


    /**
     * 还原回收站文件
     */
    @PostMapping("restore")
    public JsonData restore(@RequestBody RecycleRestoreReq req){

        req.setAccountId(LoginInterceptor.threadLocal.get().getId());

        recycleService.restore(req);

        return JsonData.buildSuccess();
    }



}