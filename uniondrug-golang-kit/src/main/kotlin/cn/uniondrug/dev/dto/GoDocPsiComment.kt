/** @author dingshichen */
package cn.uniondrug.dev.dto

import cn.uniondrug.dev.DocBuildFailException
import cn.uniondrug.dev.util.getAnnotationValue
import cn.uniondrug.dev.util.getCommentValue
import cn.uniondrug.dev.util.removeSuffix
import com.intellij.psi.PsiComment
import java.util.regex.Pattern

val docAuthorPattern: Pattern = Pattern.compile("// @Author\\(.+\\)\\s*")
val docGetPattern: Pattern = Pattern.compile("// @Get\\(.+\\)\\s*")
val docPostPattern: Pattern = Pattern.compile("// @Post\\(.+\\)\\s*")
val docRequestPattern: Pattern = Pattern.compile("// @Request\\(.+\\)\\s*")
val docResponsePattern: Pattern = Pattern.compile("// @Response\\(.+\\)\\s*")
val docResponseListPattern: Pattern = Pattern.compile("// @ResponseList\\(.+\\)\\s*")
val docResponsePagingPattern: Pattern = Pattern.compile("// @ResponsePaging\\(.+\\)\\s*")
val docErrorParttern: Pattern = Pattern.compile("// @Error\\(.+\\)\\s*")

/**
 * 接口名称注释
 */
class DocNameComment(
    private val methodName: String,
    private val psiComment: PsiComment
) : PsiComment by psiComment {

    fun getName() =
        psiComment.text?.getCommentValue(methodName)?.removeSuffix() ?: throw DocBuildFailException("分析接口名称失败，请检查接口定义")
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
) : PsiComment by psiComment

/*
 * 接口作者注释
 */
class DocAuthorComment(
    private val psiComment: PsiComment
) : PsiComment by psiComment

/*
 * 接口入参注释
 */
class DocRequestComment(psiComment: PsiComment) : PsiComment by psiComment

/*
 * 接口返回值注释 数据类型为 object
 */
open class DocResponseComment(psiComment: PsiComment) : PsiComment by psiComment

/*
 * 接口返回值注释 数据类型为 list
 */
class DocResponseListComment(psiComment: PsiComment) : DocResponseComment(psiComment)

/*
 * 接口返回值注释 数据类型为 paging
 */
class DocResponsePagingComment(psiComment: PsiComment) : DocResponseComment(psiComment)

/*
 * 接口错误码注释
 */
class DocErrorComment(psiComment: PsiComment) : PsiComment by psiComment

/*
 * 接口是否忽略注释
 */
class DocIgnoreComment(psiComment: PsiComment) : PsiComment by psiComment

/*
 * 接口详情描述注释
 */
class DocDescComment(psiComment: PsiComment) : PsiComment by psiComment