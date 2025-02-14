package br.com.dashboard.company.entities.version

import jakarta.persistence.*

@Entity
@Table(name = "tb_version")
data class Version(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var version: String = "",
    var url: String = ""
)
