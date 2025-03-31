package br.com.digital.order.service

import br.com.digital.order.entities.fee.Fee
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.ObjectDuplicateException
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.repository.FeeRepository
import br.com.digital.order.service.DayService.Companion.DUPLICATE_DAY
import br.com.digital.order.utils.common.DayOfWeek
import br.com.digital.order.utils.common.Function
import br.com.digital.order.utils.common.PercentageRequestVO
import br.com.digital.order.utils.others.ConverterUtils.parseListObjects
import br.com.digital.order.utils.others.ConverterUtils.parseObject
import br.com.digital.order.vo.day.DaysRequestVO
import br.com.digital.order.vo.fee.FeeRequestVO
import br.com.digital.order.vo.fee.FeeResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeeService {

    @Autowired
    private lateinit var feeRepository: FeeRepository

    @Autowired
    private lateinit var companyService: CompanyService

    @Autowired
    private lateinit var dayService: DayService

    @Transactional(readOnly = true)
    fun findAllFees(
        user: User
    ): List<FeeResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val fees = feeRepository.findAllFees(companyId = companySaved.id)
        return parseListObjects(fees, FeeResponseVO::class.java)
    }

    fun getFeeByType(
        companyId: Long? = null,
        assigned: Function
    ): Fee? {
        return feeRepository.findFeeByAssigned(companyId = companyId, assigned = assigned)
    }

    fun getFee(
        companyId: Long? = null,
        feeId: Long
    ): Fee {
        val feeSaved: Fee? = feeRepository.findFeeById(companyId = companyId, feeId = feeId)
        if (feeSaved != null) {
            return feeSaved
        } else {
            throw ResourceNotFoundException(message = FEE_NOT_FOUND)
        }
    }

    @Transactional
    fun createNewFee(
        user: User,
        fee: FeeRequestVO
    ) {
        fee.assigned?.let {
            if (checkNameFeeAlreadyExists(companyId = user.id, assigned = it)) {
                throw ObjectDuplicateException(message = DUPLICATE_NAME_FEE)
            } else {
                val instancedFee: Fee = parseObject(origin = fee, destination = Fee::class.java)
                instancedFee.company = companyService.getCompanyByUserLogged(user = user)
                feeRepository.save(instancedFee)
            }
        }
    }

    private fun checkNameFeeAlreadyExists(
        companyId: Long,
        assigned: Function
    ): Boolean {
        val productResult = feeRepository.checkFeeAlreadyExists(companyId = companyId, assigned = assigned)
        return productResult != null
    }

    @Transactional
    fun addDaysFee(
        user: User,
        feeId: Long,
        days: DaysRequestVO
    ) {
        val feeSaved = getFee(companyId = user.id, feeId = feeId)
        feeSaved.days?.filter { day ->
            if (day.day == DayOfWeek.ALL) {
                throw ObjectDuplicateException(message = DUPLICATE_DAY)
            } else {
                false
            }
        }
        val validDays = days.days?.let { daysInstanced ->
            if (DayOfWeek.ALL in daysInstanced) {
                feeSaved.days?.forEach { day ->
                    deleteDayFee(user = user, feeId = feeId, dayId = day.id)
                }
                listOf(DayOfWeek.ALL)
            } else {
                daysInstanced
            }
        } ?: emptyList()
        validDays.forEach { day ->
            dayService.saveDay(fee = feeSaved, day = day)
        }
    }

    @Transactional
    fun updatePriceFee(
        user: User,
        feeId: Long,
        percentage: PercentageRequestVO
    ) {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        getFee(companyId = companySaved.id, feeId = feeId)
        feeRepository.updatePriceFee(companyId = companySaved.id, feeId = feeId, percentage = percentage.percentage)
    }

    @Transactional
    fun deleteFee(
        user: User,
        feeId: Long
    ) {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val feeSaved = getFee(companyId = companySaved.id, feeId = feeId)
        feeSaved.days?.forEach { day ->
            dayService.deleteDay(feeId = feeId, dayId = day.id)
        }
        feeRepository.deleteFeeById(companyId = companySaved.id, feeId = feeId)
    }

    @Transactional
    fun deleteDayFee(
        user: User,
        feeId: Long,
        dayId: Long
    ) {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val feeSaved = getFee(companyId = companySaved.id, feeId = feeId)
        val check = feeSaved.days?.filter { day ->
            if (day.id == dayId) {
                return@filter true
            } else {
                false
            }
        }
        if (check != null) {
            dayService.deleteDay(feeId = feeId, dayId = dayId)
        } else {
            throw ResourceNotFoundException(message = FEE_NOT_FOUND)
        }
    }

    companion object {
        const val DUPLICATE_NAME_FEE = "The fee already exists"
        const val FEE_NOT_FOUND = "Fee not found"
    }
}
