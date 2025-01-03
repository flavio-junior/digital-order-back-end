package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.checkout.Checkout
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface CheckoutRepository : JpaRepository<Checkout, Long> {

    @Query("SELECT c FROM Checkout c WHERE c.date = :date")
    fun getAllCheckoutsDay(
        @Param("date") date: LocalDate = LocalDate.now(),
        pageable: Pageable
    ): Page<Checkout>?

    @Query("SELECT SUM(c.total) FROM Checkout c WHERE c.date = :date")
    fun getGeneralBalance(
        @Param("date") date: LocalDate = LocalDate.now()
    ): Double?

    @Query(
        value =
            """
            SELECT
                SUM(c.total) 
            FROM
                tb_checkout_details c 
            WHERE
                c.date >= CURRENT_DATE - INTERVAL '7 days'
        """,
        nativeQuery = true
    )
    fun findBalanceLast7DaysNative(): Double?

    @Query(
        value =
            """
            SELECT
                SUM(c.total) 
            FROM
                tb_checkout_details c 
            WHERE
                EXTRACT(YEAR FROM c.date) = EXTRACT(YEAR FROM CURRENT_DATE)
            AND
                EXTRACT(MONTH FROM c.date) = EXTRACT(MONTH FROM CURRENT_DATE)
        """,
        nativeQuery = true
    )
    fun findBalanceCurrentMonthNative(): Double?

    @Query(
        value =
            """
            SELECT
                SUM(c.total) 
            FROM
                tb_checkout_details c 
            WHERE
            EXTRACT(YEAR FROM c.date) = EXTRACT(YEAR FROM CURRENT_DATE)
        """,
        nativeQuery = true
    )
    fun findBalanceCurrentYearNative(): Double?
}
