package br.com.dashboard.company.service

import br.com.dashboard.company.entities.checkout.Checkout
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.CheckoutRepository
import br.com.dashboard.company.utils.common.TypeOrder
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.checkout.CheckoutResponseVO
import br.com.dashboard.company.vo.checkout.GeneralBalanceResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CheckoutService {

    @Autowired
    private lateinit var checkoutRepository: CheckoutRepository

    @Transactional(readOnly = true)
    fun getAllCheckoutsDay(
        pageable: Pageable
    ): Page<CheckoutResponseVO> {
        val checkouts: Page<Checkout>? =
            checkoutRepository.getAllCheckoutsDay(pageable = pageable)
        return checkouts?.map { checkout ->
            parseObject(checkout, CheckoutResponseVO()::class.java)
        } ?: throw ResourceNotFoundException(message = CHECKOUT_NOT_FOUND)
    }

    @Transactional
    fun saveCheckoutDetails(
        total: Double,
        type: TypeOrder
    ) {
        checkoutRepository.save(Checkout(date = LocalDate.now(), total = total, type = type))
    }

    @Transactional
    fun getGeneralBalance(): GeneralBalanceResponseVO {
        return GeneralBalanceResponseVO(total = checkoutRepository.getGeneralBalance())
    }

    @Transactional
    fun getBalanceLast7Days(): GeneralBalanceResponseVO {
        return GeneralBalanceResponseVO(total = checkoutRepository.findBalanceLast7DaysNative())
    }

    @Transactional
    fun getBalanceCurrentMonth(): GeneralBalanceResponseVO {
        return GeneralBalanceResponseVO(total = checkoutRepository.findBalanceCurrentMonthNative())
    }

    @Transactional
    fun getBalanceCurrentYear(): GeneralBalanceResponseVO {
        return GeneralBalanceResponseVO(total = checkoutRepository.findBalanceCurrentYearNative())
    }

    companion object {
        const val CHECKOUT_NOT_FOUND = "Checkout not found"
    }
}
