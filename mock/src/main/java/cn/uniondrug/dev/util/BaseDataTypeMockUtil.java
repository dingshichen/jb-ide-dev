package cn.uniondrug.dev.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.uniondrug.dev.consts.JmInsureFiledConstants;

import java.util.*;

/**
 * 基本数据类型 mock 数据生成
 */
public class BaseDataTypeMockUtil {

//    private static Faker faker = new Faker(new Locale(System.getProperty("uniondrug-dev-kit")));
    //private static Faker faker = new Faker(new Locale("smart-doc_language"));
//    private static Faker enFaker = new Faker(new Locale("en-US"));

    private static String CLASS_PATTERN = "^([A-Za-z]{1}[A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$";

    private static Map<String, String> fieldValue = new LinkedHashMap<>();

    static {
        fieldValue.put("uuid-string", UUID.randomUUID().toString());
        fieldValue.put("uid", UUID.randomUUID().toString());
        fieldValue.put("id-string", String.valueOf(RandomUtil.randomInt(1, 200)));
//        fieldValue.put("nickname-string", enFaker.name().username());
//        fieldValue.put("hostname-string", faker.internet().ipV4Address());
//        fieldValue.put("name-string", faker.name().username());
//        fieldValue.put("author-string", faker.book().author());
//        fieldValue.put("url-string", faker.internet().url());
//        fieldValue.put("username-string", faker.name().username());
        fieldValue.put("page-int", "1");
        fieldValue.put("page-integer", "1");
        fieldValue.put("age-int", String.valueOf(RandomUtil.randomInt(0, 70)));
        fieldValue.put("age-integer", String.valueOf(RandomUtil.randomInt(0, 70)));
//        fieldValue.put("email-string", faker.internet().emailAddress());
//        fieldValue.put("domain-string", faker.internet().domainName());
//        fieldValue.put("phone-string", faker.phoneNumber().cellPhone());
//        fieldValue.put("mobile-string", faker.phoneNumber().cellPhone());
//        fieldValue.put("telephone-string", faker.phoneNumber().phoneNumber());
//        fieldValue.put("address-string", faker.address().fullAddress().replace(",", "，"));
//        fieldValue.put("ip-string", faker.internet().ipV4Address());
//        fieldValue.put("ipv4-string", faker.internet().ipV4Address());
//        fieldValue.put("ipv6-string", faker.internet().ipV6Address());
//        fieldValue.put("company-string", faker.company().name());
        fieldValue.put("timestamp-long", String.valueOf(System.currentTimeMillis()));
//        fieldValue.put("timestamp-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("time-long", String.valueOf(System.currentTimeMillis()));
//        fieldValue.put("time-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_SECOND));
//        fieldValue.put("birthday-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("birthday-long", String.valueOf(System.currentTimeMillis()));
        fieldValue.put("code-string", String.valueOf(RandomUtil.randomInt(100, 99999)));
        fieldValue.put("message-string", "success,fail".split(",")[RandomUtil.randomInt(0, 1)]);
//        fieldValue.put("date-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
//        fieldValue.put("date-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
//        fieldValue.put("begintime-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
//        fieldValue.put("endtime-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
//        fieldValue.put("time-localtime", DateTimeUtil.long2Str(System.currentTimeMillis(), DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("state-int", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("state-integer", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-int", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-integer", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-boolean", "true");
        fieldValue.put("flag-Boolean", "false");
//        fieldValue.put("idcard-string", IDCardUtil.getIdCard());
        fieldValue.put("sex-int", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("sex-integer", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("gender-int", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("gender-integer", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("limit-int", "10");
        fieldValue.put("limit-integer", "10");
        fieldValue.put("size-int", "10");
        fieldValue.put("size-integer", "10");
        fieldValue.put("offset-int", "1");
        fieldValue.put("offset-integer", "1");
        fieldValue.put("offset-long", "1");
//        fieldValue.put("version-string", enFaker.app().version());
        /* ***************************** 投保理赔字段 ***************************/
        fieldValue.put("approvalno-string", JmInsureMockUtils.mockApprovalNo());
        fieldValue.put("approvenumber-string", JmInsureMockUtils.mockApprovalNo());
        fieldValue.put("batchno-string", JmInsureMockUtils.mockBatchNo());
        fieldValue.put("billno-string", JmInsureMockUtils.mockBillNo());
        fieldValue.put("codeno-string", RandomUtil.randomNumbers(13));
        fieldValue.put("insureno-string", JmInsureMockUtils.mockInsureNo());
        fieldValue.put("invoiceno-string", JmInsureMockUtils.mockInvoiceNo());
        fieldValue.put("ipackageno-string", JmInsureMockUtils.mockIpackageNo());
        fieldValue.put("memberno-string", JmInsureMockUtils.mockMemberNo());
        fieldValue.put("orderno-string", JmInsureMockUtils.mockOrderNo());
        fieldValue.put("outBizno-string", IdUtil.fastSimpleUUID());
        fieldValue.put("paymentcode-string", JmInsureMockUtils.mockPaymentCode());
        fieldValue.put("paymentno-string", JmInsureMockUtils.mockPaymentCode());
        fieldValue.put("payno-string", JmInsureMockUtils.mockPayNo());
        fieldValue.put("policyno-string", JmInsureMockUtils.mockPolciyNo());
        fieldValue.put("recipientcode-string", JmInsureMockUtils.mockRecipientCode());
        fieldValue.put("registno-string", JmInsureMockUtils.mockRegisterNo());
        fieldValue.put("reportno-string", JmInsureMockUtils.mockRegisterNo());
        fieldValue.put("schemeno-string", JmInsureMockUtils.mockSchemeNo());
        fieldValue.put("sql-string", "select * from table_name;");
        fieldValue.put("statementno-string", JmInsureMockUtils.mockStatementNo());
        /* ***************************** 投保理赔字段 ***************************/

    }

    /**
     * Generate a random value based on java type name.
     *
     * @param typeName field type name
     * @return random value
     */
    public static String jsonValueByType(String typeName) {
        String type = typeName.contains(".") ? typeName.substring(typeName.lastIndexOf(".") + 1) : typeName;
        String value = ApiRandomUtil.randomValueByType(type);
        if (javaPrimaryType(type)) {
            return value;
        } else if ("Void".equals(type)) {
            return "null";
        } else {
//            return "\"" + value + "\"";
            return value;
        }
    }


    /**
     * Generate random field values based on field field names and type.
     *
     * @param typeName  field type name
     * @param filedName field name
     * @return random value
     */
    public static String getValByTypeAndFieldName(String typeName, String filedName) {
        boolean isArray = true;
        String type = typeName.contains("java.lang") ? typeName.substring(typeName.lastIndexOf(".") + 1, typeName.length()) : typeName;
        String key = filedName.toLowerCase() + "-" + type.toLowerCase();
        StringBuilder value = null;
        if (!type.contains("[")) {
            isArray = false;
        }
        for (Map.Entry<String, String> entry : fieldValue.entrySet()) {
            if (key.contains(entry.getKey())) {
                value = new StringBuilder(entry.getValue());
                if (isArray) {
                    for (int i = 0; i < 2; i++) {
                        value.append(",").append(entry.getValue());
                    }
                }
                break;
            }
        }

        if (filedName.toLowerCase().endsWith("time") || JmInsureFiledConstants.TIME_FILED_LIST.contains(filedName)) {
            value = new StringBuilder(DateUtil.now());
        } else if (filedName.toLowerCase().endsWith("date") || JmInsureFiledConstants.DATE_FILED_LIST.contains(filedName)) {
            value = new StringBuilder(DateUtil.today());
        } else if (filedName.toLowerCase().endsWith("phone")) {
//            value = new StringBuilder(faker.phoneNumber().cellPhone());
        } else if (filedName.toLowerCase().endsWith("idcard")) {
//            value = new StringBuilder(IDCardUtil.getIdCard());
        }

        if (Objects.isNull(value)) {
            return jsonValueByType(typeName);
        } else {
            if (javaPrimaryType(type)) {
                return value.toString();
            } else {
//                return handleJsonStr(value.toString());
                return value.toString();
            }
        }
    }

    public static String handleJsonStr(String content) {
        return "\"" + content + "\"";
    }


    public static boolean javaPrimaryType(String type) {
        switch (type) {
            case "Integer":
            case "int":
            case "Long":
            case "long":
            case "Double":
            case "double":
            case "Float":
            case "Number":
            case "float":
            case "Boolean":
            case "boolean":
            case "Short":
            case "short":
            case "BigDecimal":
            case "BigInteger":
            case "Byte":
                return true;
            default:
                return false;
        }
    }


}
