package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.report.Report
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long> {

    @Query(value = "SELECT r FROM Report r WHERE r.user.id = :userId")
    fun findAllReports(
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<Report>

    @Query(value = "SELECT r FROM Report r WHERE r.user.id = :userId AND (:date IS NULL OR r.date = CAST(:date AS date))")
    fun findReportsByDate(
        @Param("userId") userId: Long,
        @Param("date") date: String,
        pageable: Pageable
    ): Page<Report>

    @Query(value = "SELECT r FROM Report r WHERE r.user.id = :userId AND r.id = :reportId")
    fun findReportById(
        @Param("userId") userId: Long,
        @Param("reportId") reportId: Long
    ): Report?

    @Modifying
    @Query(value = "DELETE FROM Report r WHERE r.id = :reportId AND r.user.id = :userId")
    fun deleteReportById(
        @Param("userId") userId: Long,
        @Param("reportId") reportId: Long
    ): Int
}
