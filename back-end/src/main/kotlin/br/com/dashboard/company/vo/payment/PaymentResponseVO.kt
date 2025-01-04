package br.com.dashboard.company.vo.payment

import br.com.dashboard.company.utils.common.PaymentType
import br.com.dashboard.company.utils.common.TypeOrder
import jakarta.persistence.Column

data class PaymentResponseVO(
    var id: Long = 0,
    var date: String? = null,
    var hour: String? = null,
    var code: Long? = null,
    @Column(name = "type_order", nullable = false)
    var typeOrder: TypeOrder? = null,
    @Column(name = "type_payment", nullable = false)
    var typePayment: PaymentType? = null,
    var discount: Boolean? = null,
    @Column(name = "value_discount", nullable = true)
    var valueDiscount: Double? = null,
    var total: Double = 0.0
)
