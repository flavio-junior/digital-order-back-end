package br.com.dashboard.company.vo.payment

import br.com.dashboard.company.utils.common.PaymentType
import br.com.dashboard.company.utils.common.TypeOrder
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
