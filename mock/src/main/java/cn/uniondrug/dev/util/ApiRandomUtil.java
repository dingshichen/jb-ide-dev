package cn.uniondrug.dev.util;

import cn.hutool.core.util.RandomUtil;

/**
 * @author zangjie
 * @date 2021/7/2 10:19 上午
 * @describe
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 */
public class ApiRandomUtil extends RandomUtil {

    /**
     * TODO 根据java类型随机生成值
     * @param type
     * @return
     */
    public static String randomValueByType(String type) {
        if (type.equals("BigDecimal")) {
//            return String.valueOf(randomDouble("0.00));
        } else {
//            return RandomUtil.randomValueByType(type);
        }
        return "";
    }

}
