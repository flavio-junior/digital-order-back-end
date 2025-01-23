package br.com.dashboard.company.service

import br.com.dashboard.company.entities.`object`.Object
import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.ObjectRepository
import br.com.dashboard.company.utils.common.ObjectStatus
import br.com.dashboard.company.utils.common.TypeItem
import br.com.dashboard.company.vo.`object`.ObjectRequestVO
import br.com.dashboard.company.vo.product.RestockProductRequestVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ObjectService {

    @Autowired
    private lateinit var objectRepository: ObjectRepository

    @Autowired
    private lateinit var overviewService: OverviewService

    @Autowired
    private lateinit var itemService: ItemService

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var foodService: FoodService

    @Autowired
    private lateinit var userService: UserService

    @Transactional
    fun saveObjects(
        userId: Long,
        order: Order? = null,
        buy: Boolean = false,
        objectsToSave: MutableList<ObjectRequestVO>? = null
    ): Pair<MutableList<Object>?, Double> {
        var total = 0.0
        val userAuthenticated = userService.findUserById(userId = userId)
        val result = objectsToSave?.map { item ->
            when (item.type) {
                TypeItem.FOOD -> {
                    val objectSaved = foodService.saveObjectFood(
                        user = userAuthenticated,
                        order = order,
                        foodRequest = item,
                    )
                    val overview = overviewService.saveOverview(
                        status = objectSaved.first.status,
                        quantity = objectSaved.first.quantity
                    )
                    objectSaved.first.overview = arrayListOf(overview)
                    total += objectSaved.second
                    objectRepository.save(objectSaved.first)
                }

                TypeItem.ITEM -> {
                    val objectSaved = itemService.saveObjectItem(
                        user = userAuthenticated,
                        order = order,
                        itemRequest = item,
                    )
                    val overview = overviewService.saveOverview(
                        status = objectSaved.first.status,
                        quantity = objectSaved.first.quantity
                    )
                    objectSaved.first.overview = arrayListOf(overview)
                    total += objectSaved.second
                    objectRepository.save(objectSaved.first)
                }

                else -> {
                    val objectProductInstanced = productService.buyProduct(
                        user = userAuthenticated,
                        order = order,
                        buy = buy,
                        productRequest = item
                    )
                    val overview = overviewService.saveOverview(
                        status = objectProductInstanced.first.status,
                        quantity = objectProductInstanced.first.quantity
                    )
                    objectProductInstanced.first.overview = arrayListOf(overview)
                    total += objectProductInstanced.second
                    objectRepository.save(objectProductInstanced.first)
                }
            }
        }
        return Pair(first = result?.toMutableList(), second = total)
    }

    fun getObject(
        orderId: Long,
        objectId: Long
    ): Object {
        val objectSalved: Object? = objectRepository.findObjectById(orderId = orderId, objectId = objectId)
        if (objectSalved != null) {
            return objectSalved
        } else {
            throw ResourceNotFoundException(OBJECT_NOT_FOUND)
        }
    }

    @Transactional
    fun updateStatusObject(
        orderId: Long,
        objectId: Long,
        status: ObjectStatus? = null
    ) {
        objectRepository.updateStatusObject(
            orderId = orderId,
            objectId = objectId,
            status = status
        )
    }

    @Transactional
    fun incrementMoreItemsObject(
        orderId: Long,
        objectId: Long,
        quantity: Int,
        total: Double,
        objectResult: Object? = null
    ) {
        objectRepository.incrementMoreItemsObject(
            orderId = orderId,
            objectId = objectId,
            quantity = quantity,
            total = total
        )
        overviewService.saveOverview(status = ObjectStatus.PENDING, quantity = quantity, objectResult = objectResult)
    }

    @Transactional
    fun removeOverview(
        user: User,
        orderId: Long,
        objectId: Long,
        overviewId: Long
    ): Double {
        val objectSaved = getObject(orderId = orderId, objectId = objectId)
        val overviewSaved = overviewService.getOverview(objectId = objectId, overviewId = overviewId)
        if (objectSaved.type == TypeItem.PRODUCT) {
            productService.restockProduct(
                user = user,
                productId = objectSaved.identifier,
                restockProduct = RestockProductRequestVO(quantity = objectSaved.quantity)
            )
        }
        val newQuantityToSave = (objectSaved.quantity - overviewSaved.quantity)
        val priceCalculated = (objectSaved.price * overviewSaved.quantity)
        decrementItemsObject(
            orderId = orderId,
            objectId = objectId,
            quantity = newQuantityToSave,
            total = priceCalculated
        )
        overviewService.deleteOverview(objectId = objectId, overviewId = overviewId)
        return priceCalculated
    }

    @Transactional
    fun decrementItemsObject(
        orderId: Long,
        objectId: Long,
        quantity: Int,
        total: Double
    ) {
        objectRepository.decrementItemsObject(
            orderId = orderId,
            objectId = objectId,
            quantity = quantity,
            total = total
        )
    }

    @Transactional
    fun updateStatusOverview(
        orderId: Long,
        objectId: Long,
        overviewId: Long,
        status: ObjectStatus? = null
    ) {
        getObject(orderId = orderId, objectId = objectId)
        overviewService.updateStatusOverview(objectId = objectId, overviewId = overviewId, status = status)
    }

    @Transactional
    fun deleteObject(
        user: User,
        orderId: Long,
        objectId: Long
    ) {
        val objectSaved = getObject(orderId = orderId, objectId = objectId)
        if (objectSaved.type == TypeItem.PRODUCT) {
            productService.restockProduct(
                user = user,
                productId = objectSaved.identifier,
                restockProduct = RestockProductRequestVO(quantity = objectSaved.quantity)
            )
        }
        objectRepository.deleteObjectById(orderId = orderId, objectId = objectId)
    }

    companion object {
        const val OBJECT_NOT_FOUND = "Object not found!"
        const val OBJECT_ALREADY_EXISTS = "Object already exists!"
        const val OBJECT_WITH_PENDING_DELIVERY = "Object with pending delivery!"
    }
}
