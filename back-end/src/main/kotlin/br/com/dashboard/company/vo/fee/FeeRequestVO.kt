package br.com.dashboard.company.vo.fee

import br.com.dashboard.company.utils.common.Function

data class FeeRequestVO(
    var price: Double = 0.0,
    var assigned: Function? = null
)
