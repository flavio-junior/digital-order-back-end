package br.com.dashboard.company.vo.`object`

import br.com.dashboard.company.utils.common.ObjectStatus
import com.fasterxml.jackson.annotation.JsonProperty

data class OverviewResponseVO(
    var id: Long = 0,
    @JsonProperty(value = "created_at")
    var createdAt: String? = null,
    var status: ObjectStatus? = null,
    var quantity: Int = 0
)
