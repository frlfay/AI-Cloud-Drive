package net.xdclass.service;

import net.xdclass.controller.req.AccountLoginReq;
import net.xdclass.controller.req.AccountRegisterReq;
import net.xdclass.dto.AccountDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AccountService {
    void register(AccountRegisterReq req);

    String uploadAvatar(MultipartFile file);

    AccountDTO login(AccountLoginReq req);

    AccountDTO queryDetail(Long id);
}
