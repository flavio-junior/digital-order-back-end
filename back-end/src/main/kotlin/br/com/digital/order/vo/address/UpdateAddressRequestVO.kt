package br.com.digital.order.vo.address

data class UpdateAddressRequestVO(
    var street: String = "",
    var number: Int? = null,
    var district: String = "",
    var complement: String = ""
)
