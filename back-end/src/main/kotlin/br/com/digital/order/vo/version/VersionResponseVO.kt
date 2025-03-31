package br.com.digital.order.vo.version

data class VersionResponseVO(
    var status: Boolean = false,
    var id: Long? = 0,
    var version: String? = null,
    var url: String? = null
)
