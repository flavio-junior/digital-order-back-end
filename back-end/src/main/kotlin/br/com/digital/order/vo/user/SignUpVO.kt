package br.com.digital.order.vo.user

import br.com.digital.order.entities.employee.Employee
import br.com.digital.order.utils.common.TypeAccount

data class SignUpVO(
    var name: String,
    var surname: String,
    val email: String,
    val password: String,
    var type: TypeAccount? = null,
    var employee: Employee? = null
)
