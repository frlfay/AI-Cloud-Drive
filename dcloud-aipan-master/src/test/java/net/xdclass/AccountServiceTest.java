package net.xdclass;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.controller.req.AccountLoginReq;
import net.xdclass.controller.req.AccountRegisterReq;
import net.xdclass.dto.AccountDTO;
import net.xdclass.service.AccountService;
import net.xdclass.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@SpringBootTest
@Slf4j
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;


    /**
     * 注册方法测试
     */

    @Test
    public void testRegister(){
        AccountRegisterReq registerReq = AccountRegisterReq.builder().phone("123").password("123").username("老王").avatarUrl("xdclass.net").build();
        accountService.register(registerReq);
    }

    /**
     * 登录方法测试
     */
    @Test
    public void testLogin(){
        AccountLoginReq loginReq = AccountLoginReq.builder().phone("123").password("123").build();
        AccountDTO accountDTO = accountService.login(loginReq);
        String loginToken = JwtUtil.geneLoginJWT(accountDTO);
        log.info("loginToken:{}",loginToken);
    }

    /**
     * 测试账号详情方法
     */
    @Test
    public void testDetail(){
        AccountDTO accountDTO = accountService.queryDetail(1872489419534000129L);
        log.info("accountDTO:{}",accountDTO);
    }

}