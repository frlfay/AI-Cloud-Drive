package net.xdclass.controller;

import lombok.AllArgsConstructor;
import net.xdclass.annotation.ShareCodeCheck;
import net.xdclass.aspect.ShareCodeAspect;
import net.xdclass.controller.req.*;
import net.xdclass.dto.AccountFileDTO;
import net.xdclass.dto.ShareDTO;
import net.xdclass.dto.ShareDetailDTO;
import net.xdclass.dto.ShareSimpleDTO;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.service.ShareService;
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
@RequestMapping("/api/share/v1")
public class ShareController {


    @Autowired
    private ShareService shareService;

    /**
     * 获取我的个人分享列表接口
     */
    @GetMapping("list")
    public JsonData list(){
        List<ShareDTO> list = shareService.listShare();
        return JsonData.buildSuccess(list);
    }

    /**
     * 创建分享链接
     */
    @PostMapping("create")
    public JsonData create(@RequestBody ShareCreateReq req){

        req.setAccountId(LoginInterceptor.threadLocal.get().getId());

        ShareDTO shareDTO = shareService.createShare(req);

        return JsonData.buildSuccess(shareDTO);
    }


    /**
     * 取消分享
     */
    @PostMapping("cancel")
    public JsonData cancel(@RequestBody ShareCancelReq req){

        req.setAccountId(LoginInterceptor.threadLocal.get().getId());

        shareService.cancelShare(req);

        return JsonData.buildSuccess();
    }


    /**
     * 访问分享接口,返回基本的分享信息
     * 情况一：如果链接不需要校验码，则一并返回token
     * 情况二：如果链接需要校验码，则返回校验码，调用对应接口校验码，再返回token
     */
    @GetMapping("visit")
    public JsonData visit(@RequestParam(value = "shareId") Long shareId){

        ShareSimpleDTO shareSimpleDTO = shareService.simpleDetail(shareId);
        return JsonData.buildSuccess(shareSimpleDTO);
    }


    /**
     * 校验分享码，返回临时token
     */
    @PostMapping("check_share_code")
    public JsonData checkShareCode(@RequestBody ShareCheckReq req){

        String shareToken = shareService.checkShareCode(req);
        if(shareToken == null){
            return JsonData.buildResult(BizCodeEnum.SHARE_NOT_EXIST);
        }
        return JsonData.buildSuccess(shareToken);
    }


    /**
     * 查看分享详情接口
     */
    @GetMapping("detail")
    @ShareCodeCheck
    public JsonData detail(){
        ShareDetailDTO shareDetailDTO  = shareService.detail(ShareCodeAspect.get());
        return JsonData.buildSuccess(shareDetailDTO);
    }


    /**
     * 查看某个分享文件夹下的文件列表
     */
    @PostMapping("list_share_file")
    @ShareCodeCheck
    public JsonData listShareFile(@RequestBody ShareFileQueryReq req){
        req.setShareId(ShareCodeAspect.get());
        List<AccountFileDTO> list = shareService.listShareFile(req);
        return JsonData.buildSuccess(list);
    }


    /**
     * 文件转存
     */
    @PostMapping("transfer")
    @ShareCodeCheck
    public JsonData transfer(@RequestBody ShareFileTransferReq req){

        req.setShareId(ShareCodeAspect.get());
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        shareService.transferShareFile(req);
        return JsonData.buildSuccess();
    }



}