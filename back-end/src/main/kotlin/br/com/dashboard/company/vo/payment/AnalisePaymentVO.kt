package br.com.dashboard.company.vo.payment

import br.com.dashboard.company.utils.common.PaymentType
import br.com.dashboard.company.utils.common.TypeOrder
import com.fasterxml.jackson.annotation.JsonProperty

data class AnalisePaymentVO(
    val analise: List<PaymentSummaryDTO>,
    val total: Double,
    val discount: Long
)

data class PaymentSummaryDTO(
    @JsonProperty(value = "type_order")
    val typeOrder: TypeOrder,
    @JsonProperty(value = "type_payment")
    val typePayment: PaymentType,
    val count: Long,
    val total: Double
)
