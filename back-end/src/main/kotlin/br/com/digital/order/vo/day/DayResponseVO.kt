package br.com.digital.order.vo.day

import br.com.digital.order.utils.common.DayOfWeek

data class DayResponseVO(
    var id: Long = 0,
    var day: DayOfWeek? = null
)
