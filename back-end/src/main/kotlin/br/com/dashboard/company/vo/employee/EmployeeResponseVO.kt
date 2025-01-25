package br.com.dashboard.company.vo.employee

import br.com.dashboard.company.utils.common.Function

data class EmployeeResponseVO(
    var id: Long = 0,
    var createdAt: String? = "",
    var name: String = "",
    var function: Function? = null,
)
