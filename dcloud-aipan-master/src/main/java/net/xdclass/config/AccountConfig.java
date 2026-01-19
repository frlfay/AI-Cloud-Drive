package net.xdclass.config;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
public class AccountConfig {

    /**
     * 账号密码加密的盐
     */
    public static final String ACCOUNT_SALT = "xdclass.net168";

    /**
     * 默认存储空间大小  100MB
     */
    public static final Long DEFAULT_STORAGE_SIZE = 1024 * 1024  * 100L;

    /**
     * 根文件夹名称
     */
    public static final String ROOT_FOLDER_NAME = "全部文件夹";

    /**
     * 根文件夹的父ID
     */
    public static final Long ROOT_PARENT_ID = 0L;


    /**
     * 网盘前端地址
     */
    public static final String PAN_FRONT_DOMAIN_SHARE_API = "127.0.0.1:9999/share/";

}