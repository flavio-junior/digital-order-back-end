package br.com.digital.order.entities.fee

import br.com.digital.order.entities.company.Company
import br.com.digital.order.entities.day.Day
import br.com.digital.order.entities.order.Order
import br.com.digital.order.utils.common.Function
import jakarta.persistence.*

@Entity
@Table(name = "tb_fee")
data class Fee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var percentage: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varying")
    var assigned: Function? = null,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_fee_day",
        joinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_day", referencedColumnName = "id")]
    )
    var days: MutableList<Day>? = null,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_fee_author",
        joinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_author", referencedColumnName = "id")]
    )
    var author: Author? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_order_fee",
        joinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_order", referencedColumnName = "id")]
    )
    var order: Order? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_company_fee",
        joinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_company", referencedColumnName = "id")]
    )
    var company: Company? = null
)
