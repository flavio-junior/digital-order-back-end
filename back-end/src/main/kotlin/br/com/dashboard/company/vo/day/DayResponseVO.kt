package br.com.dashboard.company.vo.day

import br.com.dashboard.company.utils.common.DayOfWeek

data class DayResponseVO(
    var id: Long = 0,
    var day: DayOfWeek? = null
)
