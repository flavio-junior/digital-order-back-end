package br.com.digital.order.vo.reservation

import br.com.digital.order.utils.common.ReservationStatus

data class ReservationResponseVO(
    var id: Long = 0,
    var name: String = "",
    var status: ReservationStatus? = null
)
