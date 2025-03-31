package br.com.digital.order.entities.`object`

import br.com.digital.order.utils.common.ObjectStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_overview")
data class Overview(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varying")
    var status: ObjectStatus? = null,
    var quantity: Int = 0,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_object_overview",
        joinColumns = [JoinColumn(name = "fk_overview", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_object", referencedColumnName = "id")]
    )
    var objectResult: Object? = null
)
