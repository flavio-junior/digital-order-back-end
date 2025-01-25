package br.com.dashboard.company.entities.employee

import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.utils.common.Function
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_employee")
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null,
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varying")
    var function: Function? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tb_user_employee",
        joinColumns = [JoinColumn(name = "fk_employee", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "fk_user", referencedColumnName = "id")]
    )
    var user: User? = null
)
