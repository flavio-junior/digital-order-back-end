package br.com.digital.order.vo.fee

import br.com.digital.order.utils.common.Function

data class FeeResponseOrderVO(
    var id: Long = 0,
    var percentage: Int = 0,
    var assigned: Function? = null,
    var author: AuthorResponseVO? = null
)
