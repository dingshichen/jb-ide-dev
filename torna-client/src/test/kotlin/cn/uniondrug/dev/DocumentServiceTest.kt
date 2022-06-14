package cn.uniondrug.dev

class DocumentServiceTest {

    val token = "4L2kqjXP:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYwIiwiZXhwIjoxNjU1ODI0MDQyLCJpYXQiOjE2NTUyMTkyNDJ9.p0u-BhvBCf-BaCprNKvT8rN00_fRwv6F4mmmT7DrBBw"

    val moduleId = "oNzvQRXD"

//    @Test
    fun deleteDocument() {
        val documentService = DocumentService()
        documentService.listDocumentByModule(token, moduleId).forEach {
            // 连目录一起删除了
            documentService.deleteDocument(token, it.id)
        }
    }

}