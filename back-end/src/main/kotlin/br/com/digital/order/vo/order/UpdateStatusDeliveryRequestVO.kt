package br.com.digital.order.vo.order

import br.com.digital.order.utils.common.AddressStatus

data class UpdateStatusDeliveryRequestVO(
    var status: AddressStatus
)
