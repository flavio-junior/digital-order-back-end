package br.com.digital.order.entities.report

import br.com.digital.order.entities.company.Company
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "tb_report")
data class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var date: LocalDate? = null,
    var hour: LocalTime? = null,
    @Column(length = 1000, nullable = false)
    var resume: String? = null,
    var author: String? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_company_report",
        joinColumns = [JoinColumn(name = "fk_report", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_company", referencedColumnName = "id")]
    )
    var company: Company? = null
)
