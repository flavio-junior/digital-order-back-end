package br.com.dashboard.company.service

import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.utils.common.Status
import br.com.dashboard.company.vo.order.OrderRequestVO
import br.com.dashboard.company.vo.order.OrderResponseVO
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShoppingCartService {

    @Autowired
    private lateinit var orderService: OrderService

    @Transactional
    fun buyProducts(
        user: User,
        order: OrderRequestVO
    ): OrderResponseVO {
        return orderService.createNewOrder(
            user = user,
            status = Status.CLOSED,
            order = order
        )
    }
}
