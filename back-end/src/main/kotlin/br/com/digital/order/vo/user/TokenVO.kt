package br.com.digital.order.vo.user

import br.com.digital.order.utils.common.TypeAccount

data class TokenVO(
    val user: String? = null,
    val authenticated: Boolean? = null,
    val created: String? = null,
    val type: TypeAccount? = null,
    val expiration: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
)
