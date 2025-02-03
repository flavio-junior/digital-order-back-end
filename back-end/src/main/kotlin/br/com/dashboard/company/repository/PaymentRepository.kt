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

    @Query(
        value = """
        SELECT p FROM Payment p WHERE p.date = :date AND p.user.id = :userId
        AND (:code IS NULL OR p.code = :code)
        """)
    fun getAllPaymentsDay(
        @Param("date") date: LocalDate = LocalDate.now(),
        @Param("code") code: Long?,
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<Payment>?

    @Query(
        value = """
                SELECT 
                    p.typePayment, 
                    p.typeOrder, 
                    COUNT(p), 
                    SUM(p.total), 
                    SUM(CASE WHEN p.discount = true THEN 1 ELSE 0 END)
                FROM Payment p
                WHERE p.date = :date AND p.user.id = :userId
                GROUP BY p.typePayment, p.typeOrder
        """
    )
    fun getAnalysisDay(
        @Param("date") date: LocalDate,
        @Param("userId") userId: Long
    ): List<Array<Any>>

    @Query(
        value = """
        SELECT 
            p.typePayment, 
            p.typeOrder, 
            COUNT(p), 
            SUM(p.total), 
            SUM(CASE WHEN p.discount = true THEN 1 ELSE 0 END)
        FROM Payment p
        WHERE p.date >= :startDate 
        AND p.date <= :endDate
        AND p.user.id = :userId
        GROUP BY p.typePayment, p.typeOrder
    """
    )
    fun getAnalysisOfWeek(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("userId") userId: Long
    ): List<Array<Any>>

    @Query(
        value = """
        SELECT 
            p.typePayment, 
            p.typeOrder, 
            COUNT(p), 
            SUM(p.total), 
            SUM(CASE WHEN p.discount = true THEN 1 ELSE 0 END)
        FROM Payment p
        WHERE EXTRACT(YEAR FROM p.date) = EXTRACT(YEAR FROM CURRENT_DATE)
        AND EXTRACT(MONTH FROM p.date) = EXTRACT(MONTH FROM CURRENT_DATE)
        AND p.user.id = :userId
        GROUP BY p.typePayment, p.typeOrder
    """
    )
    fun getAnalysisMonth(
        @Param("userId") userId: Long
    ): List<Array<Any>>

    @Query(
        value = """
        SELECT 
            p.typePayment, 
            p.typeOrder, 
            COUNT(p), 
            SUM(p.total), 
            SUM(CASE WHEN p.discount = true THEN 1 ELSE 0 END)
        FROM Payment p
        WHERE EXTRACT(YEAR FROM p.date) = EXTRACT(YEAR FROM CURRENT_DATE)
        AND p.user.id = :userId
        GROUP BY p.typePayment, p.typeOrder
    """
    )
    fun getAnalysisYear(
        @Param("userId") userId: Long
    ): List<Array<Any>>
}
