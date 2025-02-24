package br.com.digital.order.repository

import br.com.digital.order.entities.report.Report
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long> {

    @Query(value = "SELECT r FROM Report r WHERE r.company.id = :companyId")
    fun findAllReports(
        @Param("companyId") companyId: Long? = null,
        pageable: Pageable
    ): Page<Report>

    @Query(value = "SELECT r FROM Report r WHERE r.company.id = :companyId AND (:date IS NULL OR r.date = CAST(:date AS date))")
    fun findReportsByDate(
        @Param("companyId") companyId: Long? = null,
        @Param("date") date: String,
        pageable: Pageable
    ): Page<Report>

    @Query(value = "SELECT r FROM Report r WHERE r.company.id = :companyId AND r.id = :reportId")
    fun findReportById(
        @Param("companyId") companyId: Long? = null,
        @Param("reportId") reportId: Long
    ): Report?

    @Modifying
    @Query(value = "DELETE FROM Report r WHERE r.id = :reportId AND r.company.id = :companyId")
    fun deleteReportById(
        @Param("companyId") companyId: Long? = null,
        @Param("reportId") reportId: Long
    ): Int
}
