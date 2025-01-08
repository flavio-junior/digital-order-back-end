package br.com.dashboard.company.service

import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.entities.payment.Payment
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.PaymentRepository
import br.com.dashboard.company.utils.common.PaymentType
import br.com.dashboard.company.utils.common.TypeOrder
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.checkout.GeneralBalanceResponseVO
import br.com.dashboard.company.vo.payment.AnalisePaymentVO
import br.com.dashboard.company.vo.payment.PaymentRequestVO
import br.com.dashboard.company.vo.payment.PaymentResponseVO
import br.com.dashboard.company.vo.payment.PaymentSummaryDTO
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

    fun updatePayment(
        payment: PaymentRequestVO,
        order: Order
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
        paymentResult.total = total
        paymentResult.order = order
        return paymentRepository.save(paymentResult)
    }

    @Transactional(readOnly = true)
    fun getAllPaymentsDay(
        pageable: Pageable
    ): Page<PaymentResponseVO> {
        val payments: Page<Payment>? =
            paymentRepository.getAllPaymentsDay(pageable = pageable)
        return payments?.map { payment ->
            parseObject(payment, PaymentResponseVO()::class.java)
        } ?: throw ResourceNotFoundException(message = PAYMENT_NOT_FOUND)
    }

    fun getAnalysisDay(date: LocalDate): AnalisePaymentVO {
        val paymentSummaries = paymentRepository.getAnalysisDay(date = date)
            .map {
                val typePayment = it[0] as PaymentType
                val typeOrder = it[1] as TypeOrder
                val count = it[2] as Long
                val total = it[3] as Double
                PaymentSummaryDTO(typeOrder = typeOrder, typePayment = typePayment, count = count, total = total)
            }
        val totalGeneral = paymentSummaries.sumOf { it.total }
        val totalDiscounts = paymentRepository.getAnalysisDay(date).sumOf { it[4] as Long }
        return AnalisePaymentVO(
            analise = paymentSummaries,
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
