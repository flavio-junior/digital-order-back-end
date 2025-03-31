package br.com.digital.order.vo.payment

import br.com.digital.order.utils.common.PaymentType
import br.com.digital.order.utils.common.TypeOrder
import com.fasterxml.jackson.annotation.JsonProperty

data class AnaliseDayVO(
    val content: List<TypePaymentVO>,
    @JsonProperty(value = "number_orders")
    val numberOrders: Long,
    val total: Double,
    val discount: Long
)

data class TypePaymentVO(
    @JsonProperty(value = "type_order")
    val typeOrder: TypeOrder,
    val analise: List<DescriptionPaymentVO>,
    @JsonProperty(value = "number_orders")
    val numberOrders: Long,
    val total: Double,
    val discount: Long
)

data class DescriptionPaymentVO(
    @JsonProperty(value = "type_order")
    val typeOrder: TypeOrder,
    @JsonProperty(value = "type_payment")
    val typePayment: PaymentType,
    @JsonProperty(value = "number_items")
    val numberItems: Long,
    val total: Double,
    val discount: Long
)
