package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.`object`.Overview
import br.com.dashboard.company.utils.common.ObjectStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OverviewRepository : JpaRepository<Overview, Long> {

    @Query(value = "SELECT o FROM Overview o WHERE o.id = :overviewId AND o.objectResult.id = :objectId")
    fun findOverviewById(
        @Param("objectId") objectId: Long,
        @Param("overviewId") overviewId: Long
    ): Overview?

    @Modifying
    @Query(value = "UPDATE Overview o SET o.status = :status WHERE o.id = :overviewId AND o.objectResult.id = :objectId")
    fun updateStatusOverview(
        @Param("objectId") objectId: Long,
        @Param("overviewId") overviewId: Long,
        @Param("status") status: ObjectStatus?
    ): Int

    @Modifying
    @Query(value = "DELETE FROM Overview o WHERE o.id = :overviewId AND o.objectResult.id = :objectId")
    fun deleteOverviewById(
        @Param("objectId") objectId: Long,
        @Param("overviewId") overviewId: Long
    ): Int
}
