package br.com.digital.order.repository

import br.com.digital.order.entities.day.Day
import br.com.digital.order.utils.common.DayOfWeek
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DayRepository: JpaRepository<Day, Long> {

    @Query(value = "SELECT d FROM Day d WHERE d.fee.id = :feeId AND d.id = :dayId")
    fun findDayById(
        @Param("dayId") dayId: Long,
        @Param("feeId") feeId: Long
    ): Day?

    @Query("SELECT d FROM Day d WHERE d.fee.id = :feeId AND d.day = :day")
    fun checkDayAlreadyExists(
        @Param("feeId") feeId: Long,
        @Param("day") day: DayOfWeek
    ): Day?

    @Modifying
    @Query(value = "DELETE FROM Day d WHERE d.id = :dayId AND d.fee.id = :feeId")
    fun deleteDayById(
        @Param("dayId") dayId: Long,
        @Param("feeId") feeId: Long
    ): Int
}
