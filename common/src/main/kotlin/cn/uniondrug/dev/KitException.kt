/** @author dingshichen */
package cn.uniondrug.dev

/**
 * 文档构建失败抛出此异常
 */
class DocBuildFailException(message: String) : RuntimeException(message)

/**
 * 发送钉钉消息失败抛出次异常
 */
class PostDingTalkFailException(message: String): RuntimeException(message)