package br.com.dashboard.company.vo.fee

import br.com.dashboard.company.utils.common.Function
import br.com.dashboard.company.vo.day.DayResponseVO

data class FeeResponseVO(
    var id: Long = 0,
    var price: Double = 0.0,
    var assigned: Function? = null,
    var days: MutableList<DayResponseVO>? = null
)
