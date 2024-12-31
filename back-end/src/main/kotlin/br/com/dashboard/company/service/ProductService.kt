package br.com.dashboard.company.service

import br.com.dashboard.company.entities.`object`.Object
import br.com.dashboard.company.entities.order.Order
import br.com.dashboard.company.entities.product.Product
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ObjectDuplicateException
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.exceptions.handler.InvalidRequest
import br.com.dashboard.company.repository.ProductRepository
import br.com.dashboard.company.utils.common.ObjectStatus
import br.com.dashboard.company.utils.common.PriceRequestVO
import br.com.dashboard.company.utils.others.ConstantsUtils.ZERO_QUANTITY_ERROR
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.`object`.ObjectRequestVO
import br.com.dashboard.company.vo.product.ProductRequestVO
import br.com.dashboard.company.vo.product.ProductResponseVO
import br.com.dashboard.company.vo.product.RestockProductRequestVO
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
    private lateinit var userService: UserService

    @Transactional(readOnly = true)
    fun findAllProducts(
        user: User,
        name: String?,
        pageable: Pageable
    ): Page<ProductResponseVO> {
        val products: Page<Product>? =
            productRepository.findAllProducts(userId = user.id, name = name, pageable = pageable)
        return products?.map { product -> parseObject(product, ProductResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = PRODUCT_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findProductByName(
        user: User,
        name: String
    ): List<ProductResponseVO> {
        val products: List<Product> = productRepository.findProductByName(userId = user.id, name = name)
        return products.map { product -> parseObject(product, ProductResponseVO::class.java) }
    }

    @Transactional(readOnly = true)
    fun findProductById(
        user: User,
        productId: Long
    ): ProductResponseVO {
        val product = getProduct(userId = user.id, productId = productId)
        return parseObject(product, ProductResponseVO::class.java)
    }

    fun getProduct(
        userId: Long,
        productId: Long
    ): Product {
        val productSaved: Product? = productRepository.findProductById(userId = userId, productId = productId)
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
        if (!checkNameProductAlreadyExists(userId = user.id, name = product.name)) {
            val userAuthenticated = userService.findUserById(userId = user.id)
            val productResult: Product = parseObject(product, Product::class.java)
            productResult.categories =
                categoryService.converterCategories(userId = user.id, categories = product.categories)
            productResult.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
            productResult.user = userAuthenticated
            return parseObject(productRepository.save(productResult), ProductResponseVO::class.java)
        } else {
            throw ObjectDuplicateException(message = DUPLICATE_NAME_PRODUCT)
        }
    }

    private fun checkNameProductAlreadyExists(
        userId: Long,
        name: String
    ): Boolean {
        val productResult = productRepository.checkNameProductAlreadyExists(userId = userId, name = name)
        return productResult != null
    }

    @Transactional
    fun buyProduct(
        user: User? = null,
        order: Order? = null,
        productRequest: ObjectRequestVO
    ): Pair<Object, Double> {
        var total = 0.0
        val productSaved = getProduct(userId = user?.id ?: 0, productId = productRequest.identifier)
        checkBodyProduct(product = productSaved, quantityRequest = productRequest.quantity)
        val objectProductResult: Object = parseObject(productRequest, Object::class.java)
        objectProductResult.identifier = productRequest.identifier
        objectProductResult.type = productRequest.type
        objectProductResult.name = productSaved.name
        objectProductResult.price = productSaved.price
        objectProductResult.quantity = productRequest.quantity
        val priceCalculated = (productSaved.price * productRequest.quantity)
        objectProductResult.total = priceCalculated
        objectProductResult.status = ObjectStatus.PENDING
        objectProductResult.order = order
        productSaved.user = user
        total += priceCalculated
        productRepository.buyProduct(userId = user?.id ?: 0, productId = productSaved.id, quantity = productRequest.quantity)
        return Pair(objectProductResult, total)
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
        if (!checkNameProductAlreadyExists(userId = user.id, name = product.name)) {
            val productSaved: Product = getProduct(userId = user.id, productId = product.id)
            productSaved.name = product.name
            productSaved.categories?.clear()
            productSaved.categories =
                categoryService.converterCategories(userId = user.id, categories = product.categories)
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
        val productSaved = getProduct(userId = user.id, productId = productId)
        productRepository.updatePriceProduct(userId = user.id, idProduct = productSaved.id, price = price.price)
    }

    @Transactional
    fun restockProduct(
        user: User,
        productId: Long,
        restockProduct: RestockProductRequestVO
    ) {
        val productSaved = getProduct(userId = user.id, productId = productId)
        productRepository.restockProduct(
            userId = user.id,
            idProduct = productSaved.id,
            quantity = restockProduct.quantity
        )
    }

    @Transactional
    fun deleteProduct(
        user: User,
        productId: Long
    ) {
        val productSaved = getProduct(userId = user.id, productId = productId)
        productSaved.categories = null
        productRepository.deleteProductById(userId = user.id, productId = productId)
    }

    companion object {
        const val PRODUCT_NOT_FOUND = "Product not found!"
        const val PRODUCT_WITH_EMPTY_STOCK = "Product with empty stock!"
        const val REQUESTED_QUANTITY_EXCEEDS_STOCK = "The requested quantity exceeds the available stock."
        const val DUPLICATE_NAME_PRODUCT = "The product already exists"
    }
}
