package br.com.dashboard.company.service

import br.com.dashboard.company.entities.fee.Fee
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ObjectDuplicateException
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.FeeRepository
import br.com.dashboard.company.utils.common.Function
import br.com.dashboard.company.utils.common.PriceRequestVO
import br.com.dashboard.company.utils.others.ConverterUtils.parseListObjects
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
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
    private lateinit var userService: UserService

    @Transactional(readOnly = true)
    fun findAllFees(
        user: User
    ): List<FeeResponseVO> {
        val fees = feeRepository.findAllFees(userId = user.id)
        return parseListObjects(fees, FeeResponseVO::class.java)
    }

    fun getFee(
        userId: Long,
        feeId: Long
    ): Fee {
        val feeSaved: Fee? = feeRepository.findFeeById(userId = userId, feeId = feeId)
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
            if (checkNameFeeAlreadyExists(userId = user.id, assigned = it)) {
                throw ObjectDuplicateException(message = DUPLICATE_NAME_FEE)
            } else {
                val instancedFee: Fee = parseObject(origin = fee, destination = Fee::class.java)
                instancedFee.user = userService.findUserById(userId = user.id)
                feeRepository.save(instancedFee)
            }
        }
    }

    private fun checkNameFeeAlreadyExists(
        userId: Long,
        assigned: Function
    ): Boolean {
        val productResult = feeRepository.checkFeeAlreadyExists(userId = userId, assigned = assigned)
        return productResult != null
    }

    @Transactional
    fun updatePriceFee(
        user: User,
        feeId: Long,
        price: PriceRequestVO
    ) {
        getFee(userId = user.id, feeId = feeId)
        feeRepository.updatePriceFee(userId = user.id, feeId = feeId, price = price.price)
    }

    @Transactional
    fun deleteFee(
        user: User,
        feeId: Long
    ) {
        getFee(userId = user.id, feeId = feeId)
        feeRepository.deleteFeeById(userId = user.id, feeId = feeId)
    }

    companion object {
        const val DUPLICATE_NAME_FEE = "The fee already exists"
        const val FEE_NOT_FOUND = "Fee not found"
    }
}
