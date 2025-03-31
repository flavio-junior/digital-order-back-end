package br.com.digital.order.vo.food

import br.com.digital.order.vo.category.CategoryResponseVO

data class FoodRequestVO(
    var name: String = "",
    var categories: MutableList<CategoryResponseVO>? = null,
    var price: Double = 0.0
)
