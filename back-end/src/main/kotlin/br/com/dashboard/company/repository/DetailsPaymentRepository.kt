package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.payment.DetailsPayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DetailsPaymentRepository: JpaRepository<DetailsPayment, Long> {

    @Query(value = "SELECT d FROM DetailsPayment d WHERE d.date = :date AND d.user.id = :userId")
    fun getAllDetailsPayments(
        @Param("date") date: LocalDate = LocalDate.now(),
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<DetailsPayment>?
}
