package br.com.digital.order.entities.item

import br.com.digital.order.entities.company.Company
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "tb_item")
data class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "name", nullable = false, unique = true)
    var name: String = "",
    var price: Double = 0.0,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_company_item",
        joinColumns = [JoinColumn(name = "fk_item", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_company", referencedColumnName = "id")]
    )
    var company: Company? = null
)
