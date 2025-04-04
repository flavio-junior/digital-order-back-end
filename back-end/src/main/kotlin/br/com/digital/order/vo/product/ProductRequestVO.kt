package br.com.digital.order.vo.product

import br.com.digital.order.vo.category.CategoryResponseVO

data class ProductRequestVO(
    var name: String = "",
    var categories: MutableList<CategoryResponseVO>? = null,
    var price: Double = 0.0,
    var quantity: Int = 0
)
