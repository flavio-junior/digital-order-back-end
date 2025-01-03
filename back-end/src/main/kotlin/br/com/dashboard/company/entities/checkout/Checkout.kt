package br.com.dashboard.company.entities.checkout

import br.com.dashboard.company.utils.common.TypeOrder
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "tb_checkout_details")
data class Checkout(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var date: LocalDate? = null,
    @Enumerated(EnumType.STRING)
    var type: TypeOrder? = null,
    var total: Double = 0.0
)
