package br.com.dashboard.company.entities.fee

import jakarta.persistence.*

@Entity
@Table(name = "tb_author")
data class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var author: String? = "",
    var assigned: String? = "",
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_fee_author",
        joinColumns = [JoinColumn(name = "fk_author", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")]
    )
    var fee: Fee? = null,
)
