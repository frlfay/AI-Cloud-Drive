package net.xdclass.service;

import net.xdclass.controller.req.*;
import net.xdclass.dto.AccountFileDTO;
import net.xdclass.dto.FileChunkDTO;
import net.xdclass.dto.FolderTreeNodeDTO;
import net.xdclass.model.AccountFileDO;

import java.util.List;

public interface AccountFileService {
    /**
     * 获取文件列表
     * @param accountId
     * @param parentId
     * @return
     */
    List<AccountFileDTO> listFile(Long accountId, Long parentId);

    /**
     * 创建文件夹
     * @param req
     */
    Long createFolder(FolderCreateReq req);

    /**
     * 重命名文件
     * @param req
     */
    void renameFile(FileUpdateReq req);

    /**
     * 文件树接口
     *
     * @param accountId
     * @return
     */
    List<FolderTreeNodeDTO> folderTree(Long accountId);

    /**
     * 文件树接口
     *
     * @param accountId
     * @return
     */
     List<FolderTreeNodeDTO> folderTreeV2(Long accountId);

    /**
     * 普通小文件上传
     * @param req
     */
    void fileUpload(FileUploadReq req);

    /**
     * 批量移动目标文件夹
     * @param req
     */
    void moveBatch(FileBatchReq req);

    /**
     * 文件的批量删除
     * @param req
     */
    void delBatch(FileDelReq req);

    /**
     * 文件复制
     * @param req
     */
    void copyBatch(FileBatchReq req);

    /**
     * 文件秒传
     * @param req
     * @return
     */
    Boolean secondUpload(FileSecondUploadReq req);


    /**
     * 保存文件和文件关联关系
     * @param req
     * @param storeFileObjectKey
     */
     void saveFileAndAccountFile(FileUploadReq req, String storeFileObjectKey);


    /**
     * 检查文件id是否合法
     * @param fileIds
     * @param accountId
     * @return
     */
     List<AccountFileDO> checkFileIdLegal(List<Long> fileIds, Long accountId);

    /**
     * 递归查找文件
     * @param allAccountFileDOList
     * @param prepareAccountFileDOList
     * @param onlyFolder
     */
     void findAllAccountFileDOWithRecur(List<AccountFileDO> allAccountFileDOList, List<AccountFileDO> prepareAccountFileDOList, boolean onlyFolder);


    /**
     * 批量复制 或 转存
     * @param accountFileDOList
     * @param targetParentId
     * @return
     */
    List<AccountFileDO> findBatchCopyFileWithRecur(List<AccountFileDO> accountFileDOList, Long targetParentId);


    /**
     * 检查容量是否足够
     * @param accountId
     * @param fileSize
     * @return
     */
    boolean checkAndUpdateCapacity(Long accountId, Long fileSize);


    /**
     * 处理文件名重复
     * @param accountFileDO
     * @return
     */
    Long processFileNameDuplicate(AccountFileDO accountFileDO);


    }
