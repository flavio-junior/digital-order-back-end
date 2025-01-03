package br.com.dashboard.company.service

import br.com.dashboard.company.entities.address.Address
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.AddressRepository
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.address.AddressRequestVO
import br.com.dashboard.company.vo.address.UpdateAddressRequestVO
import br.com.dashboard.company.vo.order.UpdateStatusDeliveryRequestVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AddressService {

    @Autowired
    private lateinit var repository: AddressRepository

    @Transactional(readOnly = true)
    fun findAddressById(
        addressId: Long
    ): Address {
        val orderSaved: Address? = repository.findAddressById(addressId = addressId)
        if (orderSaved != null) {
            return orderSaved
        } else {
            throw ResourceNotFoundException(ADDRESS_NOT_FOUND)
        }
    }

    @Transactional
    fun saveAddress(
        addressRequestVO: AddressRequestVO
    ): Address {
        val orderResult: Address = parseObject(addressRequestVO, Address::class.java)
        return repository.save(orderResult)
    }

    @Transactional
    fun updateAddress(
        addressId: Long,
        updateAddressRequestVO: UpdateAddressRequestVO
    ) {
       val addressSaved = findAddressById(addressId = addressId)
        addressSaved.street = updateAddressRequestVO.street
        addressSaved.number = updateAddressRequestVO.number
        addressSaved.district = updateAddressRequestVO.district
        addressSaved.complement = updateAddressRequestVO.complement
        repository.save(addressSaved)
    }

    @Transactional
    fun updateStatusDelivery(
        addressId: Long,
        status: UpdateStatusDeliveryRequestVO
    ) {
        findAddressById(addressId = addressId)
        repository.updateStatusDelivery(addressId = addressId, status = status.status)
    }

    companion object {
        const val ADDRESS_NOT_FOUND = "Address not found!"
    }
}
