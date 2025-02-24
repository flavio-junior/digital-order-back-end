package br.com.digital.order.vo.fee

import br.com.digital.order.utils.common.Function
import br.com.digital.order.vo.day.DayResponseVO

data class FeeResponseVO(
    var id: Long = 0,
    var percentage: Int = 0,
    var assigned: Function? = null,
    var days: MutableList<DayResponseVO>? = null
)
