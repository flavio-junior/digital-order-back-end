package br.com.digital.order.service

import br.com.digital.order.entities.fee.Author
import br.com.digital.order.entities.order.Order
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.InternalErrorClient
import br.com.digital.order.exceptions.ObjectDuplicateException
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.repository.OrderRepository
import br.com.digital.order.service.ObjectService.Companion.OBJECT_ALREADY_EXISTS
import br.com.digital.order.service.ObjectService.Companion.OBJECT_NOT_FOUND
import br.com.digital.order.service.ObjectService.Companion.OBJECT_WITH_PENDING_DELIVERY
import br.com.digital.order.service.ReservationService.Companion.RESERVATION_NOT_FOUND
import br.com.digital.order.utils.common.*
import br.com.digital.order.utils.common.Function
import br.com.digital.order.utils.others.ConstantsUtils.ZERO_QUANTITY_ERROR
import br.com.digital.order.utils.others.ConverterUtils.parseObject
import br.com.digital.order.utils.others.ValidDayFeeUtils
import br.com.digital.order.vo.address.UpdateAddressRequestVO
import br.com.digital.order.vo.`object`.ObjectRequestVO
import br.com.digital.order.vo.`object`.UpdateObjectRequestVO
import br.com.digital.order.vo.order.OrderRequestVO
import br.com.digital.order.vo.order.OrderResponseVO
import br.com.digital.order.vo.order.UpdateStatusDeliveryRequestVO
import br.com.digital.order.vo.payment.PaymentRequestVO
import br.com.digital.order.vo.product.RestockProductRequestVO
import br.com.digital.order.vo.reservation.ReservationResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Repository
class OrderService {

    @Autowired
    private lateinit var qrCodeService: QRCodeService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var addressService: AddressService

    @Autowired
    private lateinit var objectService: ObjectService

    @Autowired
    private lateinit var reservationService: ReservationService

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var companyService: CompanyService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var feeService: FeeService

    @Autowired
    private lateinit var authorService: AuthorService

    private val validDays = ValidDayFeeUtils()

    @Transactional(readOnly = true)
    fun findAllOrders(
        user: User,
        status: Status,
        pageable: Pageable
    ): Page<OrderResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val orders: Page<Order>? =
            orderRepository.findAllOrdersOpen(companyId = companySaved.id, status = status, pageable = pageable)
        return orders?.map { order -> parseObject(order, OrderResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = ORDER_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findOrderById(
        user: User,
        orderId: Long
    ): OrderResponseVO {
        val order = getOrder(user = user, orderId = orderId)
        return parseObject(order, OrderResponseVO::class.java)
    }

    @Transactional(readOnly = true)
    fun findOrderByCodePayment(
        code: Long
    ): OrderResponseVO {
        val order: Order? = orderRepository.findOrderByCodePayment(code = code)
        if (order != null) {
            return parseObject(order, OrderResponseVO::class.java)
        } else {
            throw ResourceNotFoundException(message = ORDER_NOT_FOUND)
        }
    }

    private fun getOrder(
        user: User,
        orderId: Long
    ): Order {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val orderSaved: Order? = orderRepository.findOrderById(companyId = companySaved.id, orderId = orderId)
        if (orderSaved != null) {
            return orderSaved
        } else {
            throw ResourceNotFoundException(ORDER_NOT_FOUND)
        }
    }

    @Transactional
    fun createNewOrder(
        user: User,
        order: OrderRequestVO
    ): OrderResponseVO {
        val userAuthenticated = userService.findUserById(userId = user.id)
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val orderResult: Order = parseObject(order, Order::class.java)
        var total: Double
        var qrCode = false
        var savePayment = false
        orderResult.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        if (order.type == TypeOrder.SHOPPING_CART) {
            orderResult.status = Status.CLOSED
            val objectsSaved = objectService.saveObjects(user = user, buy = true, objectsToSave = order.objects)
            orderResult.objects = objectsSaved.first
            total = objectsSaved.second
            if (order.payment == null) {
                throw InternalErrorClient(message = SELECT_PAYMENT_TO_CLOSE_ORDER)
            }
        } else {
            orderResult.status = Status.OPEN
            val objectsSaved = objectService.saveObjects(user = user, objectsToSave = order.objects)
            orderResult.objects = objectsSaved.first
            total = objectsSaved.second
        }
        if (order.type == TypeOrder.RESERVATION) {
            val fee = feeService.getFeeByType(companyId = companySaved.id, assigned = Function.WAITER)
            if (fee != null) {
                val saveFee = {
                    fee.author = authorService.saveAuthor(
                        user = user,
                        author = Author(
                            author = userAuthenticated?.name,
                            assigned = order.fee?.assigned
                        )
                    )
                    fee.company = companySaved
                    orderResult.fee = fee
                    val feeValue = (total * fee.percentage) / 100.0
                    total += feeValue
                }
                validDays.getDays(
                    days = fee.days,
                    addFeeWithBaseDayOfWeek = {
                        saveFee()
                    },
                    addFeeAllDays = {
                        saveFee()
                    }
                )
            }
        } else {
            orderResult.fee = null
        }
        orderResult.reservations = reservationService.validateReservationsToSave(
            user = user,
            reservations = order.reservations,
            status = ReservationStatus.RESERVED
        )
        val addressSaved = order.address?.let { addressService.saveAddress(addressRequestVO = it) }
        orderResult.address = addressSaved
        orderResult.quantity = order.objects?.size ?: 0
        if (order.payment?.type != null) {
            var applyDiscount = total
            if (order.payment?.discount == true) {
                applyDiscount -= order.payment?.value ?: 0.0
            }
            savePayment = true
            qrCode = true
        }
        orderResult.total = total
        orderResult.company = companySaved
        orderResult.payment = null
        val orderSaved = orderRepository.save(orderResult)
        if (savePayment) {
            orderSaved.payment = paymentService.savePayment(
                company = companySaved,
                order = orderSaved,
                payment = order.payment,
                author = orderSaved.fee?.author?.author ?: user.name,
                assigned = orderSaved.fee?.author?.assigned ?: user.name,
            )
        }
        val orderResponse = parseObject(orderRepository.save(orderSaved), OrderResponseVO::class.java)
        if (qrCode) {
            val qrCodeBytes =
                qrCodeService.generateQRCodeImage(value = orderResponse.payment?.code.toString(), 300, 300)
            orderResponse.qrCode = Base64.getEncoder().encodeToString(qrCodeBytes)
        }
        return orderResponse
    }

    @Transactional
    fun incrementMoreObjectsOrder(
        user: User,
        orderId: Long,
        objects: MutableList<ObjectRequestVO>
    ) {
        val orderSaved = getOrder(orderId = orderId, user = user)
        objects.map { objectAvailable ->
            val objectFound = orderSaved.objects?.find { it.name == objectAvailable.name }
            if (objectFound != null) {
                throw ObjectDuplicateException(message = OBJECT_ALREADY_EXISTS)
            }
        }
        val objectsSaved = objectService.saveObjects(user = user, order = orderSaved, objectsToSave = objects)
        orderRepository.updateQuantityOrder(orderId = orderId, objectsSaved.first?.size)
        incrementDataOrder(
            orderId = orderId,
            total = objectsSaved.second
        )
    }

    @Transactional
    fun updateObject(
        user: User,
        orderId: Long,
        objectId: Long,
        objectActual: UpdateObjectRequestVO
    ) {
        val orderSaved: Order = getOrder(user = user, orderId = orderId)
        val identifiedObject = orderSaved.objects?.firstOrNull { it.id == objectId }
        if (identifiedObject != null) {
            val objectSaved = objectService.getObject(orderId = orderSaved.id, objectId = objectId)
            val priceCalculated = (objectSaved.price * objectActual.quantity)
            when (objectActual.action) {
                Action.INCREMENT_OVERVIEW -> {
                    if (objectActual.quantity == 0) {
                        throw InternalErrorClient(message = ZERO_QUANTITY_ERROR)
                    }
                    objectService.incrementMoreItemsObject(
                        user = user,
                        orderId = orderId,
                        objectId = objectId,
                        quantity = objectActual.quantity,
                        total = priceCalculated,
                        objectResult = objectSaved
                    )
                    incrementDataOrder(
                        orderId = orderId,
                        total = priceCalculated
                    )
                }

                Action.REMOVE_OVERVIEW -> {
                    val overviewResult = objectService.removeOverview(
                        user = user,
                        orderId = orderId,
                        objectId = objectId,
                        overviewId = objectActual.overview
                    )
                    decrementDataOrder(
                        orderId = orderId,
                        total = overviewResult
                    )
                }

                Action.UPDATE_STATUS_OVERVIEW -> {
                    objectService.updateStatusOverview(
                        orderId = orderId,
                        objectId = objectId,
                        overviewId = objectActual.overview,
                        status = objectActual.status
                    )
                }

                Action.REMOVE_OBJECT -> {
                    objectService.deleteObject(user = user, orderId = orderSaved.id, objectId = objectSaved.id)
                    decrementDataOrder(
                        orderId = orderId,
                        quantity = SUBTRACT_ONE,
                        total = objectSaved.total
                    )
                }
            }
        } else {
            throw ResourceNotFoundException(OBJECT_NOT_FOUND)
        }
    }

    @Transactional
    fun incrementMoreReservationsOrder(
        user: User,
        orderId: Long,
        reservationsToSava: MutableList<ReservationResponseVO>
    ) {
        val orderSaved = getOrder(user = user, orderId = orderId)
        val reservationsToSaved = reservationService.validateReservationsToSave(
            user = user,
            reservations = reservationsToSava,
            status = ReservationStatus.RESERVED
        )
        orderSaved.reservations?.addAll(reservationsToSaved)
    }

    @Transactional
    fun removeReservation(
        user: User,
        orderId: Long,
        reservationId: Long
    ) {
        val orderSaved = getOrder(orderId = orderId, user = user)
        val reservationExisting = orderSaved.reservations?.firstOrNull { it.id == reservationId }
        if (reservationExisting != null) {
            reservationService.removeReservationOrder(orderId = orderId, reservationId = reservationId)
            reservationService.updateStatusReservation(
                user = user,
                reservationId = reservationId,
                status = ReservationStatus.AVAILABLE
            )
        } else {
            throw ResourceNotFoundException(RESERVATION_NOT_FOUND)
        }
    }

    @Transactional
    fun incrementDataOrder(
        orderId: Long,
        total: Double? = 0.0
    ) {
        orderRepository.incrementDataOrder(orderId = orderId, total = total)
    }

    @Transactional
    fun decrementDataOrder(
        orderId: Long,
        quantity: Int? = 0,
        total: Double? = 0.0
    ) {
        orderRepository.decrementDataOrder(orderId = orderId, quantity = quantity, total = total)
    }

    @Transactional
    fun closeOrder(
        user: User,
        orderId: Long,
        force: Boolean = false,
        payment: PaymentRequestVO
    ): OrderResponseVO {
        val orderResult = getOrder(user = user, orderId = orderId)
        if (orderResult.status == Status.CLOSED) {
            throw ObjectDuplicateException(message = ORDER_ALREADY_CLOSED)
        } else {
            when (orderResult.type) {
                TypeOrder.RESERVATION -> {
                    orderResult.reservations?.forEach { reservation ->
                        reservation.status = ReservationStatus.AVAILABLE
                    }
                }

                else -> {}
            }
            if (force) {
                orderResult.objects?.map { objectSaved ->
                    objectSaved.overview?.map { overviewSaved ->
                        if (overviewSaved.status == ObjectStatus.PENDING) {
                            objectService.updateStatusOverview(
                                orderId = orderId,
                                objectId = objectSaved.id,
                                overviewId = overviewSaved.id,
                                status = ObjectStatus.DELIVERED
                            )
                        }
                    }
                }
            } else {
                orderResult.objects?.map { objectSaved ->
                    objectSaved.overview?.forEach { overviewSaved ->
                        if (overviewSaved.status == ObjectStatus.PENDING) {
                            throw InternalErrorClient(message = OBJECT_WITH_PENDING_DELIVERY)
                        }
                    }
                    if (objectSaved.status == ObjectStatus.PENDING) {
                        throw InternalErrorClient(message = OBJECT_WITH_PENDING_DELIVERY)
                    }
                }
            }
            updateStatusOrder(user = user, orderId = orderId, status = Status.CLOSED)
            orderResult.status = Status.CLOSED
            val valueFee = orderResult.fee?.percentage?.let { percentage ->
                (orderResult.total * percentage) / (100 + percentage)
            } ?: 0.0
            orderResult.payment = paymentService.savePayment(
                company = companyService.getCompanyByUserLogged(user = user),
                order = orderResult,
                payment = payment,
                fee = orderResult.fee != null,
                valueFee = valueFee,
                author = orderResult.fee?.author?.author ?: user.name,
                assigned = orderResult.fee?.author?.assigned ?: user.name
            )
            val orderResponse = parseObject(orderResult, OrderResponseVO::class.java)
            val qrCodeBytes =
                qrCodeService.generateQRCodeImage(value = orderResponse.payment?.code.toString(), 300, 300)
            orderResponse.qrCode = Base64.getEncoder().encodeToString(qrCodeBytes)
            orderResponse.fee = null
            return orderResponse
        }
    }

    @Transactional
    fun updateStatusOrder(
        user: User,
        orderId: Long,
        status: Status
    ) {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        orderRepository.updateStatusOrder(companyId = companySaved.id, orderId = orderId, status = status)
    }

    @Transactional
    fun updateAddressOrder(
        user: User,
        orderId: Long,
        addressId: Long,
        updateAddressRequestVO: UpdateAddressRequestVO
    ) {
        getOrder(user = user, orderId = orderId)
        addressService.updateAddress(addressId = addressId, updateAddressRequestVO = updateAddressRequestVO)
    }

    @Transactional
    fun updateStatusDelivery(
        user: User,
        orderId: Long,
        status: UpdateStatusDeliveryRequestVO
    ) {
        val orderSaved = getOrder(user = user, orderId = orderId)
        orderSaved.objects?.forEach {
            if (it.status == ObjectStatus.PENDING) {
                throw InternalErrorClient(message = OBJECT_WITH_PENDING_DELIVERY)
            }
        }
        addressService.updateStatusDelivery(addressId = orderSaved.address?.id ?: 0, status = status)
    }

    @Transactional
    fun deleteOrder(
        user: User,
        orderId: Long
    ) {
        val orderSaved: Order = getOrder(user = user, orderId = orderId)
        when (orderSaved.type) {
            TypeOrder.RESERVATION -> {
                if (orderSaved.fee?.author?.id != null) {
                    authorService.deleteAuthor(
                        authorId = orderSaved.fee?.author?.id ?: 0,
                        feeId = orderSaved.fee?.id ?: 0
                    )
                }
            }

            else -> {}
        }
        orderSaved.objects?.forEach { objectSaved ->
            when (objectSaved.type) {
                TypeItem.PRODUCT -> {
                    productService.restockProduct(
                        user = user,
                        productId = objectSaved.identifier,
                        RestockProductRequestVO(quantity = objectSaved.quantity)
                    )
                }

                else -> {}
            }
        }
        orderSaved.reservations?.forEach { reservation ->
            reservation.status = ReservationStatus.AVAILABLE
        }
        orderRepository.deleteOrderById(companyId = orderSaved.company?.id, orderId = orderSaved.id)
    }

    companion object {
        const val ORDER_NOT_FOUND = "Order not found!"
        const val DELIVERY_ORDER_PENDING = "Delivery order pending"
        const val SELECT_PAYMENT_TO_CLOSE_ORDER = "Select Payment to Close Order"
        const val ORDER_ALREADY_CLOSED = "Order already closed"
        const val SUBTRACT_ONE = 1
    }
}
