package br.com.digital.order.vo.employee

import br.com.digital.order.utils.common.Function
import br.com.digital.order.utils.common.StatusEmployee

data class EmployeeResponseVO(
    var id: Long = 0,
    var createdAt: String? = "",
    var name: String = "",
    var function: Function? = null,
    var status: StatusEmployee? = null
)
