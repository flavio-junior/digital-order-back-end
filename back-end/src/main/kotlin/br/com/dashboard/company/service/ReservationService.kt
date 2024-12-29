package br.com.dashboard.company.service

import br.com.dashboard.company.entities.reservation.Reservation
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ObjectDuplicateException
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.ReservationRepository
import br.com.dashboard.company.utils.common.ReservationStatus
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.reservation.ReservationRequestVO
import br.com.dashboard.company.vo.reservation.ReservationResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService {

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Autowired
    private lateinit var userService: UserService

    @Transactional(readOnly = true)
    fun findAllReservations(
        user: User,
        name: String?,
        pageable: Pageable
    ): Page<ReservationResponseVO> {
        val reservations: Page<Reservation>? =
            reservationRepository.findAllReservations(userId = user.id, name = name, pageable = pageable)
        return reservations?.map { reservation -> parseObject(reservation, ReservationResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = RESERVATION_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findReservationByName(
        user: User,
        name: String
    ): List<ReservationResponseVO> {
        val reservations: List<Reservation> =
            reservationRepository.findReservationByName(userId = user.id, reservationName = name)
        return reservations.map { reservation -> parseObject(reservation, ReservationResponseVO::class.java) }
    }

    @Transactional(readOnly = true)
    fun findReservationById(
        user: User,
        id: Long
    ): ReservationResponseVO {
        val reservation = getReservation(userId = user.id, reservationId = id)
        return parseObject(reservation, ReservationResponseVO::class.java)
    }

   fun getReservation(
        userId: Long,
        reservationId: Long
    ): Reservation {
        val reservationSaved: Reservation? =
            reservationRepository.findReservationById(userId = userId, reservationId = reservationId)
        if (reservationSaved != null) {
            return reservationSaved
        } else {
            throw ResourceNotFoundException(RESERVATION_NOT_FOUND)
        }
    }

    @Transactional
    fun createNewReservation(
        user: User,
        reservation: ReservationRequestVO
    ): ReservationResponseVO {
        if (!checkNameReservationAlreadyExists(userId = user.id, name = reservation.name)) {
            val userAuthenticated = userService.findUserById(id = user.id)
            val reservationResult: Reservation = parseObject(reservation, Reservation::class.java)
            reservationResult.status = ReservationStatus.AVAILABLE
            reservationResult.user = userAuthenticated
            return parseObject(reservationRepository.save(reservationResult), ReservationResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_RESERVATION)
        }
    }

    fun validateReservationsToSave(
        userId: Long,
        reservations: MutableList<ReservationResponseVO>?,
        status: ReservationStatus
    ):  MutableList<Reservation> {
        val reservationsToSave = mutableListOf<Reservation>()
        reservations?.forEach { reservationVO ->
            val reservationSave = getReservation(userId = userId, reservationId = reservationVO.id)
            if (reservationSave.status == ReservationStatus.RESERVED) {
                throw ObjectDuplicateException(message = "The '${reservationSave.name}' is already in use")
            }
            reservationSave.status = status
            reservationsToSave.add(reservationSave)
        }
        return reservationsToSave
    }

    private fun checkNameReservationAlreadyExists(
        userId: Long,
        name: String
    ): Boolean {
        val reservationResult = reservationRepository.checkNameReservationAlreadyExists(userId = userId, name = name)
        return reservationResult != null
    }

    @Transactional
    fun updateReservation(
        user: User,
        reservation: ReservationResponseVO
    ): ReservationResponseVO {
        if (!checkNameReservationAlreadyExists(userId = user.id, name = reservation.name)) {
            val reservationSaved: Reservation = getReservation(userId = user.id, reservationId = reservation.id)
            reservationSaved.name = reservation.name
            return parseObject(reservationRepository.save(reservationSaved), ReservationResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_RESERVATION)
        }
    }

    @Transactional
    fun updateStatusReservation(
        userId: Long,
        reservationId: Long,
        status: ReservationStatus
    ) {
        reservationRepository.updateStatusReservation(
            userId = userId,
            reservationId = reservationId,
            status = status
        )
    }

    @Transactional
    fun removeReservationOrder(
        orderId: Long,
        reservationId: Long
    ) {
        reservationRepository.deleteRelationBetweenReservationAndByIdOrderById(orderId, reservationId)
    }

    @Transactional
    fun deleteReservation(
        userId: Long,
        reservationId: Long
    ) {
        val reservationSaved: Reservation = getReservation(userId = userId, reservationId = reservationId)
        reservationRepository.deleteReservationById(userId = userId, reservationId = reservationSaved.id)
    }

    companion object {
        const val RESERVATION_NOT_FOUND = "Reservation not found!"
        const val DUPLICATE_NAME_RESERVATION = "The Reservation already exists"
    }
}
