package br.com.digital.order.utils.others

import br.com.digital.order.entities.day.Day
import br.com.digital.order.utils.common.DayOfWeek

class ValidDayFeeUtils {

    fun getDays(
        days: List<Day>?,
        addFeeWithBaseDayOfWeek: () -> Unit = {},
        addFeeAllDays: () -> Unit = {}
    ) {
        val currentDayOfWeek = java.time.LocalDate.now().dayOfWeek
        if (days?.any { it.day == DayOfWeek.ALL } == true) {
            addFeeAllDays()
        }
        if (days?.any { it.day == converterDayOfWeekToEnum(dayOfWeek = currentDayOfWeek) } == true) {
            addFeeWithBaseDayOfWeek()
        }
    }
}

private fun converterDayOfWeekToEnum(dayOfWeek: java.time.DayOfWeek): DayOfWeek {
    return when (dayOfWeek) {
        java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
        java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
        java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
        java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
        java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
        java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
    }
}
