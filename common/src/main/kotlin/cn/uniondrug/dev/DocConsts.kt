/** @author dingshichen */
package cn.uniondrug.dev

// 生成文件模版
const val API_DOC_TEMPLATE = "**\${item.name}**\n\n**URL:** `\${item.url}`\n\n**Type:** `\${item.httpMethod}`\n\n**Author:** \${item.author}\n\n**Content-Type:** `\${item.contentType}`\n\n**Description:** \${item.description}\n\n**Body-parameters:**\n\n\${detail.requestBody}\n\n**Request-example:**\n```json\n\${detail.requestExample}\n```\n**Response-fields:**\n\n\${detail.responseBody}\n\n**Response-example:**\n```json\n\${detail.responseExample}\n```\n\n"

const val MBS_DOC_TEMPLATE = "**\${item.name}**\n\n**MBS:** `\${item.mbs}`\n\n**Topic:** `\${item.topic}`\n\n**Tag:** `\${item.tag}`\n\n**Author:** \${item.author}\n\n**Description:** \${item.description}\n\n**Message-parameters:**\n\n\${detail.messageBody}\n\n**Message-example:**\n```json\n\${detail.messageExample}\n```"