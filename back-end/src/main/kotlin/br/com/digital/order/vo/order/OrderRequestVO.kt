package br.com.digital.order.vo.order

import br.com.digital.order.utils.common.TypeOrder
import br.com.digital.order.vo.address.AddressRequestVO
import br.com.digital.order.vo.fee.RequestFeeVO
import br.com.digital.order.vo.`object`.ObjectRequestVO
import br.com.digital.order.vo.payment.PaymentRequestVO
import br.com.digital.order.vo.reservation.ReservationResponseVO

data class OrderRequestVO(
    var type: TypeOrder? = null,
    var reservations: MutableList<ReservationResponseVO>? = null,
    var fee: RequestFeeVO? = null,
    var address: AddressRequestVO? = null,
    var objects: MutableList<ObjectRequestVO>? = null,
    var observation: String? = null,
    var payment: PaymentRequestVO? = null
)
