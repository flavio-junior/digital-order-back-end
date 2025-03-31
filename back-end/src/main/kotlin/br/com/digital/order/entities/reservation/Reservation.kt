package br.com.digital.order.entities.reservation

import br.com.digital.order.entities.company.Company
import br.com.digital.order.entities.order.Order
import br.com.digital.order.utils.common.ReservationStatus
import jakarta.persistence.*

@Entity
@Table(name = "tb_reservation")
data class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "name", nullable = false, unique = true)
    var name: String = "",
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus? = null,
    @ManyToOne
    @JoinTable(
        name = "tb_order_reservation",
        joinColumns = [JoinColumn(name = "fk_order", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_reservation", referencedColumnName = "id")]
    )
    var order: Order? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_company_reservation",
        joinColumns = [JoinColumn(name = "fk_reservation", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_company", referencedColumnName = "id")]
    )
    var company: Company? = null
)
