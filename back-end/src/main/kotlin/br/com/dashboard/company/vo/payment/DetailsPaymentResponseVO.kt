package br.com.dashboard.company.vo.payment

import java.time.LocalDate
import java.time.LocalTime

data class DetailsPaymentResponseVO(
    var id: Long = 0,
    var author: String? = "",
    var assigned: String? = "",
    var date: LocalDate? = null,
    var hour: LocalTime? = null,
    var value: Double = 0.0,
    var identifier: Long = 0
)
