package br.com.dashboard.company.entities.payment

import br.com.dashboard.company.entities.user.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "tb_details_payment")
data class DetailsPayment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var author: String? = "",
    var assigned: String? = "",
    var date: LocalDate? = null,
    var hour: LocalTime? = null,
    var value: Double = 0.0,
    var identifier: Long = 0,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_user_details_payment",
        joinColumns = [JoinColumn(name = "fk_details_payment", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_user", referencedColumnName = "id")]
    )
    var user: User? = null
)
