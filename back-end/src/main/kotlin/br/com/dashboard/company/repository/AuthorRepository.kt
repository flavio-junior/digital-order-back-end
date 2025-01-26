package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.fee.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : JpaRepository<Author, Long> {

    @Query(value = "SELECT a FROM Author a WHERE a.id = :authorId AND a.fee.id = :feeId")
    fun findFeeById(
        @Param("authorId") authorId: Long,
        @Param("feeId") feeId: Long
    ): Author?

    @Modifying
    @Query(value = "DELETE FROM Author a WHERE a.id = :authorId AND a.fee.id = :feeId")
    fun deleteFeeById(
        @Param("authorId") authorId: Long,
        @Param("feeId") feeId: Long
    ): Int
}
