package br.com.digital.order.vo.`object`

import br.com.digital.order.utils.common.Action
import br.com.digital.order.utils.common.ObjectStatus

data class UpdateObjectRequestVO(
    var action: Action,
    var overview: Long = 0,
    val status: ObjectStatus? = null,
    var quantity: Int = 0
)
