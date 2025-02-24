package br.com.digital.order.vo.payment

import br.com.digital.order.utils.common.PaymentType

data class PaymentRequestVO(
    var type: PaymentType? = null,
    var discount: Boolean? = null,
    var value: Double? = null,
    var remove: Boolean = false
)
