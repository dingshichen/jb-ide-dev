package cn.uniondrug.dev.util;

/**
 * @author zangjie
 * @date 2022/5/18 14:18
 * @copyright (C), 2011-2031, 南京云联数科科技有限公司
 */
public class StringUtil extends org.apache.commons.lang3.StringUtils {

    /**
     * Remove single or double quotes in query keywords to avoid sql errors
     *
     * @param value String
     * @return String
     */
    public static Object removeQuotes(Object value) {
        if (value instanceof String){
            String str = value.toString();
            if (isNotEmpty(str)) {
                return str.replaceAll("'", "").replaceAll("\"", "");
            } else {
                return "";
            }
        }
        return value;
    }

}
