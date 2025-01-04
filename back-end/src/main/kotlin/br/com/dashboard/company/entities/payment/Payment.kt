package br.com.dashboard.company.entities.payment

import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.utils.common.PaymentType
import br.com.dashboard.company.utils.common.TypeOrder
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
    @Column(name = "type_order", nullable = false)
    @Enumerated(EnumType.STRING)
    var typeOrder: TypeOrder? = null,
    @Column(name = "type_payment", nullable = false)
    @Enumerated(EnumType.STRING)
    var typePayment: PaymentType? = null,
    var discount: Boolean? = null,
    @Column(name = "value_discount", nullable = true)
    var valueDiscount: Double? = null,
    var total: Double = 0.0,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_order_payment",
        joinColumns = [JoinColumn(name = "fk_payment", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_order", referencedColumnName = "id")]
    )
    var order: Order? = null
)
