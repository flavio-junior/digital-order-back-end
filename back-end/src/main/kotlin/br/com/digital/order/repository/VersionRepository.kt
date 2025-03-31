package br.com.digital.order.repository

import br.com.digital.order.entities.version.Version
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VersionRepository : JpaRepository<Version, Long> {

    @Query("SELECT v FROM Version v WHERE v.version =:version")
    fun findByVersion(
        @Param("version") version: String
    ): Version?

    @Modifying
    @Query("UPDATE Version v SET v.version =:version, v.url =:url WHERE v.id =:versionId")
    fun updateUrlVersion(
        @Param("versionId") versionId: Long,
        @Param("version") version: String,
        @Param("url") url: String
    )
}
