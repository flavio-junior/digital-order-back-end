package br.com.dashboard.company.service

import br.com.dashboard.company.entities.fee.Fee
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ObjectDuplicateException
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.FeeRepository
import br.com.dashboard.company.service.DayService.Companion.DUPLICATE_DAY
import br.com.dashboard.company.utils.common.DayOfWeek
import br.com.dashboard.company.utils.common.Function
import br.com.dashboard.company.utils.common.PercentageRequestVO
import br.com.dashboard.company.utils.others.ConverterUtils.parseListObjects
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.day.DaysRequestVO
import br.com.dashboard.company.vo.fee.FeeRequestVO
import br.com.dashboard.company.vo.fee.FeeResponseVO
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
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = user.id)
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
                instancedFee.company = companyService.getCompanyByUserLogged(userLoggedId = user.id)
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
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = user.id)
        getFee(companyId = companySaved.id, feeId = feeId)
        feeRepository.updatePriceFee(companyId = companySaved.id, feeId = feeId, percentage = percentage.percentage)
    }

    @Transactional
    fun deleteFee(
        user: User,
        feeId: Long
    ) {
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = user.id)
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
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = user.id)
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
