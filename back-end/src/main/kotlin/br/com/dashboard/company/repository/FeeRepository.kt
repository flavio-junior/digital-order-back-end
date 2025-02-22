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

    @Query(value = "SELECT f FROM Fee f WHERE f.company.id = :companyId")
    fun findAllFees(
        @Param("companyId") companyId: Long? = null
    ): List<Fee>

    @Query(value = "SELECT f FROM Fee f WHERE f.company.id = :companyId AND f.id = :feeId")
    fun findFeeById(
        @Param("companyId") companyId: Long? = null,
        @Param("feeId") feeId: Long
    ): Fee?

    @Query(value = "SELECT f FROM Fee f WHERE f.company.id = :companyId AND f.assigned = :assigned")
    fun findFeeByAssigned(
        @Param("companyId") companyId: Long? = null,
        @Param("assigned") assigned: Function
    ): Fee?

    @Query("SELECT f FROM Fee f WHERE f.company.id = :companyId AND f.assigned = :assigned")
    fun checkFeeAlreadyExists(
        @Param("companyId") companyId: Long? = null,
        @Param("assigned") assigned: Function
    ): Fee?

    @Modifying
    @Query("UPDATE Fee f SET f.percentage =:percentage WHERE f.company.id = :companyId AND f.id = :feeId")
    fun updatePriceFee(
        @Param("companyId") companyId: Long? = null,
        @Param("feeId") feeId: Long,
        @Param("percentage") percentage: Int
    )

    @Modifying
    @Query(value = "DELETE FROM Fee f WHERE f.id = :feeId AND f.company.id = :companyId")
    fun deleteFeeById(
        @Param("companyId") companyId: Long? = null,
        @Param("feeId") feeId: Long
    ): Int
}
