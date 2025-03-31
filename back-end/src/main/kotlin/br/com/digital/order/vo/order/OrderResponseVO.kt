package br.com.digital.order.vo.order

import br.com.digital.order.utils.common.Status
import br.com.digital.order.utils.common.TypeOrder
import br.com.digital.order.vo.address.AddressResponseVO
import br.com.digital.order.vo.fee.FeeResponseOrderVO
import br.com.digital.order.vo.`object`.ObjectResponseVO
import br.com.digital.order.vo.payment.PaymentResponseVO
import br.com.digital.order.vo.reservation.ReservationResponseVO
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class OrderResponseVO(
    var id: Long = 0,
    @JsonProperty(value = "created_at")
    var createdAt: LocalDateTime? = null,
    @JsonProperty(value = "qr_code")
    var qrCode: String? = null,
    var type: TypeOrder? = null,
    var status: Status? = null,
    var reservations: MutableList<ReservationResponseVO>? = null,
    var fee: FeeResponseOrderVO? = null,
    var address: AddressResponseVO? = null,
    var objects: MutableList<ObjectResponseVO>? = null,
    var quantity: Int = 0,
    var total: Double = 0.0,
    var observation: String? = null,
    var payment: PaymentResponseVO? = null
)
