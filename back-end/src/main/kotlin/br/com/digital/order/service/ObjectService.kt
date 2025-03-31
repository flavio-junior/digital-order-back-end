package br.com.digital.order.service

import br.com.digital.order.entities.`object`.Object
import br.com.digital.order.entities.order.Order
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.repository.ObjectRepository
import br.com.digital.order.utils.common.ObjectStatus
import br.com.digital.order.utils.common.TypeItem
import br.com.digital.order.vo.`object`.ObjectRequestVO
import br.com.digital.order.vo.product.RestockProductRequestVO
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

    @Transactional
    fun saveObjects(
        user: User,
        order: Order? = null,
        buy: Boolean = false,
        objectsToSave: MutableList<ObjectRequestVO>? = null
    ): Pair<MutableList<Object>?, Double> {
        var total = 0.0
        val result = objectsToSave?.map { item ->
            when (item.type) {
                TypeItem.FOOD -> {
                    val objectSaved = foodService.saveObjectFood(
                        user = user,
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
                        user = user,
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
                        user = user,
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
        objectId: Long
    ) {
        val updatedObject = getObject(orderId = orderId, objectId = objectId)
        val allDelivered = updatedObject.overview?.all { it.status == ObjectStatus.DELIVERED } ?: false
        if (allDelivered) {
            objectRepository.updateStatusObject(
                orderId = orderId,
                objectId = objectId,
                status = ObjectStatus.DELIVERED
            )
        } else {
            objectRepository.updateStatusObject(
                orderId = orderId,
                objectId = objectId,
                status = ObjectStatus.PENDING
            )
        }
    }

    @Transactional
    fun incrementMoreItemsObject(
        user: User,
        orderId: Long,
        objectId: Long,
        quantity: Int,
        total: Double,
        objectResult: Object? = null
    ) {
        val objectSaved = getObject(orderId = orderId, objectId = objectId)
        if (objectSaved.type == TypeItem.PRODUCT) {
            productService.decrementProduct(user = user, objectSaved.identifier, quantity = quantity)
        }
        objectRepository.incrementMoreItemsObject(
            orderId = orderId,
            objectId = objectId,
            quantity = quantity,
            total = total
        )
        overviewService.saveOverview(status = ObjectStatus.PENDING, quantity = quantity, objectResult = objectResult)
        updateStatusObject(orderId = orderId, objectId = objectId)
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
                restockProduct = RestockProductRequestVO(quantity = overviewSaved.quantity)
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
        updateStatusObject(orderId = orderId, objectId = objectId)
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
        updateStatusObject(orderId = orderId, objectId = objectId)
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
