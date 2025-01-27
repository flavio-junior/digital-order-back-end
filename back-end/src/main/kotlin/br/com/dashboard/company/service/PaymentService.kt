package br.com.dashboard.company.service

import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.entities.payment.DetailsPayment
import br.com.dashboard.company.entities.payment.Payment
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.PaymentRepository
import br.com.dashboard.company.utils.common.PaymentType
import br.com.dashboard.company.utils.common.TypeOrder
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.checkout.GeneralBalanceResponseVO
import br.com.dashboard.company.vo.payment.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class PaymentService {

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    fun savePayment(
        user: User? = null,
        order: Order,
        payment: PaymentRequestVO,
        fee: Boolean,
        valueFee: Double?,
        author: String,
        assigned: String,
        identifier: Long
    ): Payment {
        val paymentResult: Payment = parseObject(payment, Payment::class.java)
        var total: Double = order.total
        paymentResult.date = LocalDate.now()
        paymentResult.hour = LocalTime.now().withNano(0)
        paymentResult.code = System.currentTimeMillis()
        paymentResult.typeOrder = order.type
        paymentResult.typePayment = payment.type
        if (payment.discount == true) {
            total -= payment.value ?: 0.0
        }
        paymentResult.discount = payment.discount
        paymentResult.valueDiscount = payment.value
        paymentResult.fee = fee
        paymentResult.valueFee = valueFee
        paymentResult.total = total
        paymentResult.details = DetailsPayment(
            author = author,
            assigned = assigned,
            date = LocalDate.now(),
            hour = LocalTime.now().withNano(0),
            value = paymentResult.total,
            identifier = identifier,
            user = user
        )
        paymentResult.order = order
        paymentResult.user = user
        return paymentRepository.save(paymentResult)
    }

    @Transactional(readOnly = true)
    fun getAllPaymentsDay(
        user: User,
        pageable: Pageable
    ): Page<PaymentResponseVO> {
        val payments: Page<Payment>? =
            paymentRepository.getAllPaymentsDay(userId = user.id, pageable = pageable)
        return payments?.map { payment ->
            parseObject(payment, PaymentResponseVO()::class.java)
        } ?: throw ResourceNotFoundException(message = PAYMENT_NOT_FOUND)
    }

    fun getAnalysisDay(
        user: User,
        date: String
    ): AnaliseDayVO {
        val analiseDay = paymentRepository.getAnalysisDay(userId = user.id, date = LocalDate.parse(date))
        val filterWithTypesPayments = mutableMapOf<Pair<TypeOrder, PaymentType>, DescriptionPaymentVO>()
        analiseDay.forEach {
            val typePayment = it[0] as PaymentType
            val typeOrder = it[1] as TypeOrder
            val numberItems = it[2] as Long
            val total = it[3] as Double
            val discountsCount = it[4] as Long
            val key = Pair(typeOrder, typePayment)
            filterWithTypesPayments[key] = filterWithTypesPayments.getOrDefault(
                key, DescriptionPaymentVO(
                    typeOrder = typeOrder,
                    typePayment = typePayment,
                    numberItems = 0,
                    total = 0.0,
                    discount = 0
                )
            ).let { existing ->
                existing.copy(
                    numberItems = existing.numberItems + numberItems,
                    total = existing.total + total,
                    discount = existing.discount + discountsCount
                )
            }
        }
        val groupedByTypeOrder = filterWithTypesPayments.values.groupBy { it.typeOrder }
        val typePaymentVOs = groupedByTypeOrder.map { (typeOrder, summaries) ->
            val totalOrders = summaries.sumOf { it.numberItems }
            val totalAmount = summaries.sumOf { it.total }
            val totalDiscounts = summaries.sumOf { it.discount }
            TypePaymentVO(
                typeOrder = typeOrder,
                analise = summaries,
                numberOrders = totalOrders,
                total = totalAmount,
                discount = totalDiscounts
            )
        }
        val totalGeneral = typePaymentVOs.sumOf { it.total }
        val totalOrders = typePaymentVOs.sumOf { it.numberOrders }
        val totalDiscounts = typePaymentVOs.sumOf { it.discount }
        return AnaliseDayVO(
            content = typePaymentVOs,
            numberOrders = totalOrders,
            total = totalGeneral,
            discount = totalDiscounts
        )
    }

    @Transactional
    fun getBalanceLast7Days(): GeneralBalanceResponseVO {
        return GeneralBalanceResponseVO(total = paymentRepository.findBalanceLast7DaysNative())
    }

    @Transactional
    fun getBalanceCurrentMonth(): GeneralBalanceResponseVO {
        return GeneralBalanceResponseVO(total = paymentRepository.findBalanceCurrentMonthNative())
    }

    @Transactional
    fun getBalanceCurrentYear(): GeneralBalanceResponseVO {
        return GeneralBalanceResponseVO(total = paymentRepository.findBalanceCurrentYearNative())
    }

    companion object {
        const val PAYMENT_NOT_FOUND = "Payment not found"
    }
}
