package br.com.digital.order.entities.address

import br.com.digital.order.entities.order.Order
import br.com.digital.order.utils.common.AddressStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "tb_address")
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varying")
    var status: AddressStatus? = null,
    var street: String = "",
    var number: Int? = null,
    var district: String = "",
    var complement: String = "",
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_order_address",
        joinColumns = [JoinColumn(name = "fk_order", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_address", referencedColumnName = "id")]
    )
    var order: Order? = null
)
