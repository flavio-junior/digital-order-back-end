package br.com.digital.order.vo.day

import br.com.digital.order.utils.common.DayOfWeek

data class DaysRequestVO(
    var days: MutableList<DayOfWeek>? = null
)
