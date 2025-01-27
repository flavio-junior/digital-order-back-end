package br.com.dashboard.company.entities.payment

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
    var identifier: Long = 0
)
