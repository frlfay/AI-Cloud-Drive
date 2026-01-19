package net.xdclass.service;

import net.xdclass.controller.req.RecycleDelReq;
import net.xdclass.controller.req.RecycleRestoreReq;
import net.xdclass.dto.AccountFileDTO;

import java.util.List;

public interface RecycleService {
    List<AccountFileDTO> listRecycleFiles(Long accountId);

    void delete(RecycleDelReq req);

    void restore(RecycleRestoreReq req);
}
