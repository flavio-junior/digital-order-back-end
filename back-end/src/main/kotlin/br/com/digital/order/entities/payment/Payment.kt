package br.com.digital.order.entities.payment

import br.com.digital.order.entities.company.Company
import br.com.digital.order.entities.order.Order
import br.com.digital.order.utils.common.PaymentType
import br.com.digital.order.utils.common.TypeOrder
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "tb_payment")
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var date: LocalDate? = null,
    var hour: LocalTime? = null,
    var code: Long? = null,
    var author: String? = "",
    var assigned: String? = "",
    @Column(name = "type_order", nullable = false)
    @Enumerated(EnumType.STRING)
    var typeOrder: TypeOrder? = null,
    @Column(name = "type_payment", nullable = false)
    @Enumerated(EnumType.STRING)
    var typePayment: PaymentType? = null,
    var discount: Boolean? = null,
    @Column(name = "value_discount", nullable = true)
    var valueDiscount: Double? = null,
    var fee: Boolean? = null,
    @Column(name = "value_fee", nullable = true)
    var valueFee: Double? = null,
    var total: Double = 0.0,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_order_payment",
        joinColumns = [JoinColumn(name = "fk_payment", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_order", referencedColumnName = "id")]
    )
    var order: Order? = null,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_company_payment",
        joinColumns = [JoinColumn(name = "fk_payment", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_company", referencedColumnName = "id")]
    )
    var company: Company? = null
)
