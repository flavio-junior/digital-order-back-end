package br.com.dashboard.company.vo.employee

import br.com.dashboard.company.utils.common.Function

data class RegisterEmployeeRequestVO(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val function: Function? = null
)
