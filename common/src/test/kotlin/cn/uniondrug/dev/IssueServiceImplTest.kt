package cn.uniondrug.dev

import org.junit.Test

class IssueServiceImplTest {

    private val issueService: IssueService = IssueServiceImpl()

    @Test
    fun postIssue() {
        val issue = Issue("ding.shichen", "能不能整点有用的")
        issueService.postIssue(issue)
        println("发送完成")
    }

}