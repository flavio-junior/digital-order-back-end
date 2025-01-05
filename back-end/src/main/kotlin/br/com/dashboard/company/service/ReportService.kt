package br.com.dashboard.company.service

import br.com.dashboard.company.entities.report.Report
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.repository.ReportRepository
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.report.ReportRequestVO
import br.com.dashboard.company.vo.report.ReportResponseVO
import org.springframework.beans.factory.annotation.Autowired
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
}
