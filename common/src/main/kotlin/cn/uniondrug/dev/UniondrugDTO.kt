package cn.uniondrug.dev

data class Result<T>(
    val errno: Int,
    val error: String,
    val dataType: String,
    val data: T,
)

data class Paging(
    val first: Int,
    val before: Int,
    val current: Int,
    val last: Int,
    val next: Int,
    val limit: Int,
    val totalPages: Int,
    val totalItems: Int,
)

data class PagingBody<T>(
    val body: List<T>,
    val paging: Paging,
)
