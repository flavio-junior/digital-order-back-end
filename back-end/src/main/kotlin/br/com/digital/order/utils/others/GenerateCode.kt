package br.com.digital.order.utils.others

import br.com.digital.order.utils.others.ConstantsUtils.FROM
import br.com.digital.order.utils.others.ConstantsUtils.UNTIL
import kotlin.random.Random

fun generateCode(): Long {
    return Random.nextLong(from = FROM, until = UNTIL)
}
