package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.utils.common.Status
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {

    @Query("SELECT o from Order o WHERE o.company.id = :companyId AND o.status =:status")
    fun findAllOrdersOpen(
        @Param("companyId") companyId: Long? = null,
        @Param("status") status: Status,
        pageable: Pageable
    ): Page<Order>?

    @Modifying
    @Query("UPDATE Order o SET o.status =:status WHERE o.company.id = :companyId AND o.id =:orderId")
    fun updateStatusOrder(
        @Param("companyId") companyId: Long? = null,
        @Param("orderId") orderId: Long,
        @Param("status") status: Status
    )

    @Query(value = "SELECT o FROM Order o WHERE o.company.id = :companyId AND o.id = :orderId")
    fun findOrderById(
        @Param("companyId") companyId: Long? = null,
        @Param("orderId") orderId: Long
    ): Order?

    @Query(value = "SELECT o FROM Order o WHERE o.payment.code = :code")
    fun findOrderByCodePayment(
        @Param("code") code: Long
    ): Order?

    @Modifying
    @Query("UPDATE Order o SET o.quantity = o.quantity + :quantity WHERE o.id = :orderId")
    fun updateQuantityOrder(
        @Param("orderId") orderId: Long,
        @Param("quantity") quantity: Int? = 0
    )

    @Modifying
    @Query("UPDATE Order o SET o.total = o.total + :total WHERE o.id = :orderId")
    fun incrementDataOrder(
        @Param("orderId") orderId: Long,
        @Param("total") total: Double? = 0.0
    )

    @Modifying
    @Query("UPDATE Order o SET o.quantity = o.quantity - :quantity, o.total = o.total - :total WHERE o.id = :orderId")
    fun decrementDataOrder(
        @Param("orderId") orderId: Long,
        @Param("quantity") quantity: Int? = 0,
        @Param("total") total: Double? = 0.0
    )

    @Modifying
    @Query(value = "DELETE FROM Order o WHERE o.id = :orderId AND o.company.id = :companyId")
    fun deleteOrderById(
        @Param("companyId") companyId: Long? = null,
        @Param("orderId") orderId: Long
    ): Int
}
