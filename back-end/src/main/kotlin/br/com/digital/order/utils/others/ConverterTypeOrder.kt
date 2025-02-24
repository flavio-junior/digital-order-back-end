package br.com.digital.order.utils.others

import br.com.digital.order.utils.common.TypeItem

fun converterTypeOrder(
    type: String? = ""
): TypeItem? {
    return when (type) {
        TypeItem.FOOD.name -> TypeItem.FOOD
        TypeItem.PRODUCT.name -> TypeItem.PRODUCT
        else -> null
    }
}
