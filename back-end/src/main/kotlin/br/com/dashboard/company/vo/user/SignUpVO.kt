package br.com.dashboard.company.vo.user

import br.com.dashboard.company.utils.common.TypeAccount

data class SignUpVO(
    var name: String,
    var surname: String,
    val email: String,
    val password: String,
    var type: TypeAccount? = null
)
