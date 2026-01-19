package net.xdclass.mapper;

import net.xdclass.model.ShareFileDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 文件分享表 Mapper 接口
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
public interface ShareFileMapper extends BaseMapper<ShareFileDO> {

    void insertBatch(List<ShareFileDO> shareFileDOS);
}
