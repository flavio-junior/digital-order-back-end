package br.com.dashboard.company.service

import br.com.dashboard.company.entities.company.Company
import br.com.dashboard.company.entities.item.Item
import br.com.dashboard.company.entities.`object`.Object
import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ObjectDuplicateException
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.ItemRepository
import br.com.dashboard.company.utils.common.ObjectStatus
import br.com.dashboard.company.utils.common.PriceRequestVO
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.item.ItemRequestVO
import br.com.dashboard.company.vo.item.ItemResponseVO
import br.com.dashboard.company.vo.`object`.ObjectRequestVO
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
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = user.id)
        val items: Page<Item>? = itemRepository.findAllItems(companyId = companySaved.id, name = name, pageable = pageable)
        return items?.map { reservation -> parseObject(reservation, ItemResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = ITEM_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findItemByName(
        user: User,
        name: String
    ): List<ItemResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = user.id)
        val items: List<Item> = itemRepository.findItemByName(companyId = companySaved.id, name = name)
        return items.map { item -> parseObject(item, ItemResponseVO::class.java) }
    }

    @Transactional(readOnly = true)
    fun findItemById(
        user: User,
        idItem: Long
    ): ItemResponseVO {
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = user.id)
        val itemSaved: Item? = itemRepository.findItemById(companyId = companySaved.id, itemId = idItem)
        if (itemSaved != null) {
            return parseObject(itemSaved, ItemResponseVO::class.java)
        } else {
            throw ResourceNotFoundException(message = ITEM_NOT_FOUND)
        }
    }

    fun getItem(
        userId: Long,
        itemId: Long
    ): Item {
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = userId)
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
        if (!checkNameItemAlreadyExists(userId = user.id, name = item.name)) {
            val itemResult: Item = parseObject(item, Item::class.java)
            itemResult.company = companyService.getCompanyByUserLogged(userLoggedId = user.id)
            return parseObject(itemRepository.save(itemResult), ItemResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_ITEM)
        }
    }

    private fun checkNameItemAlreadyExists(
        userId: Long,
        name: String
    ): Boolean {
        val companySaved = companyService.getCompanyByUserLogged(userLoggedId = userId)
        val itemResult = itemRepository.checkNameItemAlreadyExists(companyId = companySaved.id, name = name)
        return itemResult != null
    }

    @Transactional
    fun saveObjectItem(
        company: Company? = null,
        order: Order? = null,
        itemRequest: ObjectRequestVO
    ): Pair<Object, Double> {
        var total = 0.0
        val productSaved = getItem(userId = company?.id ?: 0, itemId = itemRequest.identifier)
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
        productSaved.company = company
        total += priceCalculated
        return Pair(objectItemResult, total)
    }

    @Transactional
    fun updateItem(
        user: User,
        item: ItemResponseVO
    ): ItemResponseVO {
        if (!checkNameItemAlreadyExists(userId = user.id, name = item.name)) {
            val itemSaved: Item = getItem(userId = user.id, itemId = item.id)
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
        val itemSaved = getItem(userId = user.id, itemId = itemId)
        itemRepository.updateItemPrice(companyId = itemSaved.id, idItem = itemId, price = price.price)
    }

    @Transactional
    fun deleteItem(
        userId: Long,
        itemId: Long
    ) {
        val itemSaved: Item = getItem(userId = userId, itemId = itemId)
        itemRepository.deleteItemById(companyId = itemSaved.company?.id, itemId = itemSaved.id)
    }

    companion object {
        const val ITEM_NOT_FOUND = "Item not found!"
        const val DUPLICATE_NAME_ITEM = "The Item already exists"
    }
}
