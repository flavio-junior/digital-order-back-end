package br.com.digital.order.vo.user

import br.com.digital.order.utils.common.TypeAccount

data class UserResponseVO(
    var id: Long = 0,
    var name: String = "",
    var surname: String = "",
    var email: String = "",
    var type: TypeAccount? = null
)
