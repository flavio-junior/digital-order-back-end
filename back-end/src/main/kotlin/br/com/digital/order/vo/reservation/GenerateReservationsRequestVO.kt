package br.com.digital.order.vo.reservation

data class GenerateReservationsRequestVO(
    val prefix: String = "",
    val start: Int = 0,
    val end: Int
)
