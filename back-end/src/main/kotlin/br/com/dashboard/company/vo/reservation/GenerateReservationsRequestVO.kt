package br.com.dashboard.company.vo.reservation

data class GenerateReservationsRequestVO(
    val prefix: String = "",
    val start: Int = 0,
    val end: Int
)
