package net.xdclass.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.controller.req.RecycleDelReq;
import net.xdclass.controller.req.RecycleRestoreReq;
import net.xdclass.dto.AccountFileDTO;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.FolderFlagEnum;
import net.xdclass.exception.BizException;
import net.xdclass.mapper.AccountFileMapper;
import net.xdclass.model.AccountFileDO;
import net.xdclass.service.AccountFileService;
import net.xdclass.service.RecycleService;
import net.xdclass.util.SpringBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class RecycleServiceImpl implements RecycleService {

    @Autowired
    private AccountFileMapper accountFileMapper;

    @Autowired
    private AccountFileService accountFileService;


    @Override
    public List<AccountFileDTO> listRecycleFiles(Long accountId) {
        List<AccountFileDO> recycleList =  accountFileMapper.selectRecycleFiles(accountId,null);

        //如果是文件夹，就只显示文件夹，不显示里面的其他子文件
        List<Long> fileIds = recycleList.stream().map(AccountFileDO::getId).toList();

        //需要提取全部删除文件的ID，然后过滤下，如果某个文件的的父ID在这个文件ID集合里面，则不显示
        List<AccountFileDO> accountFileDOS = recycleList.stream()
                .filter(accountFileDO -> !fileIds.contains(accountFileDO.getParentId()))
                .collect(Collectors.toList());

        return SpringBeanUtil.copyProperties(accountFileDOS, AccountFileDTO.class);
    }

    /**
     * 删除回收站内容
     * * 文件ID数量是否合法
     * * 判断文件是否是文件夹，文件夹的话需要递归获取里面子文件ID，然后进行批量删除
     * * 批量删除回收站文件
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(RecycleDelReq req) {

        List<AccountFileDO> records = accountFileMapper.selectRecycleFiles(req.getAccountId(), req.getFileIds());
        //文件ID数量是否合法
        if(req.getFileIds().size() != records.size()){
            throw new BizException(BizCodeEnum.FILE_DEL_BATCH_ILLEGAL);
        }

        //判断文件是否是文件夹，文件夹的话需要递归获取里面子文件ID，然后进行批量删除
        List<AccountFileDO> allRecords = new ArrayList<>();
        //需要单独写查询文件夹和子文件夹的递归方法，需要del=1
        findAllAccountFileDOWithRecur(allRecords, records, false);

        List<Long> recycleFileIds = allRecords.stream().map(AccountFileDO::getId).toList();

        //批量删除回收站文件
        accountFileMapper.deleteRecycleFiles(recycleFileIds);

    }


    /**
     * 还原回收站文件
     * * 检查是否满足：文件ID数量是否合法
     * * 还原前的父文件和当前文件夹是否有重复名称的文件和文件夹
     * * 判断文件是否是文件夹，文件夹的话需要递归获取里面子文件ID，才可以进行批量还原
     * * 检查空间是否足够
     * * 批量还原文件
     * @param req
     */
    @Override
    public void restore(RecycleRestoreReq req) {
        //检查是否满足：文件ID数量是否合法
        List<AccountFileDO> accountFileDOList = accountFileMapper.selectRecycleFiles(req.getAccountId(), req.getFileIds());
        if(req.getFileIds().size() != accountFileDOList.size()){
            throw new BizException(BizCodeEnum.FILE_RECYCLE_ILLEGAL);
        }

        //还原前的父文件和当前文件夹是否有重复名称的文件和文件夹
        accountFileDOList.forEach(accountFileDO -> {
            Long selectCount = accountFileService.processFileNameDuplicate(accountFileDO);
            if(selectCount > 0){
                 accountFileMapper.updateRecycleFileNameById(accountFileDO.getId(), accountFileDO.getFileName());
            }
        });

        //判断文件是否是文件夹，文件夹的话需要递归获取里面子文件ID，才可以进行批量还原
        List<AccountFileDO> allAccountFileDOList = new ArrayList<>();
        findAllAccountFileDOWithRecur(allAccountFileDOList, accountFileDOList, false);
        List<Long> allFileIds = allAccountFileDOList.stream().map(AccountFileDO::getId).toList();

        //检查空间是否足够
        if(!accountFileService.checkAndUpdateCapacity(req.getAccountId(),allAccountFileDOList.stream()
                .map(accountFileDO -> accountFileDO.getFileSize() == null ? 0 : accountFileDO.getFileSize()).mapToLong(Long::valueOf).sum())){
            throw new BizException(BizCodeEnum.FILE_STORAGE_NOT_ENOUGH);
        }

        //批量还原文件
        accountFileMapper.restoreFiles(allFileIds);

    }

    private void findAllAccountFileDOWithRecur(List<AccountFileDO> allRecords, List<AccountFileDO> records, boolean onlyFolder) {

        for(AccountFileDO accountFileDO : records){
            if(Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
                //递归查找 del=1
                List<AccountFileDO> childAccountFileDOList = accountFileMapper.selectRecycleChildFiles(accountFileDO.getId(),accountFileDO.getAccountId());
                findAllAccountFileDOWithRecur(allRecords,childAccountFileDOList,onlyFolder);
            }

            //如果通过onlyFolder是true,只存储文件夹到allAccountFileDOList，否则都存储到allAccountFileDOList
            if(!onlyFolder || Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
                allRecords.add(accountFileDO);
            }
        }
    }
}