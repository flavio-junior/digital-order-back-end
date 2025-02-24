package br.com.digital.order.repository

import br.com.digital.order.entities.address.Address
import br.com.digital.order.utils.common.AddressStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<Address, Long> {

    @Query(value = "SELECT a FROM Address a WHERE a.id = :addressId")
    fun findAddressById(
        @Param("addressId") addressId: Long
    ): Address?

    @Modifying
    @Query("UPDATE Address a SET a.status =:status WHERE a.id = :addressId")
    fun updateStatusDelivery(
        @Param("addressId") addressId: Long,
        @Param("status") status: AddressStatus
    )
}
