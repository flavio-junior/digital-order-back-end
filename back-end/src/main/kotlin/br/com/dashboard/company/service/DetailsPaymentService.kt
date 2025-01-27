package br.com.dashboard.company.service

import br.com.dashboard.company.entities.payment.DetailsPayment
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.DetailsPaymentRepository
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.payment.DetailsPaymentResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DetailsPaymentService {

    @Autowired
    private lateinit var detailsPaymentRepository: DetailsPaymentRepository

    @Transactional(readOnly = true)
    fun getAllDetailsPaymentsDay(
        user: User,
        pageable: Pageable
    ): Page<DetailsPaymentResponseVO> {
        val detailsPayments: Page<DetailsPayment>? =
            detailsPaymentRepository.getAllDetailsPayments(userId = user.id, pageable = pageable)
        return detailsPayments?.map { detailsPayment ->
            parseObject(detailsPayment, DetailsPaymentResponseVO()::class.java)
        } ?: throw ResourceNotFoundException(message = DETAILS_PAYMENT_NOT_FOUND)
    }

    companion object {
        const val DETAILS_PAYMENT_NOT_FOUND = "Details of payment not found!"
    }
}
