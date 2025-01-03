package br.com.dashboard.company.service

import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.entities.payment.Payment
import br.com.dashboard.company.repository.PaymentRepository
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.payment.PaymentRequestVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class PaymentService {

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    fun updatePayment(
        payment: PaymentRequestVO,
        order: Order
    ) {
        val paymentResult: Payment = parseObject(payment, Payment::class.java)
        paymentResult.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        paymentResult.type = payment.type
        paymentResult.total = order.total
        paymentResult.order = order
        paymentRepository.save(paymentResult)
    }
}
