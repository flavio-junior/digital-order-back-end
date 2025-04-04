package br.com.digital.order.service

import br.com.digital.order.entities.company.Company
import br.com.digital.order.entities.order.Order
import br.com.digital.order.entities.payment.Payment
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.repository.PaymentRepository
import br.com.digital.order.utils.common.PaymentType
import br.com.digital.order.utils.common.TypeOrder
import br.com.digital.order.utils.others.ConverterUtils.parseObject
import br.com.digital.order.utils.others.TypeAnalysis
import br.com.digital.order.vo.payment.*
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

    @Autowired
    private lateinit var companyService: CompanyService

    fun savePayment(
        company: Company? = null,
        order: Order,
        payment: PaymentRequestVO? = null,
        fee: Boolean = false,
        valueFee: Double? = 0.0,
        author: String,
        assigned: String
    ): Payment {
        val paymentResult: Payment = parseObject(payment, Payment::class.java)
        var total: Double = order.total
        paymentResult.date = LocalDate.now()
        paymentResult.hour = LocalTime.now().withNano(0)
        paymentResult.code = System.currentTimeMillis()
        paymentResult.author = author
        paymentResult.assigned = assigned
        paymentResult.typeOrder = order.type
        paymentResult.typePayment = payment?.type
        if (payment?.discount == true) {
            total -= payment.value ?: 0.0
        }
        paymentResult.discount = payment?.discount
        paymentResult.valueDiscount = payment?.value
        if (payment?.remove == true) {
            paymentResult.fee = false
            paymentResult.valueFee = 0.0
            if (valueFee != null) {
                val newTotalCalculated: Double = total - valueFee
                paymentResult.total = newTotalCalculated
            }
        } else {
            paymentResult.fee = fee
            paymentResult.valueFee = valueFee
            paymentResult.total = total
        }
        paymentResult.order = order
        paymentResult.company = company
        return paymentRepository.save(paymentResult)
    }

    @Transactional(readOnly = true)
    fun getAllPaymentsDay(
        user: User,
        code: Long?,
        pageable: Pageable
    ): Page<PaymentResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val payments: Page<Payment>? =
            paymentRepository.getAllPaymentsDay(companyId = companySaved.id, code = code, pageable = pageable)
        return payments?.map { payment ->
            parseObject(payment, PaymentResponseVO()::class.java)
        } ?: throw ResourceNotFoundException(message = PAYMENT_NOT_FOUND)
    }

    @Transactional
    fun getDetailsAnalysis(
        user: User,
        date: LocalDate?,
        start: LocalDate?,
        end: LocalDate?,
        type: TypeAnalysis
    ): AnaliseDayVO {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        return when (type) {
            TypeAnalysis.DAY -> {
                if (start != null && end != null) {
                    getAnalysisDayBetweenDays(companyId = companySaved.id, start = start, end = end)
                } else {
                    getAnalysisDay(user = user, date = date)
                }
            }

            TypeAnalysis.WEEK -> getAnalysisOfWeek(companyId = companySaved.id)
            TypeAnalysis.MONTH -> getAnalysisMonth(companyId = companySaved.id)
            TypeAnalysis.YEAR -> getAnalysisYear(companyId = companySaved.id)
        }
    }

    private fun getAnalysisDay(
        user: User,
        date: LocalDate? = null
    ): AnaliseDayVO {
        return converterAnaliseDay(
            analiseDay = paymentRepository.getAnalysisDay(
                companyId = user.id,
                date = date
            )
        )
    }

    private fun getAnalysisDayBetweenDays(
        companyId: Long? = null,
        start: LocalDate,
        end: LocalDate
    ): AnaliseDayVO {
        return converterAnaliseDay(
            analiseDay = paymentRepository.getAnalysisDayBetweenDays(
                companyId = companyId,
                start = start,
                end = end
            )
        )
    }

    @Transactional
    fun getAnalysisOfWeek(
        companyId: Long? = null
    ): AnaliseDayVO {
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        return converterAnaliseDay(
            analiseDay = paymentRepository.getAnalysisOfWeek(
                companyId = companyId,
                startDate = startDate,
                endDate = endDate
            )
        )
    }

    @Transactional
    fun getAnalysisMonth(
        companyId: Long? = null
    ): AnaliseDayVO {
        return converterAnaliseDay(analiseDay = paymentRepository.getAnalysisMonth(companyId = companyId))
    }

    @Transactional
    fun getAnalysisYear(
        companyId: Long? = null
    ): AnaliseDayVO {
        return converterAnaliseDay(analiseDay = paymentRepository.getAnalysisYear(companyId = companyId))
    }

    private fun converterAnaliseDay(
        analiseDay: List<Array<Any>>
    ): AnaliseDayVO {
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

    companion object {
        const val PAYMENT_NOT_FOUND = "Payment not found"
    }
}
