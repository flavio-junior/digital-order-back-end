package br.com.dashboard.company.vo.user

import br.com.dashboard.company.entities.employee.Employee
import br.com.dashboard.company.utils.common.TypeAccount

data class SignUpVO(
    var name: String,
    var surname: String,
    val email: String,
    val password: String,
    var type: TypeAccount? = null,
    var employee: Employee? = null
)
