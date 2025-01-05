package br.com.dashboard.company.entities.report

import br.com.dashboard.company.entities.user.User
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
        name = "tb_user_report",
        joinColumns = [JoinColumn(name = "fk_report", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_user", referencedColumnName = "id")]
    )
    var user: User? = null
)
