package br.com.digital.order.utils.others

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}
