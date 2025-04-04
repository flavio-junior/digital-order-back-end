package br.com.digital.order.service

import br.com.digital.order.entities.reservation.Reservation
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.ObjectDuplicateException
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.repository.ReservationRepository
import br.com.digital.order.utils.common.ReservationStatus
import br.com.digital.order.utils.others.ConverterUtils.parseObject
import br.com.digital.order.vo.reservation.GenerateReservationsRequestVO
import br.com.digital.order.vo.reservation.ReservationRequestVO
import br.com.digital.order.vo.reservation.ReservationResponseVO
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
    private lateinit var companyService: CompanyService

    @Transactional(readOnly = true)
    fun findAllReservations(
        user: User,
        name: String?,
        pageable: Pageable
    ): Page<ReservationResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val reservations: Page<Reservation>? =
            reservationRepository.findAllReservations(companyId = companySaved.id, name = name, pageable = pageable)
        return reservations?.map { reservation -> parseObject(reservation, ReservationResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = RESERVATION_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findReservationByName(
        user: User,
        name: String
    ): List<ReservationResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val reservations: List<Reservation> =
            reservationRepository.findReservationByName(companyId = companySaved.id, reservationName = name)
        return reservations.map { reservation -> parseObject(reservation, ReservationResponseVO::class.java) }
    }

    @Transactional(readOnly = true)
    fun findReservationById(
        user: User,
        id: Long
    ): ReservationResponseVO {
        val reservation = getReservation(user = user, reservationId = id)
        return parseObject(reservation, ReservationResponseVO::class.java)
    }

    fun getReservation(
        user: User,
        reservationId: Long
    ): Reservation {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val reservationSaved: Reservation? =
            reservationRepository.findReservationById(companyId = companySaved.id, reservationId = reservationId)
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
        if (!checkNameReservationAlreadyExists(user = user, name = reservation.name)) {
            val reservationResult: Reservation = parseObject(reservation, Reservation::class.java)
            reservationResult.status = ReservationStatus.AVAILABLE
            reservationResult.company = companyService.getCompanyByUserLogged(user = user)
            return parseObject(reservationRepository.save(reservationResult), ReservationResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_RESERVATION)
        }
    }

    @Transactional
    fun generateReservations(
        user: User,
        body: GenerateReservationsRequestVO
    ): List<ReservationResponseVO?> {
        return (body.start..body.end).mapIndexed { index, number ->
            try {
                val formattedNumber = number.toString().padStart(length = 2, padChar = '0')
                createNewReservation(
                    user = user,
                    reservation = ReservationRequestVO(name = "${body.prefix} $formattedNumber")
                )
            } catch (ex: ObjectDuplicateException) {
                null
            }
        }
    }

    fun validateReservationsToSave(
        user: User,
        reservations: MutableList<ReservationResponseVO>?,
        status: ReservationStatus
    ): MutableList<Reservation> {
        val reservationsToSave = mutableListOf<Reservation>()
        reservations?.forEach { reservationVO ->
            val reservationSave = getReservation(user = user, reservationId = reservationVO.id)
            if (reservationSave.status == ReservationStatus.RESERVED) {
                throw ObjectDuplicateException(message = "The '${reservationSave.name}' is already in use")
            }
            reservationSave.status = status
            reservationsToSave.add(reservationSave)
        }
        return reservationsToSave
    }

    private fun checkNameReservationAlreadyExists(
        user: User,
        name: String
    ): Boolean {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val reservationResult =
            reservationRepository.checkNameReservationAlreadyExists(companyId = companySaved.id, name = name)
        return reservationResult != null
    }

    @Transactional
    fun updateReservation(
        user: User,
        reservation: ReservationResponseVO
    ): ReservationResponseVO {
        if (!checkNameReservationAlreadyExists(user = user, name = reservation.name)) {
            val reservationSaved: Reservation = getReservation(user = user, reservationId = reservation.id)
            reservationSaved.name = reservation.name
            return parseObject(reservationRepository.save(reservationSaved), ReservationResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_RESERVATION)
        }
    }

    @Transactional
    fun updateStatusReservation(
        user: User,
        reservationId: Long,
        status: ReservationStatus
    ) {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        reservationRepository.updateStatusReservation(
            companyId = companySaved.id,
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
        user: User,
        reservationId: Long
    ) {
        val reservationSaved: Reservation = getReservation(user = user, reservationId = reservationId)
        reservationRepository.deleteReservationById(
            companyId = reservationSaved.company?.id,
            reservationId = reservationSaved.id
        )
    }

    companion object {
        const val RESERVATION_NOT_FOUND = "Reservation not found!"
        const val DUPLICATE_NAME_RESERVATION = "The Reservation already exists"
    }
}
