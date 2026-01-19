package net.xdclass.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.config.AccountConfig;
import net.xdclass.controller.req.*;
import net.xdclass.dto.*;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.ShareDayTypeEnum;
import net.xdclass.enums.ShareStatusEnum;
import net.xdclass.enums.ShareTypeEnum;
import net.xdclass.exception.BizException;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.mapper.AccountFileMapper;
import net.xdclass.mapper.AccountMapper;
import net.xdclass.mapper.ShareFileMapper;
import net.xdclass.mapper.ShareMapper;
import net.xdclass.model.AccountDO;
import net.xdclass.model.AccountFileDO;
import net.xdclass.model.ShareDO;
import net.xdclass.model.ShareFileDO;
import net.xdclass.service.AccountFileService;
import net.xdclass.service.ShareService;
import net.xdclass.util.JwtUtil;
import net.xdclass.util.SpringBeanUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Service
@Slf4j
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private ShareFileMapper shareFileMapper;

    @Autowired
    private AccountFileService fileService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountFileMapper accountFileMapper;


    @Override
    public List<ShareDTO> listShare() {
        AccountDTO accountDTO = LoginInterceptor.threadLocal.get();

        List<ShareDO> shareDOList = shareMapper.selectList(new QueryWrapper<ShareDO>()
                .eq("account_id", accountDTO.getId()).orderByDesc("gmt_create"));

        return SpringBeanUtil.copyProperties(shareDOList, ShareDTO.class);
    }

    /**
     * * 检查分享文件的权限
     * * 生成分享链接和持久化数据库
     * * 生成分享详情和持久化数据库
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareDTO createShare(ShareCreateReq req) {

        //1、检查分享文件的权限
        List<Long> fileIds = req.getFileIds();
        fileService.checkFileIdLegal(fileIds, req.getAccountId());

        //2、生成分享链接和持久化数据库
        Integer dayType = req.getShareDayType();
        Integer shareDays = ShareDayTypeEnum.getDaysByType(dayType);
        Long shareId = IdUtil.getSnowflakeNextId();
        //生成分享链接
        String shareUrl = AccountConfig.PAN_FRONT_DOMAIN_SHARE_API + shareId;
        log.info("shareUrl:{}",shareUrl);

        ShareDO shareDO = ShareDO.builder()
                .id(shareId)
                .shareName(req.getShareName())
                .shareType(ShareTypeEnum.valueOf(req.getShareType()).name())
                .shareDayType(dayType)
                .shareDay(shareDays)
                .shareUrl(shareUrl)
                .shareStatus(ShareStatusEnum.USED.name())
                .accountId(req.getAccountId()).build();

        if(ShareDayTypeEnum.PERMANENT.getDayType().equals(dayType)){
            shareDO.setShareEndTime(Date.from(LocalDate.of(9999,12,31)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }else {
            shareDO.setShareEndTime(new Date(System.currentTimeMillis() + shareDays * 24 * 3600 * 1000L));
        }
        if(ShareTypeEnum.NEED_CODE.name().equalsIgnoreCase(req.getShareType())){
            //生成提取码 6位
            String shareCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();
            shareDO.setShareCode(shareCode);
        }
        shareMapper.insert(shareDO);

        //3、生成分享详情和持久化数据库
        List<ShareFileDO> shareFileDOS = new ArrayList<>();
        fileIds.forEach(fileId -> {
            ShareFileDO shareFileDO = ShareFileDO.builder()
                    .shareId(shareId)
                    .accountFileId(fileId)
                    .accountId(req.getAccountId())
                    .build();
            shareFileDOS.add(shareFileDO);
        });
        shareFileMapper.insertBatch(shareFileDOS);
        return SpringBeanUtil.copyProperties(shareDO, ShareDTO.class);
    }

    @Override
    public void cancelShare(ShareCancelReq req) {

        List<ShareDO> shareDOList = shareMapper.selectList(new QueryWrapper<ShareDO>()
                .eq("account_id", req.getAccountId()).in("id", req.getShareIds()));

        if(shareDOList.size() != req.getShareIds().size()){
            log.error("cancelShare,shareIds:{}",req.getShareIds());
            throw new BizException(BizCodeEnum.SHARE_CANCEL_ILLEGAL);
        }
        // 删除分享链接
        shareMapper.deleteBatchIds(req.getShareIds());

        //删除分享详情
        shareFileMapper.delete(new QueryWrapper<ShareFileDO>().in("share_id", req.getShareIds()));

    }

    /**
     * * 检查分享状态
     * * 查询分享记录实体
     * * 查询分享者信息
     * * 判断是否需要生成校验码,不需要的话可以直接生成分享token
     * @param shareId
     * @return
     */
    @Override
    public ShareSimpleDTO simpleDetail(Long shareId) {

        // 检查分享状态
        ShareDO shareDO = checkShareStatus(shareId);

        ShareSimpleDTO shareSimpleDTO = SpringBeanUtil.copyProperties(shareDO, ShareSimpleDTO.class);

        //查询分享者信息
        ShareAccountDTO shareAccountDTO = getShareAccount(shareDO.getAccountId());

        shareSimpleDTO.setShareAccountDTO(shareAccountDTO);

        //判断是否需要校验码
        if(ShareTypeEnum.NO_CODE.name().equalsIgnoreCase(shareDO.getShareType())){
            //直接生成分享token
            String shareToken = JwtUtil.geneShareJWT(shareDO.getId());
            shareSimpleDTO.setShareToken(shareToken);
        }
        return shareSimpleDTO;
    }

    @Override
    public String checkShareCode(ShareCheckReq req) {

        ShareDO shareDO = shareMapper.selectOne(new QueryWrapper<ShareDO>()
                .eq("id", req.getShareId()).eq("share_code", req.getShareCode())
                .eq("share_status", ShareStatusEnum.USED.name()));
        if(shareDO != null){
            //判断是否过期
            if(shareDO.getShareEndTime().getTime() > System.currentTimeMillis()){
                //生成分享token
                return JwtUtil.geneShareJWT(shareDO.getId());
            }else {
                log.error("分享链接已失效:{}",req.getShareId());
                throw new BizException(BizCodeEnum.SHARE_EXPIRED);
            }
        }
        return null;
    }

    /**
     * 分享详情接口
     * * 查询分享记录实体
     * * 检查分享状态
     * * 查询分享文件信息
     * * 查询分享者信息
     * * 构造分析详情对象返回
     * @param shareId
     * @return
     */
    @Override
    public ShareDetailDTO detail(Long shareId) {
        //查询分享记录实体
        ShareDO shareDO = checkShareStatus(shareId);
        ShareDetailDTO shareDetailDTO = SpringBeanUtil.copyProperties(shareDO, ShareDetailDTO.class);

        //查询分享文件信息
        List<AccountFileDO> accountFileDOList = getShareFileInfo(shareId);
        List<AccountFileDTO> accountFileDTOList = SpringBeanUtil.copyProperties(accountFileDOList, AccountFileDTO.class);
        shareDetailDTO.setFileDTOList(accountFileDTOList);

        //查询分享者信息
        ShareAccountDTO shareAccountDTO = getShareAccount(shareDO.getAccountId());
        shareDetailDTO.setShareAccountDTO(shareAccountDTO);
        return shareDetailDTO;
    }

    /**
     * * 检查分享链接状态
     * * 查询分享ID是否在分享的文件列表中（需要获取分享文件列表的全部文件夹和子文件夹）
     * * 分组后获取某个文件夹下面所有的子文件夹
     * * 根据父文件夹ID获取子文件夹列表
     * @param req
     * @return
     */
    @Override
    public List<AccountFileDTO> listShareFile(ShareFileQueryReq req) {
        //检查分享链接状态
        ShareDO shareDO = checkShareStatus(req.getShareId());

        //查询分享ID是否在分享的文件列表中（需要获取分享文件列表的全部文件夹和子文件夹）
        List<AccountFileDO> accountFileDOList =  checkShareFileIdOnStatus(shareDO.getId(), List.of(req.getParentId()));

        List<AccountFileDTO> accountFileDTOList = SpringBeanUtil.copyProperties(accountFileDOList, AccountFileDTO.class);

        //分组后获取某个文件夹下面所有的子文件夹
        Map<Long, List<AccountFileDTO>> fileListMap = accountFileDTOList.stream()
                .collect(Collectors.groupingBy(AccountFileDTO::getParentId));

        //根据父文件夹ID获取子文件夹列表
        List<AccountFileDTO> childFileList = fileListMap.get(req.getParentId());

        if(CollectionUtils.isEmpty(childFileList)){
            return List.of();
        }

        return childFileList;
    }


    /**
     * * 分享链接是否状态准确
     * * 转存的文件是否是分享链接里面的文件
     * * 目标文件夹是否是当前用户的
     * * 获取转存的文件
     * * 保存需要转存的文件列表（递归子文件）
     * * 同步更新所有文件的accountId为当前用户的id
     * * 计算存储空间大小，检查是否足够
     * * 更新关联对象信息，存储文件映射关系
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferShareFile(ShareFileTransferReq req) {

        //分享链接是否状态准确
        checkShareStatus(req.getShareId());

        //转存的文件是否是分享链接里面的文件
        checkInShareFiles(req.getFileIds(),req.getShareId());

        //目标文件夹是否是当前用户的
        AccountFileDO currentAccountDO = accountFileMapper.selectOne(new QueryWrapper<AccountFileDO>()
                .eq("id", req.getParentId()).eq("account_id", req.getAccountId()));
        if(currentAccountDO == null){
            log.error("目标文件夹不是当前用户的,{}",req);
            throw new BizException(BizCodeEnum.FILE_NOT_EXISTS);
        }

        //获取转存的文件
        List<AccountFileDO> shareFileList = accountFileMapper.selectBatchIds(req.getFileIds());
        //保存需要转存的文件列表（递归子文件）
        List<AccountFileDO> batchTransferFileList = fileService.findBatchCopyFileWithRecur(shareFileList, req.getParentId());

        //同步更新所有文件的accountId为当前用户的id
        batchTransferFileList.forEach(file -> {
            file.setAccountId(req.getAccountId());
        });

        //计算存储空间大小，检查是否足够
        if(!fileService.checkAndUpdateCapacity(req.getAccountId(),batchTransferFileList.stream()
                        .map(accountFileDO -> accountFileDO.getFileSize() == null ? 0 : accountFileDO.getFileSize())
                .mapToLong(Long::valueOf).sum())){
            throw new BizException(BizCodeEnum.FILE_STORAGE_NOT_ENOUGH);
        }
        //更新关联对象信息，存储文件映射关系
        accountFileMapper.insertFileBatch(batchTransferFileList);


    }

    private void checkInShareFiles(List<Long> fileIds, Long shareId) {

        //获取分享链接的文件
        List<ShareFileDO> shareFileDOS = shareFileMapper.selectList(new QueryWrapper<ShareFileDO>().eq("share_id", shareId));
        List<Long> shareFileIds = shareFileDOS.stream().map(ShareFileDO::getAccountFileId).toList();
        //找文件实体
        List<AccountFileDO> shareAccountFileDOList = accountFileMapper.selectBatchIds(shareFileIds);
        //递归找分享链接里面的所有子文件
        List<AccountFileDO> allShareFiles = new ArrayList<>();
        fileService.findAllAccountFileDOWithRecur(allShareFiles, shareAccountFileDOList, false);
        //提取全部文件的ID
        List<Long> allShareFileIds = allShareFiles.stream().map(AccountFileDO::getId).toList();

        //判断要转存的文件是否在里面
        for (Long fileId : fileIds) {
            if(!allShareFileIds.contains(fileId)){
                log.error("文件不在分享链接里面，fileId:{}",fileId);
                throw new BizException(BizCodeEnum.SHARE_FILE_ILLEGAL);
            }
        }

    }

    /**
     * 返回分享的文件列表，包括子文件
     * @param shareId
     * @param fileIdList
     * @return
     */
    private List<AccountFileDO> checkShareFileIdOnStatus(Long shareId, List<Long> fileIdList) {
        //需要获取分享文件列表的全部文件夹和子文件内容
        List<AccountFileDO>  shareFileInfoList = getShareFileInfo(shareId);
        List<AccountFileDO> allAccountFileDOList = new ArrayList<>();
        //获取全部文件，递归
        fileService.findAllAccountFileDOWithRecur(allAccountFileDOList, shareFileInfoList, false);

        if(CollectionUtils.isEmpty(allAccountFileDOList)){
            return List.of();
        }

        //把分享的对象文件的全部文件夹放到集合里面，判断目标文件集合是否都在里面
        Set<Long> allFileIdSet = allAccountFileDOList.stream().map(AccountFileDO::getId).collect(Collectors.toSet());
        if(!allFileIdSet.containsAll(fileIdList)){
            log.error("目标文件ID列表 不再 分享的文件列表中,{}",fileIdList);
            throw new BizException(BizCodeEnum.SHARE_FILE_ILLEGAL);
        }
        return allAccountFileDOList;
    }

    private List<AccountFileDO> getShareFileInfo(Long shareId) {

        //找分享文件列表
        List<ShareFileDO> shareFileDOS = shareFileMapper.selectList(new QueryWrapper<ShareFileDO>().select("account_file_id")
                .eq("share_id", shareId));
        List<Long> shareFileIdList = shareFileDOS.stream().map(ShareFileDO::getAccountFileId).toList();

        //查找文件对象
        return accountFileMapper.selectBatchIds(shareFileIdList);
    }

    /**
     * 获取分享者信息
     * @param accountId
     * @return
     */
    private ShareAccountDTO getShareAccount(Long accountId) {
        if(accountId != null){
            AccountDO accountDO = accountMapper.selectById(accountId);
            if(accountDO != null){
                return SpringBeanUtil.copyProperties(accountDO, ShareAccountDTO.class);
            }
        }
        return null;
    }

    /**
     * 检查分享状态
     * @param shareId
     * @return
     */
    private ShareDO checkShareStatus(Long shareId) {
        ShareDO shareDO = shareMapper.selectById(shareId);

        if(shareDO == null){
            log.error("分享链接不存在:{}",shareId);
            throw new BizException(BizCodeEnum.SHARE_NOT_EXIST);
        }
        //暂时未用，直接物理删除，可以调整
        if(ShareStatusEnum.CANCELED.name().equalsIgnoreCase(shareDO.getShareStatus())){
            log.error("分享链接已取消:{}",shareId);
            throw new BizException(BizCodeEnum.SHARE_CANCELED);
        }
        //判断分享是否过期
        if(shareDO.getShareEndTime().before(new Date())){
            log.error("分享链接已过期:{}",shareId);
            throw new BizException(BizCodeEnum.SHARE_EXPIRED);
        }
        return  shareDO;
    }
}