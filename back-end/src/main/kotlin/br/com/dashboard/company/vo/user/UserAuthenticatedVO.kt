package br.com.dashboard.company.vo.user

data class UserAuthenticatedVO(
    var id: Long? = 0,
    var name: String? = null,
    var surname: String? = null,
    var userName: String? = null,
    var email: String? = null,
    var type: TypeAccountVO? = null,
    var telephone: Long? = 0,
)
