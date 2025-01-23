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

    @Query(value = "SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.name = :name")
    fun checkNameReservationAlreadyExists(
        @Param("userId") userId: Long,
        @Param("name") name: String
    ): Reservation?

    @Query(
        value = """
        SELECT r FROM Reservation r
            WHERE r.user.id = :userId
        AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """
    )
    fun findAllReservations(
        @Param("userId") userId: Long,
        @Param("name") name: String?, pageable: Pageable
    ): Page<Reservation>?

    @Query(
        value = "SELECT r FROM Reservation r WHERE r.user.id = :userId AND LOWER(r.name) LIKE LOWER(CONCAT('%', :reservationName, '%')) AND r.status = AVAILABLE"
    )
    fun findReservationByName(
        @Param("userId") userId: Long,
        @Param("reservationName") reservationName: String
    ): List<Reservation>

    @Query(value = "SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.id = :idReservation")
    fun findReservationById(
        @Param("userId") userId: Long,
        @Param("idReservation") reservationId: Long
    ): Reservation?

    @Modifying
    @Query(value = "UPDATE Reservation r SET r.status =:status WHERE r.user.id = :userId AND r.id = :reservationId")
    fun updateStatusReservation(
        @Param("userId") userId: Long,
        @Param("reservationId") reservationId: Long,
        @Param("status") status: ReservationStatus
    )

    @Modifying
    @Query(
        value = "DELETE FROM tb_order_reservation WHERE fk_order = :orderId AND fk_reservation = :reservationId",
        nativeQuery = true
    )
    fun deleteRelationBetweenReservationAndByIdOrderById(
        @Param("orderId") orderId: Long,
        @Param("reservationId") reservationId: Long
    ): Int

    @Modifying
    @Query(value = "DELETE FROM Reservation r WHERE r.id = :reservationId AND r.user.id = :userId")
    fun deleteReservationById(
        @Param("userId") userId: Long,
        @Param("reservationId") reservationId: Long
    ): Int
}
