/** @author dingshichen */
package cn.uniondrug.dev.mock

import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import cn.uniondrug.dev.CommonType
import cn.uniondrug.dev.util.JmInsureMockUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

val organization = arrayOf("在水一方垂钓园筹备处", "开原市德彪投资股份有限公司", "开原市维多利亚国际娱乐广场",
    "大帅打工子弟小学", "彪记靓汤餐饮有限公司", "桂英风味餐馆", "彪哥解梦馆", "小翠美容有限公司")

val name = arrayOf("德彪", "维力", "钢子", "玉芬", "富贵", "牛二", "小翠", "桂英", "德财", "阿薇")

// 规则 MAP
val ROLE_MAP = mapOf(
    "uuid-string" to UUID.randomUUID().toString(),
    "id-int" to RandomUtil.randomInt(1, 3000),
    "id-string" to RandomUtil.randomInt(1, 3000).toString(),
    "nickname-string" to RandomUtil.randomEle(name),
    "hostname-string" to "127.0.0.1",
    "name-string" to RandomUtil.randomEle(name),
    "author-string" to RandomUtil.randomEle(name),
    "url-string" to "http://debiao.turboradio.cn",
    "user-string" to RandomUtil.randomEle(name),
    "username-string" to RandomUtil.randomEle(name),
    "workername-string" to RandomUtil.randomEle(name),
    "worker-string" to RandomUtil.randomEle(name),
    "creatorname-string" to RandomUtil.randomEle(name),
    "creator-string" to RandomUtil.randomEle(name),
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
    "organizationid-int" to RandomUtil.randomInt(10000, 20000),
    "company-string" to RandomUtil.randomEle(organization),
    "companyname-string" to RandomUtil.randomEle(organization),
    "insurer-string" to RandomUtil.randomEle(organization),
    "insurername-string" to RandomUtil.randomEle(organization),
    "organizationname-string" to RandomUtil.randomEle(organization),
    "merchant-string" to RandomUtil.randomEle(organization),
    "merchantname-string" to RandomUtil.randomEle(organization),
    "partner-string" to RandomUtil.randomEle(organization),
    "partnername-string" to RandomUtil.randomEle(organization),
    "store-string" to RandomUtil.randomEle(organization),
    "storename-string" to RandomUtil.randomEle(organization),
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
 * 获取准备结果值
 */
fun getRoleValue(key: String) = ROLE_MAP[key]

/**
 * 生成 mock 值
 */
fun generateBaseTypeMockData(type: String, fieldName: String): Any {
    return when (type) {
        CommonType.BYTE.value -> getRoleValue("${fieldName.lowercase()}-int") ?: RandomUtil.randomInt(0, 16)
        CommonType.INT.value -> getRoleValue("${fieldName.lowercase()}-int") ?: RandomUtil.randomInt(128, 1024)
        CommonType.LONG.value -> getRoleValue("${fieldName.lowercase()}-int") ?: RandomUtil.randomInt(100000, 1000000)
        // Torna 不支持 float 数组展示，只能转化为字符串数组展示
        CommonType.FLOAT.value -> RandomUtil.randomBigDecimal(BigDecimal("0.01"), BigDecimal("99.99")).run {
            setScale(2, RoundingMode.HALF_DOWN)
            toString()
        }
        CommonType.BOOL.value -> RandomUtil.randomBoolean()
        CommonType.STRING.value -> getRoleValue("${fieldName.lowercase()}-string") ?: "xxxxxx"
        else -> ""
    }
}