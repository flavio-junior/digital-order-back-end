package br.com.dashboard.company.vo.reservation

import br.com.dashboard.company.utils.common.ReservationStatus

data class ReservationResponseVO(
    var id: Long = 0,
    var name: String = "",
    var status: ReservationStatus? = null
)
