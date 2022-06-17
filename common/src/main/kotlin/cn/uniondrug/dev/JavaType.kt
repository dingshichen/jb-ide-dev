/** @author dingshichen */
package cn.uniondrug.dev

// Java 标准注解
const val DEPRECATED = "java.lang.Deprecated"
const val NOT_EMPTY = "javax.validation.constraints.NotEmpty"
const val NOT_NULL = "javax.validation.constraints.NotNull"
const val NOT_BLANK = "javax.validation.constraints.NotBlank"

// 必填
val REQUIRED = listOf(NOT_BLANK, NOT_EMPTY, NOT_NULL)

// Java 基础类型
const val BYTE = "java.lang.Byte"
const val S_BYTE = "byte"
const val SHORT = "java.lang.Short"
const val S_SHORT = "short"
const val INT = "java.lang.Integer"
const val S_INT = "int"
const val LONG = "java.lang.Long"
const val S_LONG = "long"
const val FLOAT = "java.lang.Float"
const val S_FLOAT = "float"
const val DOUBLE = "java.lang.Double"
const val S_DOUBLE = "double"
const val CHAR = "java.lang.Character"
const val S_CHAR = "char"
const val BOOLEAN = "java.lang.Boolean"
const val S_BOOLEAN = "boolean"
const val STRING = "java.lang.String"
const val BIG_DECIMAL = "java.math.BigDecimal"
const val DATE = "java.util.Date"
const val LOCAL_DATE = "java.time.LocalDate"
const val LOCAL_DATE_TIME = "java.time.LocalDateTime"

val BASE = arrayOf(
    BYTE, SHORT, INT, LONG, FLOAT, CHAR, BOOLEAN, STRING, BIG_DECIMAL, DATE, LOCAL_DATE, LOCAL_DATE_TIME,
    S_BYTE, S_SHORT, S_INT, S_LONG, S_FLOAT, S_CHAR, S_BOOLEAN, DOUBLE, S_DOUBLE
)

val BASE_LIST = listOf(
    "List<String>",
    "List<Byte>",
    "List<Short>",
    "List<Integer>",
    "List<Long>",
    "List<Float>",
    "List<Double>",
    "List<Char>",
    "List<Boolean>"
)

val FULL_BASE_LIST = arrayOf(
    "java.util.List<$BYTE>",
    "java.util.List<$SHORT>",
    "java.util.List<$INT>",
    "java.util.List<$LONG>",
    "java.util.List<$FLOAT>",
    "java.util.List<$CHAR>",
    "java.util.List<$BIG_DECIMAL>",
    "java.util.List<$BOOLEAN>",
    "java.util.List<$STRING>",
    "java.util.List<$DATE>",
    "java.util.List<$LOCAL_DATE>",
    "java.util.List<$LOCAL_DATE_TIME>",
)

// 药联框架通用类型
const val UNIONDRUG_PACKAGE = "cn.uniondrug"

const val RESULT = "cn.uniondrug.cloud.common.dto.res.Result"
const val RESULT_ERRNO = "errno"
const val RESULT_ERROR = "error"
const val RESULT_DATATYPE = "dataType"
const val RESULT_DATA = "data"

const val PAGING_BODY = "cn.uniondrug.cloud.common.dto.res.PagingBody"
const val PAGING_BODY_BODY = "body"
const val PAGING_BODY_PAGING = "paging"

const val PAGING = "cn.uniondrug.cloud.common.dto.res.Paging"
const val PAGING_FIRST = "first"
const val PAGING_BEFORE = "before"
const val PAGING_CURRENT = "current"
const val PAGING_LAST = "last"
const val PAGING_NEXT = "next"
const val PAGING_LIMIT = "limit"
const val PAGING_TOTALPAGES = "totalPages"
const val PAGING_TOTALITEMS = "totalItems"

const val PAGE_QUERY_COMMAND = "cn.uniondrug.cloud.common.dto.cmd.PageQueryCommand"
const val PAGE_QUERY_COMMAND_PAGE_NO = "pageNo"
const val PAGE_QUERY_COMMAND_PAGE_SIZE = "pageSize"
const val PAGE_QUERY_COMMAND_ORDER_DESCES = "orderDesces"

const val ORDER_DESC = "cn.uniondrug.cloud.common.dto.cmd.OrderDesc"
const val ORDER_DESC_FIELD = "field"
const val ORDER_DESC_ASC = "asc"

const val PAGING_QUERY_COMMAND = "cn.uniondrug.cloud.finance.dto.PagingQueryCommand"
const val PAGING_QUERY_COMMAND_page = "page"
const val PAGING_QUERY_COMMAND_limit = "limit"

const val OPERATOR = "cn.uniondrug.cloud.finance.dto.Operator"
const val MEMBER_ID = "memberId"
const val MEMBER_NAME = "memberName"

const val OPERATOR_DTO = "cn.uniondrug.cloud.finance.dto.OperatorDTO"
const val WORKER_ID = "workerId"
const val WORKER_NAME = "workerName"