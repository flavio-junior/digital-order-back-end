package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.reservation.Reservation
import br.com.dashboard.company.utils.common.ReservationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long> {

    @Query(value = "SELECT r FROM Reservation r WHERE r.company.id = :companyId AND r.name = :name")
    fun checkNameReservationAlreadyExists(
        @Param("companyId") companyId: Long? = null,
        @Param("name") name: String
    ): Reservation?

    @Query(
        value = """
        SELECT r FROM Reservation r
            WHERE r.company.id = :companyId
        AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """
    )
    fun findAllReservations(
        @Param("companyId") companyId: Long? = null,
        @Param("name") name: String?, pageable: Pageable
    ): Page<Reservation>?

    @Query(
        value = "SELECT r FROM Reservation r WHERE r.company.id = :companyId AND LOWER(r.name) LIKE LOWER(CONCAT('%', :reservationName, '%')) AND r.status = AVAILABLE"
    )
    fun findReservationByName(
        @Param("companyId") companyId: Long? = null,
        @Param("reservationName") reservationName: String
    ): List<Reservation>

    @Query(value = "SELECT r FROM Reservation r WHERE r.company.id = :companyId AND r.id = :idReservation")
    fun findReservationById(
        @Param("companyId") companyId: Long? = null,
        @Param("idReservation") reservationId: Long
    ): Reservation?

    @Modifying
    @Query(value = "UPDATE Reservation r SET r.status =:status WHERE r.company.id = :companyId AND r.id = :reservationId")
    fun updateStatusReservation(
        @Param("companyId") companyId: Long? = null,
        @Param("reservationId") reservationId: Long,
        @Param("status") status: ReservationStatus
    )

    @Modifying
    @Query(
        nativeQuery = true,
        value = "DELETE FROM tb_order_reservation WHERE fk_order = :orderId AND fk_reservation = :reservationId",
    )
    fun deleteRelationBetweenReservationAndByIdOrderById(
        @Param("orderId") orderId: Long,
        @Param("reservationId") reservationId: Long
    ): Int

    @Modifying
    @Query(value = "DELETE FROM Reservation r WHERE r.id = :reservationId AND r.company.id = :companyId")
    fun deleteReservationById(
        @Param("companyId") companyId: Long? = null,
        @Param("reservationId") reservationId: Long
    ): Int
}
