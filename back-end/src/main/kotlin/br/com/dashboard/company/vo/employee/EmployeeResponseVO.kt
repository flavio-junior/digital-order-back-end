package br.com.dashboard.company.vo.employee

import br.com.dashboard.company.utils.common.Function
import br.com.dashboard.company.utils.common.StatusEmployee

data class EmployeeResponseVO(
    var id: Long = 0,
    var createdAt: String? = "",
    var name: String = "",
    var function: Function? = null,
    var status: StatusEmployee? = null
)
