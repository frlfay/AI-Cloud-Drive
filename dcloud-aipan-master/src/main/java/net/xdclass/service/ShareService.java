package net.xdclass.service;

import net.xdclass.controller.req.*;
import net.xdclass.dto.AccountFileDTO;
import net.xdclass.dto.ShareDTO;
import net.xdclass.dto.ShareDetailDTO;
import net.xdclass.dto.ShareSimpleDTO;

import java.util.List;

public interface ShareService {

    List<ShareDTO> listShare();

    ShareDTO createShare(ShareCreateReq req);

    void cancelShare(ShareCancelReq req);

    ShareSimpleDTO simpleDetail(Long shareId);

    String checkShareCode(ShareCheckReq req);

    ShareDetailDTO detail(Long shareId);

    List<AccountFileDTO> listShareFile(ShareFileQueryReq req);

    void transferShareFile(ShareFileTransferReq req);
}
