package br.com.dashboard.company.utils.others

import br.com.dashboard.company.utils.others.ConstantsUtils.FROM
import br.com.dashboard.company.utils.others.ConstantsUtils.UNTIL
import kotlin.random.Random

fun generateCode(): Long {
    return Random.nextLong(from = FROM, until = UNTIL)
}
