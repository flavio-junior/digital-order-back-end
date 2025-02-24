package br.com.digital.order.vo.`object`

import br.com.digital.order.utils.common.TypeItem
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

data class ObjectRequestVO(
    var identifier: Long = 0,
    var name: String = "",
    @Enumerated(EnumType.STRING)
    var type: TypeItem? = null,
    var quantity: Int = 0
)
