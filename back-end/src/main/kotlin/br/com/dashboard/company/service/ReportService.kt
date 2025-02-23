package br.com.dashboard.company.service

import br.com.dashboard.company.entities.report.Report
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.InternalErrorClient
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
import java.time.temporal.ChronoUnit

@Service
class ReportService {

    @Autowired
    private lateinit var companyService: CompanyService

    @Autowired
    private lateinit var reportRepository: ReportRepository

    @Transactional(readOnly = true)
    fun findAllReports(
        user: User,
        pageable: Pageable
    ): Page<ReportResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val reports: Page<Report> = reportRepository.findAllReports(companyId = companySaved.id, pageable = pageable)
        return reports.map { report -> parseObject(report, ReportResponseVO::class.java) }
    }

    @Transactional(readOnly = true)
    fun findReportsByDate(
        user: User,
        date: String,
        pageable: Pageable
    ): Page<ReportResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val reports: Page<Report> =
            reportRepository.findReportsByDate(companyId = companySaved.id, date = date, pageable = pageable)
        return reports.map { report -> parseObject(report, ReportResponseVO::class.java) }
    }

    fun getReport(
        user: User,
        reportId: Long
    ): Report {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val reportSaved: Report? = reportRepository.findReportById(companyId = companySaved.id, reportId = reportId)
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
        val reportResult: Report = parseObject(report, Report::class.java)
        reportResult.date = LocalDate.now()
        reportResult.hour = LocalTime.now().withNano(0)
        reportResult.company = companyService.getCompanyByUserLogged(user = user)
        return parseObject(reportRepository.save(reportResult), ReportResponseVO::class.java)
    }

    @Transactional
    fun deleteReport(
        user: User,
        reportId: Long
    ) {
        val actualDate = LocalDate.now()
        val reportSaved = getReport(user = user, reportId = reportId)
        if (reportSaved.date != null) {
            val daysDifference = ChronoUnit.DAYS.between(actualDate, reportSaved.date)
            if (daysDifference > 7) {
                throw InternalErrorClient(message = REPORT_EXPIRATION_DATE_EXPIRED)
            } else {
                reportRepository.deleteReportById(companyId = reportSaved.company?.id, reportId = reportSaved.id)
            }
        }
    }

    companion object {
        const val REPORT_NOT_FOUND = "Report not found"
        const val REPORT_EXPIRATION_DATE_EXPIRED = "Report expiration date expired!"
    }
}
