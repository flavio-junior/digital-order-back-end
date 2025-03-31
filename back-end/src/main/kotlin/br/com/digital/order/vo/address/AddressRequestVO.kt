package br.com.digital.order.vo.address

import br.com.digital.order.utils.common.AddressStatus

data class AddressRequestVO(
    var status: AddressStatus = AddressStatus.PENDING_DELIVERY,
    var street: String = "",
    var number: Int? = null,
    var district: String = "",
    var complement: String = ""
)
