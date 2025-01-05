package br.com.dashboard.company.service

import br.com.dashboard.company.entities.report.Report
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.ReportRepository
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.report.ReportRequestVO
import br.com.dashboard.company.vo.report.ReportResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class ReportService {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var reportRepository: ReportRepository

    @Transactional(readOnly = true)
    fun findAllReports(
        user: User,
        date: String?,
        pageable: Pageable
    ): Page<ReportResponseVO> {
        val reports: Page<Report> = reportRepository.findAllReports(userId = user.id, date = date, pageable = pageable)
        return reports.map { report -> parseObject(report, ReportResponseVO::class.java) }
    }

    fun getReport(
        userId: Long,
        reportId: Long
    ): Report {
        val reportSaved: Report? = reportRepository.findReportById(userId = userId, reportId = reportId)
        if (reportSaved != null) {
            return reportSaved
        } else {
            throw ResourceNotFoundException(message = REPORT_NOT_FOUND)
        }
    }

    @Transactional
    fun createNewReport(
        user: User,
        report: ReportRequestVO
    ): ReportResponseVO {
        val userAuthenticated = userService.findUserById(userId = user.id)
        val reportResult: Report = parseObject(report, Report::class.java)
        reportResult.date = LocalDate.now()
        reportResult.hour = LocalTime.now().withNano(0)
        reportResult.user = userAuthenticated
        return parseObject(reportRepository.save(reportResult), ReportResponseVO::class.java)
    }

    @Transactional
    fun deleteReport(
        userId: Long,
        reportId: Long
    ) {
        val report = getReport(userId = userId, reportId = reportId)
        reportRepository.deleteReportById(reportId = report.id, userId = userId)
    }

    companion object {
        const val REPORT_NOT_FOUND = "Report not found"
    }
}
