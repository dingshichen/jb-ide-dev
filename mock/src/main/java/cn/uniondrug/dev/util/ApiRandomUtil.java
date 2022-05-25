package cn.uniondrug.dev.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

import java.math.RoundingMode;
import java.util.Date;

/**
 * @author zangjie
 * @date 2021/7/2 10:19 上午
 * @describe
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 */
public class ApiRandomUtil extends RandomUtil {

    private static String FORMAT = "0.00";


    /**
     * 根据java类型随机生成值
     * @param type
     * @return
     */
    public static Object randomValueByType(String type) {
        if (type.equals("BigDecimal")) {
            return randomDouble(1000000.00,2, RoundingMode.HALF_UP);
        } else {
            //基础 类型 mock
            return baseRandomValueByType(type);
        }
    }


    /**
     * 基本类型生成数据
     * @param type
     * @return
     */
    public static Object baseRandomValueByType(String type) {
        switch (type) {
            //12
            case "char":
                return randomString(1);
            case "Integer":    //4
            case "int":
            case "Long": //-5
            case "long":
            case "BigDecimal":    //3
            case "BigInteger":
                return randomInt(1000);
            case "Double": //8
            case "double":
            case "Float": //6
            case "float":
                return randomDouble(Double.parseDouble(FORMAT));
            case "short":
            case "Short":
                return randomInt(0, 32767);
            case "Byte":
            case "byte":
                return randomInt(0, 127);
            case "boolean":
            case "Boolean":
                return "true";
            case "Time":  //91
            case "Date":
                return DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMATTER);
            case "LocalDate":
//                return DateUtil.(System.currentTimeMillis(), DateTimeUtil.DATE_FORMAT_DAY);
                return "";
            case "Timestamp":  //91
            case "LocalDateTime":
//                return DateTimeUtil.long2Str(System.currentTimeMillis(), DateTimeUtil.DATE_FORMAT_SECOND);
                return "";
            case "ZonedDateTime":
//                return DateTimeUtil.zonedDateTimeToStr(ZonedDateTime.now(),DateTimeUtil.DATE_FORMAT_ZONED_DATE_TIME);
                return "";
            case "uuid":
            case "UUID":
                return IdUtil.fastSimpleUUID();
            default:
                return randomString(6);
        }
    }
}
