/** @author dingshichen */
package cn.uniondrug.dev.util

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethod

const val REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping"
const val POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping"
const val DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping"
const val PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping"
const val GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping"

val MVC_ANNOTATIONS = listOf(REQUEST_MAPPING, POST_MAPPING, DELETE_MAPPING, PUT_MAPPING, GET_MAPPING)

const val BODY = "org.springframework.web.bind.annotation.RequestBody"

const val LENGTH = "org.hibernate.validator.constraints.Length"

const val JSON_ALIAS = "com.fasterxml.jackson.annotation.JsonProperty"

const val EVENT = "org.springframework.context.ApplicationEvent"
const val EVENT_PUBLISHER = "org.springframework.context.ApplicationEventPublisher"
const val EVENT_LISTENER = "org.springframework.context.ApplicationListener"

const val FEIGN_CLIENT = "org.springframework.cloud.openfeign.FeignClient"

const val SPRING_REST_TEMPLATE = "org.springframework.web.client.RestTemplate"

/**
 * 判断 PSI 方法是否不是 Spring 的方法
 */
fun isNotSpringMVCMethod(psiMethod: PsiMethod) = !isSpringMVCMethod(psiMethod)

/**
 * 判断 PSI 方法是否是 SpringMVC 的方法
 */
fun isSpringMVCMethod(psiMethod: PsiMethod) = AnnotationUtil.isAnnotated(psiMethod, MVC_ANNOTATIONS, 0)

/**
 * 判断是不是 FeignClient
 */
fun isFeignClient(psiClass: PsiClass) = psiClass.isInterface && AnnotationUtil.isAnnotated(psiClass, FEIGN_CLIENT, 0)

/**
 * 获取 feignClient url
 */
fun getFeignClientUrl(psiClass: PsiClass) = AnnotationUtil.getStringAttributeValue(psiClass.modifierList?.findAnnotation("org.springframework.cloud.openfeign.FeignClient")!!, "url")

/**
 * 获取 feignClient 方法上的 path
 */
fun getFeignClientMethodPath(psiMethod: PsiMethod): String? = AnnotationUtil.findAnnotation(psiMethod, MVC_ANNOTATIONS)?.let {
    val value = AnnotationUtil.getStringAttributeValue(it, "value")
    if (value != null) {
        return value
    }
    return it.findAttributeValue("value")?.let { memberValue ->
        (memberValue.children[0] as PsiLiteralExpression).value as String
    }
}

/**
 * 获取 MVC 接口 url
 */
fun getUrl(project: Project, psiClass: PsiClass, psiMethod: PsiMethod): String {
    val classAnnotation = AnnotationUtil.findAnnotation(psiClass, REQUEST_MAPPING)
    val pathByClass: String? = classAnnotation?.let {
        AnnotationUtil.getStringAttributeValue(it, "value")
    }
    val methodAnnotation = AnnotationUtil.findAnnotation(psiMethod, MVC_ANNOTATIONS)
    methodAnnotation ?: throw RuntimeException("获取 API 路径失败")
    val pathByMethod = AnnotationUtil.getStringAttributeValue(methodAnnotation, "value")
    return pathByClass?.let {
        "/$it/$pathByMethod".replace("//", "/")
    } ?: "/$pathByMethod".replace("//", "/")
}

/**
 * 获取 HTTP Method
 */
fun getHttpMethod(psiMethod: PsiMethod): String {
    for (annotation in psiMethod.modifierList.annotations) {
        return when (annotation.qualifiedName) {
            POST_MAPPING -> "POST"
            GET_MAPPING -> "GET"
            PUT_MAPPING -> "PUT"
            DELETE_MAPPING -> "DELETE"
            REQUEST_MAPPING -> AnnotationUtil.getStringAttributeValue(annotation, "method") ?: "GET"
            else -> continue
        }
    }
    return "GET"
}

/**
 * 获取 HTTP Header ContentType
 * TODO 没有参数怎么办
 */
fun getContentType(psiMethod: PsiMethod) =
    psiMethod.parameterList.parameters.find { AnnotationUtil.isAnnotated(it, BODY, 0) }
        ?.let { "application/json" } ?: "application/x-www-form-urlencoded"

/**
 * 查询 RequestBody 参数
 */
fun getRequestBody(project: Project, psiMethod: PsiMethod) =
    psiMethod.parameterList.parameters.find {
        AnnotationUtil.isAnnotated(it, BODY, 0)
    }?.let {
        getRequestBody(project, it)
    }

/**
 * 查询 ResponseBody 参数
 */
fun getResponseBody(project: Project, psiMethod: PsiMethod) = psiMethod.returnTypeElement?.let { getResponseBody(project, it) }

/**
 * 获取属性最大长度
 */
fun getMaxLength(psiField: PsiField): String {
    val lengthAnno = AnnotationUtil.findAnnotation(psiField, LENGTH)
    return lengthAnno?.let {
        AnnotationUtil.getLongAttributeValue(lengthAnno, "max").toString()
    } ?: ""
}