package br.com.digital.order.service

import br.com.digital.order.entities.`object`.Object
import br.com.digital.order.entities.`object`.Overview
import br.com.digital.order.entities.order.Order
import br.com.digital.order.entities.product.Product
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.ObjectDuplicateException
import br.com.digital.order.exceptions.ResourceNotFoundException
import br.com.digital.order.exceptions.handler.InvalidRequest
import br.com.digital.order.repository.ProductRepository
import br.com.digital.order.utils.common.ObjectStatus
import br.com.digital.order.utils.common.PriceRequestVO
import br.com.digital.order.utils.others.ConstantsUtils.ZERO_QUANTITY_ERROR
import br.com.digital.order.utils.others.ConverterUtils.parseObject
import br.com.digital.order.vo.`object`.ObjectRequestVO
import br.com.digital.order.vo.product.ProductRequestVO
import br.com.digital.order.vo.product.ProductResponseVO
import br.com.digital.order.vo.product.RestockProductRequestVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ProductService {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var categoryService: CategoryService

    @Autowired
    private lateinit var companyService: CompanyService

    @Transactional(readOnly = true)
    fun findAllProducts(
        user: User,
        name: String?,
        pageable: Pageable
    ): Page<ProductResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val products: Page<Product>? =
            productRepository.findAllProducts(companyId = companySaved.id, name = name, pageable = pageable)
        return products?.map { product -> parseObject(product, ProductResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = PRODUCT_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findProductByName(
        user: User,
        name: String
    ): List<ProductResponseVO> {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val products: List<Product> = productRepository.findProductByName(companyId = companySaved.id, name = name)
        return products.map { product -> parseObject(product, ProductResponseVO::class.java) }
    }

    @Transactional(readOnly = true)
    fun findProductById(
        user: User,
        productId: Long
    ): ProductResponseVO {
        val product = getProduct(user = user, productId = productId)
        return parseObject(product, ProductResponseVO::class.java)
    }

    fun getProduct(
        user: User,
        productId: Long
    ): Product {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val productSaved: Product? = productRepository.findProductById(companyId = companySaved.id, productId = productId)
        if (productSaved != null) {
            return productSaved
        } else {
            throw ResourceNotFoundException(PRODUCT_NOT_FOUND)
        }
    }

    @Transactional
    fun createNewProduct(
        user: User,
        product: ProductRequestVO
    ): ProductResponseVO {
        if (!checkNameProductAlreadyExists(user = user, name = product.name)) {
            val productResult: Product = parseObject(product, Product::class.java)
            productResult.categories =
                categoryService.converterCategories(user = user, categories = product.categories)
            productResult.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
            productResult.company = companyService.getCompanyByUserLogged(user = user)
            return parseObject(productRepository.save(productResult), ProductResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_PRODUCT)
        }
    }

    private fun checkNameProductAlreadyExists(
        user: User,
        name: String
    ): Boolean {
        val companySaved = companyService.getCompanyByUserLogged(user = user)
        val productResult = productRepository.checkNameProductAlreadyExists(companyId = companySaved.id, name = name)
        return productResult != null
    }

    @Transactional
    fun buyProduct(
        user: User,
        order: Order? = null,
        buy: Boolean = false,
        productRequest: ObjectRequestVO
    ): Pair<Object, Double> {
        var total = 0.0
        val productSaved = getProduct(user = user, productId = productRequest.identifier)
        checkBodyProduct(product = productSaved, quantityRequest = productRequest.quantity)
        val objectProductResult: Object = parseObject(productRequest, Object::class.java)
        objectProductResult.identifier = productRequest.identifier
        objectProductResult.type = productRequest.type
        objectProductResult.name = productSaved.name
        objectProductResult.price = productSaved.price
        objectProductResult.quantity = productRequest.quantity
        val priceCalculated = (productSaved.price * productRequest.quantity)
        objectProductResult.total = priceCalculated
        if (buy) {
            objectProductResult.status = ObjectStatus.DELIVERED
        } else {
            objectProductResult.status = ObjectStatus.PENDING
            objectProductResult.overview = arrayListOf(
                Overview(
                    createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    status = ObjectStatus.PENDING,
                    quantity = productRequest.quantity
                )
            )
        }
        objectProductResult.order = order
        total += priceCalculated
        productRepository.buyProduct(
            companyId = user.id,
            productId = productSaved.id,
            quantity = productRequest.quantity
        )
        return Pair(objectProductResult, total)
    }

    fun decrementProduct(
        user: User,
        productId: Long,
        quantity: Int
    ) {
        val productSaved = getProduct(user = user, productId = productId)
        productRepository.buyProduct(companyId = productSaved.company?.id, productId = productId, quantity = quantity)
    }

    fun checkBodyProduct(
        product: Product,
        quantityRequest: Int
    ) {
        if (quantityRequest == 0 || quantityRequest < 0) {
            throw InvalidRequest(message = ZERO_QUANTITY_ERROR)
        } else if (product.quantity == 0) {
            throw ObjectDuplicateException(message = PRODUCT_WITH_EMPTY_STOCK)
        } else if (quantityRequest > (product.quantity ?: 0)) {
            throw InvalidRequest(message = REQUESTED_QUANTITY_EXCEEDS_STOCK)
        }
    }

    @Transactional
    fun updateProduct(
        user: User,
        product: ProductResponseVO
    ): ProductResponseVO {
        if (!checkNameProductAlreadyExists(user = user, name = product.name)) {
            val productSaved: Product = getProduct(user = user, productId = product.id)
            productSaved.name = product.name
            productSaved.categories?.clear()
            productSaved.categories =
                categoryService.converterCategories(user = user, categories = product.categories)
            productSaved.price = product.price
            productSaved.quantity = product.quantity
            return parseObject(productRepository.save(productSaved), ProductResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_PRODUCT)
        }
    }

    @Transactional
    fun updatePriceProduct(
        user: User,
        productId: Long,
        price: PriceRequestVO
    ) {
        val productSaved = getProduct(user = user, productId = productId)
        productRepository.updatePriceProduct(companyId = productSaved.company?.id, idProduct = productSaved.id, price = price.price)
    }

    @Transactional
    fun restockProduct(
        user: User,
        productId: Long,
        restockProduct: RestockProductRequestVO
    ) {
        val productSaved = getProduct(user = user, productId = productId)
        productRepository.restockProduct(
            companyId = productSaved.company?.id,
            idProduct = productSaved.id,
            quantity = restockProduct.quantity
        )
    }

    @Transactional
    fun deleteProduct(
        user: User,
        productId: Long
    ) {
        val productSaved = getProduct(user = user, productId = productId)
        productSaved.categories = null
        productRepository.deleteProductById(companyId = productSaved.company?.id, productId = productId)
    }

    companion object {
        const val PRODUCT_NOT_FOUND = "Product not found!"
        const val PRODUCT_WITH_EMPTY_STOCK = "Product with empty stock!"
        const val REQUESTED_QUANTITY_EXCEEDS_STOCK = "The requested quantity exceeds the available stock."
        const val DUPLICATE_NAME_PRODUCT = "The product already exists"
    }
}
