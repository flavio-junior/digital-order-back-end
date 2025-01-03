package br.com.dashboard.company.vo.checkout

import br.com.dashboard.company.utils.common.TypeOrder

data class CheckoutResponseVO(
    var id: Long = 0,
    var date: String = "",
    var type: TypeOrder? = null,
    var total: Double = 0.0
)
