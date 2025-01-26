package br.com.dashboard.company.entities.day

import br.com.dashboard.company.entities.fee.Fee
import br.com.dashboard.company.utils.common.DayOfWeek
import jakarta.persistence.*

@Entity
@Table(name = "tb_day")
data class Day(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varying")
    var day: DayOfWeek? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_fee_day",
        joinColumns = [JoinColumn(name = "fk_day", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")]
    )
    var fee: Fee? = null
)
