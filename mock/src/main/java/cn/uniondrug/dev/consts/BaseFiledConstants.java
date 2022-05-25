package cn.uniondrug.dev.consts;

import java.util.List;

/**
 * 基础字段常量
 * @author zangjie
 * @date 2022/5/25 19:27
 * @copyright (C), 2011-2031, 南京云联数科科技有限公司
 */
public interface BaseFiledConstants {

    List<String> TYPES_LIST = List.of("channel", "source", "type", "method", "status", "step");

    List<String> YES_NO_LIST = List.of("directPay", "onlineInvoicing", "deleted");

}
