package br.com.digital.order.vo.payment

import br.com.digital.order.utils.common.PaymentType
import br.com.digital.order.utils.common.TypeOrder
import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentResponseVO(
    var id: Long = 0,
    var date: String? = null,
    var hour: String? = null,
    var code: Long? = null,
    var author: String? = "",
    var assigned: String? = "",
    @JsonProperty(value = "type_order")
    var typeOrder: TypeOrder? = null,
    @JsonProperty(value = "type_payment")
    var typePayment: PaymentType? = null,
    var discount: Boolean? = null,
    @JsonProperty(value = "value_discount")
    var valueDiscount: Double? = null,
    var fee: Boolean? = null,
    @JsonProperty(value = "value_fee")
    var valueFee: Double? = null,
    var total: Double = 0.0
)
