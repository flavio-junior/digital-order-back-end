package br.com.dashboard.company.vo.fee

import br.com.dashboard.company.utils.common.Function

data class FeeResponseOrderVO(
    var id: Long = 0,
    var price: Double = 0.0,
    var assigned: Function? = null,
    var author: AuthorResponseVO? = null
)
