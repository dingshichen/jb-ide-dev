package cn.uniondrug.dev.consts;

import java.util.List;

/**
 * 投保理赔字段
 * @author zangjie
 * @date 2021/9/16 3:22 下午
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 */
public interface JmInsureFiledConstants {

    /**
     * yyyy-MM-dd 日期字段
     */
    List<String> DATE_FILED_LIST = List.of("endGmtAudit", "endGmtClaimed", "endGmtCreated", "endGmtInsure",
            "endGmtPaid", "endGmtReceipt", "gmtBlocked", "gmtClaimedEnd", "gmtClaimedStart", "gmtCommittedEnd",
            "gmtCommittedStart", "gmtDamage", "gmtIpackageCreated", "gmtPaidDate", "gmtPostEnd", "gmtPostStart",
            "gmtStatistics", "gmtSubmitEnd", "gmtSubmitStart", "startGmtClaimed", "startGmtCreated", "startGmtInsure",
            "startGmtPaid", "startGmtReceipt");

    /**
     * yyyy-MM-dd HH:mm:ss 时间字段
     */
    List<String> TIME_FILED_LIST = List.of("gmtActivate", "gmtAudit", "gmtClaimed", "gmtCommitted",
            "gmtCreated", "gmtCreatedEnd", "gmtCreatedStart", "gmtDrugExpire", "gmtPaid", "gmtPost", "gmtSend", "gmtSign",
            "gmtTrans", "gmtUpdated", "startGmtAudit");


}
