package br.com.dashboard.company.entities.fee

import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.utils.common.Function
import jakarta.persistence.*

@Entity
@Table(name = "tb_fee")
data class Fee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var price: Double = 0.0,
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varying")
    var assigned: Function? = null,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "tb_fee_author",
        joinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_author", referencedColumnName = "id")]
    )
    var author: Author? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_user_fee",
        joinColumns = [JoinColumn(name = "fk_fee", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_user", referencedColumnName = "id")]
    )
    var user: User? = null
)
