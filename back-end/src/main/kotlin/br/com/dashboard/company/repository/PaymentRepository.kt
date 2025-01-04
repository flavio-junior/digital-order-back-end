package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.payment.Payment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PaymentRepository : JpaRepository<Payment, Long> {

    @Query(value = "SELECT p FROM Payment p WHERE p.date = :date")
    fun getAllPaymentsDay(
        @Param("date") date: LocalDate = LocalDate.now(),
        pageable: Pageable
    ): Page<Payment>?

    @Query(value = "SELECT SUM(p.total) FROM Payment p WHERE p.date = :date")
    fun getGeneralBalance(
        @Param("date") date: LocalDate = LocalDate.now()
    ): Double?

    @Query(
        value =
            """
            SELECT
                SUM(p.total) 
            FROM
                tb_payment p
            WHERE
                p.date >= CURRENT_DATE - INTERVAL '7 days'
        """,
        nativeQuery = true
    )
    fun findBalanceLast7DaysNative(): Double?

    @Query(
        value =
            """
            SELECT
                SUM(p.total) 
            FROM
                tb_payment p 
            WHERE
                EXTRACT(YEAR FROM p.date) = EXTRACT(YEAR FROM CURRENT_DATE)
            AND
                EXTRACT(MONTH FROM p.date) = EXTRACT(MONTH FROM CURRENT_DATE)
        """,
        nativeQuery = true
    )
    fun findBalanceCurrentMonthNative(): Double?

    @Query(
        value =
            """
            SELECT
                SUM(p.total) 
            FROM
                tb_payment p
            WHERE
            EXTRACT(YEAR FROM p.date) = EXTRACT(YEAR FROM CURRENT_DATE)
        """,
        nativeQuery = true
    )
    fun findBalanceCurrentYearNative(): Double?
}
