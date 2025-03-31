package br.com.digital.order.vo.fee

import br.com.digital.order.utils.common.Function

data class FeeRequestVO(
    var percentage: Int = 0,
    var assigned: Function? = null
)
