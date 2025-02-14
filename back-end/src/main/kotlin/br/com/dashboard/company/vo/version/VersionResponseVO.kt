package br.com.dashboard.company.vo.version

data class VersionResponseVO(
    var status: Boolean = false,
    var id: Long? = 0,
    var version: String? = null,
    var url: String? = null
)
