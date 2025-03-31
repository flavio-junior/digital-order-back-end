package br.com.digital.order.entities.day

import br.com.digital.order.entities.fee.Fee
import br.com.digital.order.utils.common.DayOfWeek
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
