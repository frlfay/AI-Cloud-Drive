package net.xdclass.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FolderFlagEnum {

    /**
     * 非文件夹
     */
    NO(0),

    /**
     *  是文件夹
     */
    YES(1);

    private final Integer code;

}
