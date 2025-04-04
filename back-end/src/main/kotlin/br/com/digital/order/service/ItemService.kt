package br.com.digital.order.service

import br.com.digital.order.entities.item.Item
import br.com.digital.order.entities.`object`.Object
import br.com.digital.order.entities.order.Order
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.ObjectDuplicateException
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.repository.ItemRepository
import br.com.digital.order.utils.common.ObjectStatus
import br.com.digital.order.utils.common.PriceRequestVO
import br.com.digital.order.utils.others.ConverterUtils.parseObject
import br.com.digital.order.vo.item.ItemRequestVO
import br.com.digital.order.vo.item.ItemResponseVO
import br.com.digital.order.vo.`object`.ObjectRequestVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService {

    @Autowired
    private lateinit var itemRepository: ItemRepository

    @Autowired
    private lateinit var companyService: CompanyService

    @Transactional(readOnly = true)
    fun findAllItems(
        user: User,
        name: String?,
        pageable: Pageable
    ): Page<ItemResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val items: Page<Item>? = itemRepository.findAllItems(companyId = companySaved.id, name = name, pageable = pageable)
        return items?.map { reservation -> parseObject(reservation, ItemResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = ITEM_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findItemByName(
        user: User,
        name: String
    ): List<ItemResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val items: List<Item> = itemRepository.findItemByName(companyId = companySaved.id, name = name)
        return items.map { item -> parseObject(item, ItemResponseVO::class.java) }
    }

    @Transactional(readOnly = true)
    fun findItemById(
        user: User,
        idItem: Long
    ): ItemResponseVO {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val itemSaved: Item? = itemRepository.findItemById(companyId = companySaved.id, itemId = idItem)
        if (itemSaved != null) {
            return parseObject(itemSaved, ItemResponseVO::class.java)
        } else {
            throw ResourceNotFoundException(message = ITEM_NOT_FOUND)
        }
    }

    fun getItem(
        user: User,
        itemId: Long
    ): Item {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val itemSaved: Item? = itemRepository.findItemById(companyId = companySaved.id, itemId = itemId)
        if (itemSaved != null) {
            return itemSaved
        } else {
            throw ResourceNotFoundException(ITEM_NOT_FOUND)
        }
    }

    @Transactional
    fun createNewItem(
        user: User,
        item: ItemRequestVO
    ): ItemResponseVO {
        if (!checkNameItemAlreadyExists(user = user, name = item.name)) {
            val itemResult: Item = parseObject(item, Item::class.java)
            itemResult.company = companyService.getCompanyByUserLogged(user = user)
            return parseObject(itemRepository.save(itemResult), ItemResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_ITEM)
        }
    }

    private fun checkNameItemAlreadyExists(
        user: User,
        name: String
    ): Boolean {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val itemResult = itemRepository.checkNameItemAlreadyExists(companyId = companySaved.id, name = name)
        return itemResult != null
    }

    @Transactional
    fun saveObjectItem(
        user: User,
        order: Order? = null,
        itemRequest: ObjectRequestVO
    ): Pair<Object, Double> {
        var total = 0.0
        val productSaved = getItem(user = user, itemId = itemRequest.identifier)
        val objectItemResult: Object = parseObject(itemRequest, Object::class.java)
        objectItemResult.identifier = itemRequest.identifier
        objectItemResult.type = itemRequest.type
        objectItemResult.name = productSaved.name
        objectItemResult.price = productSaved.price
        objectItemResult.quantity = itemRequest.quantity
        val priceCalculated = (productSaved.price * itemRequest.quantity)
        objectItemResult.total = priceCalculated
        objectItemResult.status = ObjectStatus.PENDING
        objectItemResult.order = order
        total += priceCalculated
        return Pair(objectItemResult, total)
    }

    @Transactional
    fun updateItem(
        user: User,
        item: ItemResponseVO
    ): ItemResponseVO {
        if (!checkNameItemAlreadyExists(user = user, name = item.name)) {
            val itemSaved: Item = getItem(user = user, itemId = item.id)
            itemSaved.name = item.name
            itemSaved.price = item.price
            return parseObject(itemRepository.save(itemSaved), ItemResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_ITEM)
        }
    }

    @Transactional
    fun updatePriceItem(
        user: User,
        itemId: Long,
        price: PriceRequestVO
    ) {
        val itemSaved = getItem(user = user, itemId = itemId)
        itemRepository.updateItemPrice(companyId = itemSaved.id, idItem = itemId, price = price.price)
    }

    @Transactional
    fun deleteItem(
        user: User,
        itemId: Long
    ) {
        val itemSaved: Item = getItem(user = user, itemId = itemId)
        itemRepository.deleteItemById(companyId = itemSaved.company?.id, itemId = itemSaved.id)
    }

    companion object {
        const val ITEM_NOT_FOUND = "Item not found!"
        const val DUPLICATE_NAME_ITEM = "The Item already exists"
    }
}
