/** @author dingshichen */
package cn.uniondrug.dev.mock

import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import cn.uniondrug.dev.CommonType
import cn.uniondrug.dev.util.JmInsureMockUtils
import java.math.BigDecimal
import java.util.*

// 规则 MAP
val ROLE_MAP = mapOf(
    "uuid-string" to UUID.randomUUID().toString(),
    "id-int" to RandomUtil.randomInt(1, 3000),
    "id-string" to RandomUtil.randomInt(1, 3000).toString(),
    "nickname-string" to "BiaoGe",
    "hostname-string" to "127.0.0.1",
    "name-string" to "范德彪",
    "author-string" to "范德彪",
    "url-string" to "http://debiao.turboradio.cn",
    "username-string" to "FanDeBiao",
    "page-int" to 1,
    "age-int" to RandomUtil.randomInt(0, 70),
    "email-string" to "fandebiao@turboradio.cn",
    "domain-string" to "debiao.turboradio.cn",
    "phone-string" to "17705507550",
    "mobile-string" to "17705507550",
    "telephone-string" to "18045705750",
    "address-string" to "江苏省南京市雨花台区金证科技园12栋1024",
    "ip-string" to "218.94.117.256",
    "ipv4-string" to "218.94.117.256",
    "ipv6-string" to "1030::C9B4:FF12:48AA:1A2B",
    "company-string" to RandomUtil.randomEle(arrayOf("南京云联数科科技有限公司", "上海聚音信息科技有限公司")),
    "timestamp-int" to System.currentTimeMillis(),
    "timestamp-string" to System.currentTimeMillis().toString(),
    "time-long" to System.currentTimeMillis(),
    "time-string" to System.currentTimeMillis().toString(),
    "birthday-string" to DateUtil.format(Date(), DatePattern.NORM_DATE_FORMATTER),
    "birthday-int" to System.currentTimeMillis(),
    "code-string" to RandomUtil.randomInt(100, 99999).toString(),
    "message-string" to RandomUtil.randomEle(arrayOf("success", "fail")),
    "date-string" to DateUtil.format(Date(), DatePattern.NORM_DATE_FORMATTER),
    "begintime-string" to DateUtil.format(Date(), DatePattern.NORM_DATE_FORMATTER),
    "endtime-string" to DateUtil.format(Date(), DatePattern.NORM_DATE_FORMATTER),
    "state-int" to RandomUtil.randomInt(0, 5),
    "flag-int" to RandomUtil.randomInt(0, 10),
    "idcard-string" to "341102199307140013",
    "sex-int" to RandomUtil.randomInt(0, 2),
    "gender-int" to RandomUtil.randomInt(0, 2),
    "limit-int" to 10,
    "size-int" to 10,
    "offset-int" to 1,
    "errno-int" to 0,
    "error-string" to "请求成功",
    "datatype-string" to "OBJECT",

/* ***************************** 投保理赔字段 ***************************/
    "approvalno-string" to JmInsureMockUtils.mockApprovalNo(),
    "approvenumber-string" to JmInsureMockUtils.mockApprovalNo(),
    "batchno-string" to JmInsureMockUtils.mockBatchNo(),
    "billno-string" to JmInsureMockUtils.mockBillNo(),
    "codeno-string" to RandomUtil.randomNumbers(13),
    "insureno-string" to JmInsureMockUtils.mockInsureNo(),
    "invoiceno-string" to JmInsureMockUtils.mockInvoiceNo(),
    "ipackageno-string" to JmInsureMockUtils.mockIpackageNo(),
    "memberno-string" to JmInsureMockUtils.mockMemberNo(),
    "orderno-string" to JmInsureMockUtils.mockOrderNo(),
    "outBizno-string" to IdUtil.fastSimpleUUID(),
    "paymentcode-string" to JmInsureMockUtils.mockPaymentCode(),
    "paymentno-string" to JmInsureMockUtils.mockPaymentCode(),
    "payno-string" to JmInsureMockUtils.mockPayNo(),
    "policyno-string" to JmInsureMockUtils.mockPolciyNo(),
    "recipientcode-string" to JmInsureMockUtils.mockRecipientCode(),
    "registno-string" to JmInsureMockUtils.mockRegisterNo(),
    "reportno-string" to JmInsureMockUtils.mockRegisterNo(),
    "schemeno-string" to JmInsureMockUtils.mockSchemeNo(),
    "sql-string" to "select * from table_name;",
    "statementno-string" to JmInsureMockUtils.mockStatementNo(),
/* ***************************** 投保理赔字段 ***************************/

)

/**
 * 生成 mock 值
 */
fun generateBaseTypeMockData(type: CommonType, fieldName: String): Any {
    return when (type) {
        CommonType.BYTE,
        CommonType.INT,
        CommonType.LONG -> ROLE_MAP["${fieldName.lowercase()}-int"] ?: 0
        CommonType.FLOAT -> RandomUtil.randomBigDecimal(BigDecimal("0.01"), BigDecimal("99.99")).toDouble()
        CommonType.BOOL -> RandomUtil.randomBoolean()
        CommonType.STRING -> ROLE_MAP["${fieldName.lowercase()}-string"] ?: "xxxxxx"
        else -> ""
    }
}