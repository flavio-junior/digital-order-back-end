package br.com.digital.order.vo.employee

import br.com.digital.order.utils.common.Function

data class RegisterEmployeeRequestVO(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val function: Function? = null
)
