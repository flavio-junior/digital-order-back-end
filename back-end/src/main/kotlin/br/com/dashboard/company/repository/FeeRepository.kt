package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.fee.Fee
import br.com.dashboard.company.utils.common.Function
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FeeRepository : JpaRepository<Fee, Long> {

    @Query(value = "SELECT f FROM Fee f WHERE f.user.id = :userId")
    fun findAllFees(
        @Param("userId") userId: Long
    ): List<Fee>

    @Query(value = "SELECT f FROM Fee f WHERE f.user.id = :userId AND f.id = :feeId")
    fun findFeeById(
        @Param("userId") userId: Long,
        @Param("feeId") feeId: Long
    ): Fee?

    @Query("SELECT f FROM Fee f WHERE f.user.id = :userId AND f.assigned = :assigned")
    fun checkFeeAlreadyExists(
        @Param("userId") userId: Long,
        @Param("assigned") assigned: Function
    ): Fee?

    @Modifying
    @Query("UPDATE Fee f SET f.price =:price WHERE f.user.id = :userId AND f.id = :feeId")
    fun updatePriceFee(
        @Param("userId") userId: Long,
        @Param("feeId") feeId: Long,
        @Param("price") price: Double
    )

    @Modifying
    @Query(value = "DELETE FROM Fee f WHERE f.id = :feeId AND f.user.id = :userId")
    fun deleteFeeById(
        @Param("userId") userId: Long,
        @Param("feeId") feeId: Long
    ): Int
}
