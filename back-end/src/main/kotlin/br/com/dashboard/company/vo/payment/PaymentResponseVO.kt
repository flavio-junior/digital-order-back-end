package br.com.dashboard.company.vo.payment

import br.com.dashboard.company.utils.common.PaymentType
import br.com.dashboard.company.utils.common.TypeOrder
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column

data class PaymentResponseVO(
    var id: Long = 0,
    var date: String? = null,
    var hour: String? = null,
    var code: Long? = null,
    @JsonProperty(value = "type_order")
    var typeOrder: TypeOrder? = null,
    @JsonProperty(value = "type_payment")
    var typePayment: PaymentType? = null,
    var discount: Boolean? = null,
    @JsonProperty(value = "value_discount")
    var valueDiscount: Double? = null,
    var total: Double = 0.0
)
