package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.report.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long>
