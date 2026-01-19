package net.xdclass.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分享链接的日期类型
 */
@Getter
@AllArgsConstructor
public enum ShareDayTypeEnum {

    PERMANENT(0,0),

    SEVEN_DAYS(1,7),

    THIRTY_DAYS(2,30);


    private Integer dayType;

    private Integer days;


    /**
     * 根据类型获取对应的天数
     */
    public static Integer getDaysByType(Integer dayType){
        for (ShareDayTypeEnum value : ShareDayTypeEnum.values()) {
            if(value.getDayType().equals(dayType)){
                return value.getDays();
            }
        }
        //默认是7天有效
        return SEVEN_DAYS.days;
    }


}
