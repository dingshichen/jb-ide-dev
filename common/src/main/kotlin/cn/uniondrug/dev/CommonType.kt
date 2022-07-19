/** @author dingshichen */
package cn.uniondrug.dev

/**
 * 通用类型
 */
enum class CommonType(
    val value: String,
    val isBaseType: Boolean,
    val isBaseArray: Boolean,
) {
    STRING("string", true, false),
    BOOL("boolean", true, false),
    BYTE("byte", true, false),
    INT("int", true, false),
    LONG("long", true, false),
    FLOAT("float", true, false),
    ARRAY("array", false, true),
    OBJECT("object", false, false),
    ARRAY_STRING("array[string]", false, true),
    ARRAY_BOOL("array[boolean]", false, true),
    ARRAY_BYTE("array[byte]", false, true),
    ARRAY_INT("array[int]", false, true),
    ARRAY_LONG("array[long]", false, true),
    ARRAY_FLOAT("array[float]", false, true),
    ARRAY_OBJECT("array[object]", false, false),
}

/**
 * 将各语言接口解析的字段类型，转换成通用类型
 */
interface CommonTypeConvertor {

    fun convert(original: String): CommonType
}

class JavaTypeConvertor : CommonTypeConvertor {

    private val stringList = arrayOf("String", "Date", "DateTime", "LocalDate", "LocalDateTime")
    private val boolList = arrayOf("Boolean", "boolean")
    private val byteList = arrayOf("byte", "Byte")
    private val intList = arrayOf("Integer", "int", "short", "Short")
    private val longList = arrayOf("Long", "long")
    private val floatList = arrayOf("float", "Float", "Double", "double", "BigDecimal")
    private val arrayList = arrayOf("List", "ArrayList", "LinkedList", "JSONArray")

    override fun convert(original: String): CommonType = when (original) {
        in stringList -> CommonType.STRING
        in boolList -> CommonType.BOOL
        in byteList -> CommonType.BYTE
        in intList -> CommonType.INT
        in longList -> CommonType.LONG
        in floatList -> CommonType.FLOAT
        in arrayList -> CommonType.ARRAY
        else -> if (original.isJavaGenericArray()) {
            // 获取泛型对应类型
            when (convert(original.subJavaGeneric())) {
                CommonType.STRING -> CommonType.ARRAY_STRING
                CommonType.BOOL -> CommonType.ARRAY_BOOL
                CommonType.BYTE -> CommonType.ARRAY_BYTE
                CommonType.INT -> CommonType.ARRAY_INT
                CommonType.LONG -> CommonType.ARRAY_LONG
                CommonType.FLOAT -> CommonType.ARRAY_FLOAT
                else -> CommonType.ARRAY_OBJECT
            }
        } else CommonType.OBJECT
    }
}

class GoTypeConvertor : CommonTypeConvertor {

    private val byteList = arrayOf("uint8", "int8")
    private val intList = arrayOf("uint16", "uint32", "int16", "int32", "int", "uint")
    private val longList = arrayOf("uint64", "int64")
    private val floatList = arrayOf("float32", "float64")

    override fun convert(original: String) = when (original) {
        "string" -> CommonType.STRING
        "bool" -> CommonType.BOOL
        "interface{}" -> CommonType.STRING
        in byteList -> CommonType.BYTE
        in intList -> CommonType.INT
        in longList -> CommonType.LONG
        in floatList -> CommonType.FLOAT
        else -> if (original.isGoArray()) {
            when (original.subGoTypeInArray()) {
                "string" -> CommonType.ARRAY_STRING
                "bool" -> CommonType.ARRAY_BOOL
                in byteList -> CommonType.ARRAY_BYTE
                in intList -> CommonType.ARRAY_INT
                in longList -> CommonType.ARRAY_LONG
                in floatList -> CommonType.ARRAY_FLOAT
                else -> CommonType.ARRAY_OBJECT
            }
        } else CommonType.OBJECT
    }

}