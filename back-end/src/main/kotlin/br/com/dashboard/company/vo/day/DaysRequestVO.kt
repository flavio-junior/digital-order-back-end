package br.com.dashboard.company.vo.day

import br.com.dashboard.company.utils.common.DayOfWeek

data class DaysRequestVO(
    var days: MutableList<DayOfWeek>? = null
)
