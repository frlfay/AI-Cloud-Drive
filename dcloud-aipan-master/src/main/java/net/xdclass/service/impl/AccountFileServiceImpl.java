package net.xdclass.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.component.StoreEngine;
import net.xdclass.config.MinioConfig;
import net.xdclass.controller.req.*;
import net.xdclass.dto.AccountDTO;
import net.xdclass.dto.AccountFileDTO;
import net.xdclass.dto.FileChunkDTO;
import net.xdclass.dto.FolderTreeNodeDTO;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.FileTypeEnum;
import net.xdclass.enums.FolderFlagEnum;
import net.xdclass.exception.BizException;
import net.xdclass.mapper.AccountFileMapper;
import net.xdclass.mapper.FileMapper;
import net.xdclass.mapper.StorageMapper;
import net.xdclass.model.AccountFileDO;
import net.xdclass.model.FileDO;
import net.xdclass.model.StorageDO;
import net.xdclass.service.AccountFileService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.SpringBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
@Slf4j
@Service
public class AccountFileServiceImpl implements AccountFileService {

    @Autowired
    private AccountFileMapper accountFileMapper;


    @Autowired
    private StoreEngine fileStoreEngine;

    @Autowired
    private MinioConfig minioConfig;


    @Autowired
    private FileMapper fileMapper;


    @Autowired
    private StorageMapper storageMapper;

    /**
     * 获取文件列表接口
     * @param accountId
     * @param parentId
     * @return
     */
    @Override
    public List<AccountFileDTO> listFile(Long accountId, Long parentId) {

        List<AccountFileDO> accountFileDOList = accountFileMapper.selectList(new QueryWrapper<AccountFileDO>()
                .eq("account_id", accountId).eq("parent_id", parentId)
                .orderByDesc("is_dir")
                .orderByDesc("gmt_create")
        );

        return SpringBeanUtil.copyProperties(accountFileDOList, AccountFileDTO.class);
    }

    /**
     * 创建文件夹
     * @param req
     */
    @Override
    public Long createFolder(FolderCreateReq req) {

        AccountFileDTO accountFileDTO = AccountFileDTO.builder().accountId(req.getAccountId())
                .parentId(req.getParentId())
                .fileName(req.getFolderName())
                .isDir(FolderFlagEnum.YES.getCode())
                .build();

        return saveAccountFile(accountFileDTO);

    }

    /**
     * 重命名文件
     * 1、检查ID是否存在
     * 2、新旧文件名称不能一样
     * 3、同层文件名称不能一样
     * @param req
     */
    @Override
    public void renameFile(FileUpdateReq req) {
        //检查ID是否存在
        AccountFileDO accountFileDO = accountFileMapper.selectOne(new QueryWrapper<AccountFileDO>()
                .eq("id", req.getFileId()).eq("account_id", req.getAccountId()));

        if(accountFileDO == null){
            log.error("文件不存在,{}",req);
            throw new BizException(BizCodeEnum.FILE_NOT_EXISTS);
        }else {
            //新旧文件名称不能一样
            if(Objects.equals(accountFileDO.getFileName(), req.getNewFilename())){
                log.error("文件名称重复,{}",req);
                throw new BizException(BizCodeEnum.FILE_RENAME_REPEAT);
            }
            //同层文件名称不能一样
            Long selectCount = accountFileMapper.selectCount(new QueryWrapper<AccountFileDO>()
                    .eq("account_id", req.getAccountId())
                    .eq("parent_id", accountFileDO.getParentId())
                    .eq("file_name", req.getNewFilename()));
            if(selectCount>0){
                log.error("文件名称重复,{}",req);
                throw new BizException(BizCodeEnum.FILE_RENAME_REPEAT);
            }else {
                accountFileDO.setFileName(req.getNewFilename());
                accountFileMapper.updateById(accountFileDO);
            }
        }

    }

    /**
     * 查询文件树接口 （非递归方式）
     * 1、查询用户全部文件夹
     * 2、拼装文件树
     * @param accountId
     * @return
     */
    @Override
    public List<FolderTreeNodeDTO> folderTree(Long accountId) {
        //查询用户全部文件夹
        List<AccountFileDO> folderList = accountFileMapper.selectList(new QueryWrapper<AccountFileDO>()
                .eq("account_id", accountId)
                .eq("is_dir", FolderFlagEnum.YES.getCode())
        );

        if(CollectionUtils.isEmpty(folderList)){
            return List.of();
        }
        //构建一个map， key是文件ID，value是文件对象 相当于一个数据源
        Map<Long, FolderTreeNodeDTO> folderMap = folderList.stream()
                .collect(Collectors.toMap(AccountFileDO::getId, accountFileDO ->
                FolderTreeNodeDTO.builder()
                        .id(accountFileDO.getId())
                        .parentId(accountFileDO.getParentId())
                        .label(accountFileDO.getFileName())
                        .children(new ArrayList<>())
                        .build()
        ));

        //构建文件树，遍历数据源，为每个文件夹找到子文件夹
        for (FolderTreeNodeDTO node : folderMap.values()) {
            Long parentId = node.getParentId();

            if(parentId!=null && folderMap.containsKey(parentId)){
                //获取父文件
                FolderTreeNodeDTO parentNode = folderMap.get(parentId);
                //获取父文件夹的子节点位置
                List<FolderTreeNodeDTO> children = parentNode.getChildren();
                //将当前节点添加到对应的文件夹里面
                children.add(node);
            }

        }

        //过滤根节点，即parentID是0的
        List<FolderTreeNodeDTO> rootFolderList = folderMap.values().stream()
                .filter(node -> Objects.equals(node.getParentId(), 0L))
                .collect(Collectors.toList());


        return rootFolderList;

    }

    /**
     * 查询文件树接口 （非递归方式）
     * 1、查询用户全部文件夹
     * 2、拼装文件树
     * @param accountId
     * @return
     */
    @Override
    public List<FolderTreeNodeDTO> folderTreeV2(Long accountId) {
        //查询用户全部文件夹
        List<AccountFileDO> folderList = accountFileMapper.selectList(new QueryWrapper<AccountFileDO>()
                .eq("account_id", accountId)
                .eq("is_dir", FolderFlagEnum.YES.getCode())
        );

        if(CollectionUtils.isEmpty(folderList)){
            return List.of();
        }

        List<FolderTreeNodeDTO> folderTreeNodeDTOList = folderList.stream().map(file -> {
            return FolderTreeNodeDTO.builder()
                    .id(file.getId())
                    .parentId(file.getParentId())
                    .label(file.getFileName())
                    .children(new ArrayList<>())
                    .build();
        }).toList();

        //根据父文件ID进行分组，key是当前文件夹ID，value是对应的子文件夹列表，也是数据源
        Map<Long, List<FolderTreeNodeDTO>> folderMap = folderTreeNodeDTOList
                .stream().collect(Collectors.groupingBy(FolderTreeNodeDTO::getParentId));


        //处理拼装文件树
        for(FolderTreeNodeDTO node : folderTreeNodeDTOList){
            List<FolderTreeNodeDTO> children = folderMap.get(node.getId());
            //判断是否为空
            if(!CollectionUtils.isEmpty(children)){
                node.getChildren().addAll(children);
            }
        }

        //过滤根节点，即parentID是0的
        List<FolderTreeNodeDTO> folderTreeNodeDTOS = folderTreeNodeDTOList.stream().filter(node -> Objects.equals(node.getParentId(), 0L))
                .collect(Collectors.toList());

        return folderTreeNodeDTOS;

    }

    /**
     * 文件上传
     * 1、上传到存储引擎
     * 2、保存文件关系
     * 3、保存账号和文件的关系
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fileUpload(FileUploadReq req) {

        boolean storageEnough = checkAndUpdateCapacity(req.getAccountId(),req.getFileSize());
        if(storageEnough){
            //上传到存储引擎
            String storeFileObjectKey = storeFile(req);

            //保存文件关系 + 保存账号和文件的关系
            saveFileAndAccountFile( req,storeFileObjectKey);
        }else {
            throw new BizException(BizCodeEnum.FILE_STORAGE_NOT_ENOUGH);
        }


    }

    /**
     * 检查存储空间和更新存储空间
     * @param accountId
     * @param fileSize
     * @return
     */
    public boolean checkAndUpdateCapacity(Long accountId, Long fileSize) {
        StorageDO storageDO = storageMapper.selectOne(new QueryWrapper<StorageDO>().eq("account_id", accountId));
        Long totalSize = storageDO.getTotalSize();
        if(storageDO.getUsedSize() + fileSize <= totalSize){
            storageDO.setUsedSize(storageDO.getUsedSize() + fileSize);
            storageMapper.updateById(storageDO);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 批量移动文件
     * 1、检查被移动的文件ID是否合法
     * 2、检查目标文件夹ID是否合法
     * 3、批量移动文件到目标文件夹（重复名称处理）
     *
     * @param req
     */
    @Override
    public void moveBatch(FileBatchReq req) {

        // 检查被移动的文件ID是否合法
        List<AccountFileDO> accountFileDOList =  checkFileIdLegal(req.getFileIds(),req.getAccountId());
        //检查目标文件夹ID是否合法,包括子文件夹
        checkTargetParentIdLegal(req);

        //批量转移文件到目标文件夹
        accountFileDOList.forEach(accountFileDO -> accountFileDO.setParentId(req.getTargetParentId()));

        //批量移动文件到目标文件夹（重复名称处理）
//        accountFileDOList.forEach(this::processFileNameDuplicate);
//
//        //更新文件或者文件夹的parent_id为目标文件夹的ID
//        for(AccountFileDO accountFileDO : accountFileDOList){
//            if(accountFileMapper.updateById(accountFileDO) < 0){
//                throw  new BizException(BizCodeEnum.FILE_BATCH_UPDATE_ERROR);
//            }
//        }
        accountFileDOList.forEach(accountFileDO -> {
            Long selectCount = processFileNameDuplicate(accountFileDO);
            if(selectCount > 0){
                accountFileMapper.updateById(accountFileDO);
            }
        });
    }


    /**
     * 文件的批量删除
     * 步骤一：检查是否满足：1、文件ID数量是否合法，2、文件是否属于当前用户
     * 步骤二：判断文件是否是文件夹，文件夹的话需要递归获取里面子文件ID，然后进行批量删除
     * 步骤三：需要更新账号存储空间使用情况
     * 步骤四：批量删除账号映射文件，考虑回收站如何设计
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBatch(FileDelReq req) {
        //步骤一：检查是否满足：1、文件ID数量是否合法，2、文件是否属于当前用户
        List<AccountFileDO> accountFileDOList = checkFileIdLegal(req.getFileIds(), req.getAccountId());

        //步骤二：判断文件是否是文件夹，文件夹的话需要递归获取里面子文件ID，然后进行批量删除
        List<AccountFileDO> storeAccountFileDOList = new ArrayList<>();
        findAllAccountFileDOWithRecur(storeAccountFileDOList, accountFileDOList, false);

        //拿到全部文件ID列表
        List<Long> allFileIdList = storeAccountFileDOList.stream().map(AccountFileDO::getId).collect(Collectors.toList());

        //步骤三：需要更新账号存储空间使用情况 可以加个分布式锁，redission 作业，提示可以用account_id锁粒度
        long allFileSize = storeAccountFileDOList.stream()
                .filter(file -> file.getIsDir().equals(FolderFlagEnum.NO.getCode()))
                .mapToLong(AccountFileDO::getFileSize).sum();
        StorageDO storageDO = storageMapper.selectOne(new QueryWrapper<StorageDO>().eq("account_id", req.getAccountId()));
        storageDO.setUsedSize(storageDO.getUsedSize() - allFileSize);
        storageMapper.updateById(storageDO);

        // 步骤四：批量删除账号映射文件，考虑回收站如何设计
        accountFileMapper.deleteBatchIds(allFileIdList);

    }

    /**
     * * 检查被转移的文件ID是否合法
     * * 检查目标文件夹ID是否合法
     * * 执行拷贝，递归查找【差异点，ID是全新的】
     * * 计算存储空间大小，检查是否足够【差异点，空间需要检查】
     * * 存储相关记录
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyBatch(FileBatchReq req) {

        //检查被转移的文件ID是否合法
        List<AccountFileDO> accountFileDOList = checkFileIdLegal(req.getFileIds(), req.getAccountId());

        //检查目标文件夹ID是否合法
        checkTargetParentIdLegal(req);

        //执行拷贝，递归查找【差异点，ID是全新的】
        List<AccountFileDO> newAccountFileDOList = findBatchCopyFileWithRecur(accountFileDOList, req.getTargetParentId());

        //计算存储空间大小，检查是否足够【差异点，空间需要检查】
        long totalFileSize = newAccountFileDOList.stream().filter(file -> file.getIsDir().equals(FolderFlagEnum.NO.getCode()))
                .mapToLong(AccountFileDO::getFileSize).sum();
        if(!checkAndUpdateCapacity(req.getAccountId(),totalFileSize)){
            throw new BizException(BizCodeEnum.FILE_STORAGE_NOT_ENOUGH);
        }
        //存储
        accountFileMapper.insertFileBatch(newAccountFileDOList);


    }

    /**
     * 文件秒传
     * 1、检查文件是否存在
     * 2、检查空间是否足够
     * 3、建立关系
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean secondUpload(FileSecondUploadReq req) {
        //检查文件是否存在
        FileDO fileDO = fileMapper.selectOne(new QueryWrapper<FileDO>().eq("identifier", req.getIdentifier()));
        //检查空间是否足够
        if(fileDO!=null && checkAndUpdateCapacity(req.getAccountId(),fileDO.getFileSize())){
            //处理文件秒传
            AccountFileDTO accountFileDTO = new AccountFileDTO();
            accountFileDTO.setAccountId(req.getAccountId());
            accountFileDTO.setFileId(fileDO.getId());
            accountFileDTO.setParentId(req.getParentId());
            accountFileDTO.setFileName(req.getFilename());
            accountFileDTO.setFileSize(fileDO.getFileSize());
            accountFileDTO.setDel(false);
            accountFileDTO.setIsDir(FolderFlagEnum.NO.getCode());

            //保存关联文件关系，里面有做相关检查
            saveAccountFile(accountFileDTO);
            return true;
        }
        return false;
    }


    /**
     * 包括递归处理，生成新的ID
     * @param accountFileDOList
     * @param targetParentId
     * @return
     */
    public List<AccountFileDO> findBatchCopyFileWithRecur(List<AccountFileDO> accountFileDOList, Long targetParentId) {
        List<AccountFileDO> newAccountFileDOList = new ArrayList<>();

        accountFileDOList.forEach(accountFileDO -> doCopyChildRecord(newAccountFileDOList,accountFileDO,targetParentId));

        return newAccountFileDOList;
    }

    /**
     * 递归处理，包括子文件夹
     * @param newAccountFileDOList
     * @param accountFileDO
     * @param targetParentId
     */
    private void doCopyChildRecord(List<AccountFileDO> newAccountFileDOList, AccountFileDO accountFileDO, Long targetParentId) {
        //保存旧的ID，方便查找子文件夹
        Long oldAccountFileId = accountFileDO.getId();
        //创建新记录
        accountFileDO.setId(IdUtil.getSnowflakeNextId());
        accountFileDO.setParentId(targetParentId);
        accountFileDO.setGmtModified(null);
        accountFileDO.setGmtCreate(null);

        //处理重复文件夹
        processFileNameDuplicate(accountFileDO);

        //纳入容器存储
        newAccountFileDOList.add(accountFileDO);

        //判断是文件还是文件夹，递归处理
        if(Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
            //继续获取子文件夹列表
            List<AccountFileDO> childAccountFileDOList = findChildAccountFile(accountFileDO.getAccountId(),oldAccountFileId);
            if(CollectionUtils.isEmpty(childAccountFileDOList)){
                return;
            }
            //递归处理
            childAccountFileDOList
                    .forEach(childAccountFileDO -> doCopyChildRecord(newAccountFileDOList,childAccountFileDO,accountFileDO.getId()));
        }






    }

    /**
     * 查找文件记录，只查询下一级，不递归
     * @param accountId
     * @param parentId
     * @return
     */
    private List<AccountFileDO> findChildAccountFile(Long accountId, Long parentId) {
        return accountFileMapper.selectList(new QueryWrapper<AccountFileDO>()
                .eq("account_id", accountId).eq("parent_id", parentId));
    }

    /**
     * 检查目标文件夹ID是否合法,包括子文件夹
     * 1、目标的文件ID不能是文件
     * 2、要操作的文件列表不能包括目标文件ID
     * @param req
     */
    private void checkTargetParentIdLegal(FileBatchReq req) {
        //目标的文件ID不能是文件
        AccountFileDO targetAccountFileDO = accountFileMapper.selectOne(new QueryWrapper<AccountFileDO>()
                .eq("id", req.getTargetParentId())
                .eq("is_dir", FolderFlagEnum.YES.getCode())
                .eq("account_id", req.getAccountId()));
        if(targetAccountFileDO == null){
            log.error("目标文件ID不是文件，需要是文件夹，targetParentId={}", req.getTargetParentId());
            throw new BizException(BizCodeEnum.FILE_TARGET_PARENT_ILLEGAL);
        }

        /**
         * 要操作的文件列表不能包括目标文件ID
         * 思路
         * 1、查询批量操作的文件夹和文件夹，递归处理
         * 2、判断是否在里面
         */
        List<AccountFileDO> prepareAccountFileDOList = accountFileMapper.selectList(new QueryWrapper<AccountFileDO>()
                .in("id", req.getFileIds())
                .eq("account_id", req.getAccountId()));

        //定义一个容器，存储全部文件夹，包括子文件夹
        List<AccountFileDO> allAccountFileDOList = new ArrayList<>();
        //递归查找全部子文件夹
        findAllAccountFileDOWithRecur(allAccountFileDOList,prepareAccountFileDOList,false);

        //判断是否在里面
        if(allAccountFileDOList.stream().anyMatch(accountFileDO -> Objects.equals(accountFileDO.getId(), req.getTargetParentId()))){
            log.error("目标文件ID不能是文件，需要是文件夹，targetParentId={}", req.getTargetParentId());
            throw new BizException(BizCodeEnum.FILE_TARGET_PARENT_ILLEGAL);
        }

    }

    /**
     * 递归查找
     * @param allAccountFileDOList 容器存储查询到到全部文件或者文件夹
     * @param prepareAccountFileDOList 待查询的文件和文件夹
     * @param onlyFolder 控制是否只存储文件
     */
    public void findAllAccountFileDOWithRecur(List<AccountFileDO> allAccountFileDOList, List<AccountFileDO> prepareAccountFileDOList, boolean onlyFolder) {

        for(AccountFileDO accountFileDO : prepareAccountFileDOList){
            if(Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
                //递归查找
                List<AccountFileDO> childAccountFileDOList = accountFileMapper.selectList(new QueryWrapper<AccountFileDO>()
                        .eq("parent_id", accountFileDO.getId()));
                findAllAccountFileDOWithRecur(allAccountFileDOList,childAccountFileDOList,onlyFolder);
            }

            //如果通过onlyFolder是true,只存储文件夹到allAccountFileDOList，否则都存储到allAccountFileDOList
            if(!onlyFolder || Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
                allAccountFileDOList.add(accountFileDO);
            }
        }


    }

    /**
     * 检查被移动的文件ID是否合法
     * @param fileIds
     * @param accountId
     * @return
     */
    public List<AccountFileDO> checkFileIdLegal(List<Long> fileIds, Long accountId) {

        List<AccountFileDO> accountFileDOList = accountFileMapper
                .selectList(new QueryWrapper<AccountFileDO>().in("id", fileIds).eq("account_id", accountId));

        if(accountFileDOList.size()!=fileIds.size()){
            log.error("文件ID数量不合法,ids={}", fileIds);
            throw new BizException(BizCodeEnum.FILE_BATCH_UPDATE_ERROR);
        }
        //进一步完善的话，可以加个set，防止重复元素

        return accountFileDOList;
    }

    /**
     * 保存文件和账号文件的关系到数据库
     * @param req
     * @param storeFileObjectKey
     */
    @Override
    public void saveFileAndAccountFile(FileUploadReq req, String storeFileObjectKey) {
        //保存文件
        FileDO fileDO = saveFile(req,storeFileObjectKey);

        //保存文件账号关系
        AccountFileDTO accountFileDTO = AccountFileDTO.builder()
                .accountId(req.getAccountId())
                .parentId(req.getParentId())
                .fileId(fileDO.getId())
                .fileName(req.getFilename())
                .isDir(FolderFlagEnum.NO.getCode())
                .fileSuffix(fileDO.getFileSuffix())
                .fileSize(req.getFileSize())
                .fileType(FileTypeEnum.fromExtension(fileDO.getFileSuffix()).name())
                .build();
        saveAccountFile(accountFileDTO);

    }

    private FileDO saveFile(FileUploadReq req, String storeFileObjectKey) {
        FileDO fileDO = new FileDO();
        fileDO.setAccountId(req.getAccountId());
        fileDO.setFileName(req.getFilename());
        fileDO.setFileSize(req.getFile() !=null ? req.getFile().getSize():req.getFileSize());
        fileDO.setFileSuffix(CommonUtil.getFileSuffix(req.getFilename()));
        fileDO.setObjectKey(storeFileObjectKey);
        fileDO.setIdentifier(req.getIdentifier());
        fileMapper.insert(fileDO);
        return fileDO;
    }

    /**
     * 上传文件到存储引擎，返回存储的文件路径
     * @param req
     * @return
     */
    private String storeFile(FileUploadReq req) {

        String objectKey = CommonUtil.getFilePath(req.getFilename());
        fileStoreEngine.upload(minioConfig.getBucketName(), objectKey, req.getFile());
        return objectKey;
    }

    /**
     * 处理用户和文件的关系，存储文件和文件夹都是可以的
     *
     * 1、检查父文件是否存在
     * 2、检查文件是否重复
     * 3、保存相关文件关系
     *
     * @param accountFileDTO
     * @return
     */
    private Long saveAccountFile(AccountFileDTO accountFileDTO) {
        //检查父文件是否存在
        checkParentFileId(accountFileDTO);

        AccountFileDO accountFileDO = SpringBeanUtil.copyProperties(accountFileDTO, AccountFileDO.class);

        //检查文件是否重复 aa  aa(1) aa(2)
        processFileNameDuplicate(accountFileDO);

        //保存相关文件关系
        accountFileMapper.insert(accountFileDO);

        return accountFileDO.getId();
    }

    /**
     * 处理文件是否重复,
     *  文件夹重复和文件名重复处理规则不一样
     * @param accountFileDO
     */
    public Long processFileNameDuplicate(AccountFileDO accountFileDO) {

        Long selectCount = accountFileMapper.selectCount(new QueryWrapper<AccountFileDO>()
                .eq("account_id", accountFileDO.getAccountId())
                .eq("parent_id", accountFileDO.getParentId())
                .eq("is_dir", accountFileDO.getIsDir())
                .eq("file_name", accountFileDO.getFileName()));

        if(selectCount>0){
            //处理重复文件夹
            if(Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
                accountFileDO.setFileName(accountFileDO.getFileName()+"_"+System.currentTimeMillis());
            }else {
                //处理重复文件名,提取文件拓展名
                String[] split = accountFileDO.getFileName().split("\\.");
                accountFileDO.setFileName(split[0]+"_"+System.currentTimeMillis()+"."+split[1]);
            }
        }

        return selectCount;

    }

    /**
     * 检查父文件是否存在
     * @param accountFileDTO
     */
    private void checkParentFileId(AccountFileDTO accountFileDTO) {
        if(accountFileDTO.getParentId()!=0){
            AccountFileDO accountFileDO = accountFileMapper.selectOne(
                    new QueryWrapper<AccountFileDO>()
                            .eq("id", accountFileDTO.getParentId())
                            .eq("account_id", accountFileDTO.getAccountId()));

            if(accountFileDO == null){
                throw new BizException(BizCodeEnum.FILE_NOT_EXISTS);
            }
        }

    }
}