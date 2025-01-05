package br.com.dashboard.company.vo.report

data class ReportResponseVO(
    var id: Long = 0,
    var date: String? = null,
    var hour: String? = null,
    var resume: String? = null,
    var author: String? = null
)
