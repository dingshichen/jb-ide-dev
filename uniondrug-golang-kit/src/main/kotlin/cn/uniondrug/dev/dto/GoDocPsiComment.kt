/** @author dingshichen */
package cn.uniondrug.dev.dto

import cn.uniondrug.dev.DocBuildFailException
import cn.uniondrug.dev.util.getAnnotationMultiValues
import cn.uniondrug.dev.util.getAnnotationValue
import cn.uniondrug.dev.util.getCommentValue
import com.intellij.psi.PsiComment
import java.util.regex.Pattern

val docAuthorPattern: Pattern = Pattern.compile("// @Author\\(.+\\)\\s*")
val docGetPattern: Pattern = Pattern.compile("// @Get\\(.+\\)\\s*")
val docPostPattern: Pattern = Pattern.compile("// @Post\\(.+\\)\\s*")
val docRequestPattern: Pattern = Pattern.compile("// @Request\\(.+\\)\\s*")
val docResponsePattern: Pattern = Pattern.compile("// @Response\\(.+\\)\\s*")
val docResponseListPattern: Pattern = Pattern.compile("// @ResponseList\\(.+\\)\\s*")
val docResponsePagingPattern: Pattern = Pattern.compile("// @ResponsePaging\\(.+\\)\\s*")
val docErrorPattern: Pattern = Pattern.compile("// @Error\\(.+\\)\\s*")
val docRoutePrefixPattern: Pattern = Pattern.compile("// @RoutePrefix\\(.+\\)\\s*")
val docMbsPattern: Pattern = Pattern.compile("// @Mbs\\(.+\\)\\s*")
val docMbsTopicPattern: Pattern = Pattern.compile("// @Topic\\(.+\\)\\s*")
val docMbsTagPattern: Pattern = Pattern.compile("// @Tag\\(.+\\)\\s*")

/**
 * 接口名称注释
 */
class DocNameComment(
    private val docName: String,
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getName() =
        psiComment.text?.getCommentValue(docName) ?: throw DocBuildFailException("分析接口名称失败，请检查接口定义")

    /**
     * 是否有接口请求方式前缀
     */
    fun isRestfullPrefix() = isGetPrefix() || isPostPrefix()

    fun isGetPrefix() = psiComment.text.startsWith("// Get")

    fun isPostPrefix() = psiComment.text.startsWith("// Post")
}

/**
 * 接口过期注释
 */
class DocDeprecatedComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getDeprecated() = psiComment.text.getCommentValue("Deprecated")
}

/**
 * 接口路径前缀
 */
class DocRoutePrefixComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getPath() = psiComment.text.getAnnotationValue("RoutePrefix")
}

/**
 * 接口为 GET 请求的注释
 */
class DocGetComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getPath() = psiComment.text.getAnnotationValue("Get")
}

/**
 * 接口为 POST 请求的注释
 */
class DocPostComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getPath() = psiComment.text.getAnnotationValue("Post")
}

/**
 * 接口作者注释
 */
class DocAuthorComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getAuthor() = psiComment.text.getAnnotationValue("Author")
}

/**
 * 接口入参注释
 */
open class DocRequestComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    open fun getParam() = psiComment.text.getAnnotationValue("Request")
}

/**
 * 接口返回值注释 数据类型为 object
 */
open class DocResponseComment(
    private val psiComment: PsiComment
) : DocRequestComment(psiComment) {

    override fun getParam() = psiComment.text.getAnnotationValue("Response")
}

/**
 * 接口返回值注释 数据类型为 list
 */
class DocResponseListComment(
    private val psiComment: PsiComment
) : DocResponseComment(psiComment) {

    override fun getParam() = psiComment.text.getAnnotationValue("ResponseList")
}

/**
 * 接口返回值注释 数据类型为 paging
 */
class DocResponsePagingComment(
    private val psiComment: PsiComment
) : DocResponseComment(psiComment) {

    override fun getParam() = psiComment.text.getAnnotationValue("ResponsePaging")
}

/**
 * 接口错误码注释
 */
class DocErrorComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getValues() = psiComment.text.getAnnotationMultiValues("Error")
}

/**
 * 接口是否忽略注释
 */
class DocIgnoreComment(psiComment: PsiComment) : PsiComment by psiComment

/**
 * 接口详情描述注释
 */
class DocDescComment(psiComment: PsiComment) : PsiComment by psiComment

/**
 * MBS 通道注释
 */
class DocMbsComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getMbs() = psiComment.text.getAnnotationValue("Mbs")
}

/**
 * MBS Topic 注释
 */
class DocMbsTopicComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getTopic() = psiComment.text.getAnnotationValue("Topic")
}

/**
 * MBS Tag 注释
 */
class DocMbsTagComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getTag() = psiComment.text.getAnnotationValue("Tag")
}
