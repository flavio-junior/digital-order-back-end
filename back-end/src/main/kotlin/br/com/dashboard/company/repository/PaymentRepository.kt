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
        SELECT p FROM Payment p WHERE p.date = :date AND p.company.id = :companyId
        AND (:code IS NULL OR p.code = :code)
        """
    )
    fun getAllPaymentsDay(
        @Param("date") date: LocalDate = LocalDate.now(),
        @Param("code") code: Long?,
        @Param("companyId") companyId: Long? = null,
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
                WHERE p.date = :date AND p.company.id = :companyId
                GROUP BY p.typePayment, p.typeOrder
        """
    )
    fun getAnalysisDay(
        @Param("date") date: LocalDate? = null,
        @Param("companyId") companyId: Long
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
            WHERE p.date BETWEEN :start AND :end AND p.company.id = :companyId
            GROUP BY p.typePayment, p.typeOrder
    """
    )
    fun getAnalysisDayBetweenDays(
        @Param("start") start: LocalDate,
        @Param("end") end: LocalDate,
        @Param("companyId") companyId: Long? = null
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
        AND p.company.id = :companyId
        GROUP BY p.typePayment, p.typeOrder
    """
    )
    fun getAnalysisOfWeek(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("companyId") companyId: Long? = null
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
        AND p.company.id = :companyId
        GROUP BY p.typePayment, p.typeOrder
    """
    )
    fun getAnalysisMonth(
        @Param("companyId") companyId: Long? = null
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
        AND p.company.id = :companyId
        GROUP BY p.typePayment, p.typeOrder
    """
    )
    fun getAnalysisYear(
        @Param("companyId") companyId: Long? = null
    ): List<Array<Any>>
}
