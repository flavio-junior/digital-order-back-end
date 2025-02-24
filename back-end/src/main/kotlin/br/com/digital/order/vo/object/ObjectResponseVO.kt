package br.com.digital.order.vo.`object`

import br.com.digital.order.utils.common.ObjectStatus
import br.com.digital.order.utils.common.TypeItem

data class ObjectResponseVO(
    var id: Long = 0,
    var identifier: Long = 0,
    var type: TypeItem? = null,
    var name: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var total: Double = 0.0,
    var status: ObjectStatus? = null,
    var overview: MutableList<OverviewResponseVO>? = null
)
