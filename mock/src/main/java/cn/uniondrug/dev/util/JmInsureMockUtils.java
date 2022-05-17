package cn.uniondrug.dev.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.Date;

/**
 * 投保理赔
 * @author zangjie
 * @date 2021/9/15 6:08 下午
 * @copyright (C), 2011-2031, 上海聚音信息科技有限公司
 */
public class JmInsureMockUtils {


    /**
     * mock 批准文号
     * @return
     */
    public static String mockApprovalNo() {
        return "国药准字" + RandomUtil.randomString(9);
    }


    /**
     * mock batchNo 批次号
     * @return
     */
    public static String mockBatchNo() {
        return "BN" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)
                + RandomUtil.randomNumbers(13);
    }

    /**
     * mock billNo 开票单号
     * @return
     */
    public static String mockBillNo() {
        return DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN) + RandomUtil.randomNumbers(4);
    }

    /**
     * TODO
     * mock insureNo 投单编号
     * @return
     */
    public static String mockInsureNo() {
//        return DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN).substring(2) +
//                StringUtils.leftPad(RandomUtil.randomInt(1, 100) + "", 5, "0");
        return "";
    }

    /**
     * mock invoiceNo 发票号码
     * @return
     */
    public static String mockInvoiceNo() {
        return RandomUtil.randomInt(1, 9) + RandomUtil.randomNumbers(7);
    }


    /**
     * mock ipackageNo 理赔单号
     * @return DIC(DCS)+yyyyMMdd+5位数字
     */
    public static String mockIpackageNo() {
        return "DIC(DCS)" + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN) + RandomUtil.randomNumbers(5);
    }

    /**
     * mock MemberNo 会员编号
     * @return 12位数字
     */
    public static String mockMemberNo() {
        return RandomUtil.randomInt(1, 9) + RandomUtil.randomNumbers(11);
    }

    /**
     * mock orderNo 订单号
     * @return
     */
    public static String mockOrderNo() {
        return RandomUtil.randomInt(1, 9) + RandomUtil.randomNumbers(20);
    }

    /**
     * mock paymentCode 付款单号
     * @return
     */
    public static String mockPaymentCode() {
        DateTime dateTime = new DateTime();
        return "FM" + dateTime.toString("yyyyMMdd") + RandomUtil.randomNumbers(6);
    }

    /**
     * mock 付款计划no
     * @return
     */
    public static String mockPayNo() {
        return "PAN" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) +
                RandomUtil.randomNumbers(12);
    }

    /**
     * mock policyNo 保单号
     * @return
     */
    public static String mockPolciyNo() {
        return RandomUtil.randomString(4).toUpperCase() + "yyyyMM" + RandomUtil.randomNumbers(10);
    }

    /**
     * mock recipientCode 收款单号
     * @return
     */
    public static String mockRecipientCode() {
        return "FRC" + RandomUtil.randomNumbers(19);
    }

    /**
     * mock 赔案号
     * @return
     */
    public static String mockRegisterNo() {
        return "R" + RandomUtil.randomNumbers(18);
    }

    /**
     * mock schemeNo 方案号
     * @return
     */
    public static String mockSchemeNo() {
        return "ISS" + "yyyyMMdd" + RandomUtil.randomNumbers(5);
    }

    /**
     * mock statementNo 结算单号
     * @return
     */
    public static String mockStatementNo() {
        return "DS" + "yyyyMMdd" + RandomUtil.randomNumbers(5);
    }

}
